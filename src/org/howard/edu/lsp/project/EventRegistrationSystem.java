package org.howard.edu.lsp.project;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.howard.edu.lsp.project.EventRegistrationException.Reason;

/**
 * Manages in-memory events, attendees, and registrations for one application.
 * Records are immutable snapshots; all mutations pass through this service.
 * Synchronized operations make seat checks and updates atomic within this JVM.
 * A trusted adapter must authenticate callers before supplying a user role.
 */
public final class EventRegistrationSystem {
    /** Event snapshots indexed by unique identifier. */
    private final Map<String, Event> events = new LinkedHashMap<>();
    /** Attendee records indexed by unique identifier. */
    private final Map<String, Attendee> attendees = new LinkedHashMap<>();
    /** Registration snapshots indexed by generated identifier. */
    private final Map<String, Registration> registrations = new LinkedHashMap<>();
    /** Time source used for timestamps and editing boundaries. */
    private final Clock clock;
    /** Next unused registration sequence number within this system. */
    private long nextRegistrationId = 1;

    /** Creates an empty system using the UTC system clock. */
    public EventRegistrationSystem() {
        this(Clock.systemUTC());
    }

    /**
     * Creates an empty system with a controllable time source.
     * @param clock time source for registrations and event editing
     * @throws EventRegistrationException if the clock is null
     */
    public EventRegistrationSystem(Clock clock) {
        require(clock != null, Reason.INVALID_INPUT, "Clock is required.");
        this.clock = clock;
    }

    /**
     * Creates an OPEN event after checking the caller's trusted role.
     * @param role role supplied by a trusted, authenticated adapter
     * @param id unique, nonblank event identifier
     * @param name nonblank event name
     * @param description nonblank event description
     * @param location nonblank event location
     * @param dateTime event start instant
     * @param capacity positive maximum attendance
     * @return the created event
     * @throws EventRegistrationException for unauthorized callers, duplicate IDs,
     *         or invalid event values
     */
    public synchronized Event createEvent(UserRole role, String id, String name,
            String description, String location, Instant dateTime, int capacity) {
        requireOrganizer(role);
        requireText(id, "Event identifier");
        require(!events.containsKey(id), Reason.DUPLICATE_ID, "Event identifier already exists.");
        validateEventDetails(name, description, location, dateTime, capacity);
        Event event = new Event(id, name, description, location, dateTime, capacity, EventStatus.OPEN);
        events.put(id, event);
        return event;
    }

    /**
     * Retrieves an event snapshot.
     * @param id event identifier
     * @return the matching event
     * @throws EventRegistrationException if the ID is invalid or does not exist
     */
    public synchronized Event getEvent(String id) {
        requireText(id, "Event identifier");
        Event event = events.get(id);
        require(event != null, Reason.NOT_FOUND, "Event not found: " + id);
        return event;
    }

    /**
     * Replaces event details before its existing start time, preserving ID and status.
     * The new start must also be in the future; capacity cannot remove reserved seats.
     * @param role trusted caller role
     * @param id existing event identifier
     * @param name replacement event name
     * @param description replacement description
     * @param location replacement location
     * @param dateTime replacement start instant
     * @param capacity replacement maximum attendance
     * @return the updated event snapshot
     * @throws EventRegistrationException for unauthorized callers, invalid or missing
     *         records, canceled events, started events, or insufficient capacity
     */
    public synchronized Event updateEvent(UserRole role, String id, String name,
            String description, String location, Instant dateTime, int capacity) {
        requireOrganizer(role);
        Event event = getEvent(id);
        require(event.getStatus() != EventStatus.CANCELED, Reason.INVALID_EVENT_STATUS,
                "Canceled events cannot be edited.");
        Instant now = clock.instant();
        require(now.isBefore(event.getDateTime()), Reason.EVENT_STARTED,
                "Event details cannot be edited at or after the start time.");
        validateEventDetails(name, description, location, dateTime, capacity);
        require(now.isBefore(dateTime), Reason.INVALID_INPUT, "New event start must be in the future.");
        require(capacity >= countActive(id), Reason.CAPACITY_TOO_SMALL,
                "Capacity cannot be smaller than the active registration count.");
        Event updated = new Event(id, name, description, location, dateTime, capacity, event.getStatus());
        events.put(id, updated);
        return updated;
    }

    /**
     * Closes an event without removing its current registrations; closing twice is harmless.
     * @param role trusted caller role
     * @param id event identifier
     * @return the closed event snapshot
     * @throws EventRegistrationException for unauthorized callers, invalid or missing
     *         identifiers, or a canceled event
     */
    public synchronized Event closeEvent(UserRole role, String id) {
        requireOrganizer(role);
        Event event = getEvent(id);
        require(event.getStatus() != EventStatus.CANCELED, Reason.INVALID_EVENT_STATUS,
                "A canceled event cannot be closed.");
        return replaceEventStatus(event, EventStatus.CLOSED);
    }

    /**
     * Cancels an event and all its active registrations; repeating is harmless.
     * @param role trusted caller role
     * @param id event identifier
     * @return the canceled event snapshot
     * @throws EventRegistrationException for unauthorized callers or invalid or missing IDs
     */
    public synchronized Event cancelEvent(UserRole role, String id) {
        requireOrganizer(role);
        Event event = getEvent(id);
        for (Map.Entry<String, Registration> entry : registrations.entrySet()) {
            Registration registration = entry.getValue();
            if (registration.getEventId().equals(id)
                    && registration.getStatus() == RegistrationStatus.ACTIVE) {
                entry.setValue(canceledSnapshot(registration));
            }
        }
        return replaceEventStatus(event, EventStatus.CANCELED);
    }

    /**
     * Creates an attendee record.
     * @param id unique, nonblank attendee identifier
     * @param firstName nonblank first name
     * @param lastName nonblank last name
     * @param email nonblank email address (full address verification is outside this library)
     * @return the created attendee
     * @throws EventRegistrationException for duplicate identifiers or missing values
     */
    public synchronized Attendee createAttendee(String id, String firstName, String lastName, String email) {
        requireText(id, "Attendee identifier");
        require(!attendees.containsKey(id), Reason.DUPLICATE_ID, "Attendee identifier already exists.");
        requireText(firstName, "First name");
        requireText(lastName, "Last name");
        requireText(email, "Email");
        Attendee attendee = new Attendee(id, firstName, lastName, email);
        attendees.put(id, attendee);
        return attendee;
    }

    /**
     * Retrieves an attendee record.
     * @param id attendee identifier
     * @return the matching attendee
     * @throws EventRegistrationException if the ID is invalid or does not exist
     */
    public synchronized Attendee getAttendee(String id) {
        requireText(id, "Attendee identifier");
        Attendee attendee = attendees.get(id);
        require(attendee != null, Reason.NOT_FOUND, "Attendee not found: " + id);
        return attendee;
    }

    /**
     * Reserves one seat in an OPEN event for an existing attendee.
     * One record per attendee-event pair is retained even after cancellation.
     * @param attendeeId existing attendee identifier
     * @param eventId existing event identifier
     * @return the new ACTIVE registration with a unique ID and creation time
     * @throws EventRegistrationException for invalid or missing IDs, a non-OPEN
     *         event, duplicate participation, or a full event
     */
    public synchronized Registration register(String attendeeId, String eventId) {
        Attendee attendee = getAttendee(attendeeId);
        Event event = getEvent(eventId);
        require(event.getStatus() == EventStatus.OPEN, Reason.INVALID_EVENT_STATUS,
                "Only OPEN events accept registrations.");
        for (Registration registration : registrations.values()) {
            require(!(registration.getAttendee().getId().equals(attendeeId)
                    && registration.getEventId().equals(eventId)), Reason.ALREADY_REGISTERED,
                    "Attendee already has a registration for this event.");
        }
        require(countActive(eventId) < event.getCapacity(), Reason.EVENT_FULL, "Event capacity has been reached.");
        String id = "REG-" + nextRegistrationId++;
        Registration registration = new Registration(id, attendee, eventId, clock.instant(), RegistrationStatus.ACTIVE);
        registrations.put(id, registration);
        return registration;
    }

    /**
     * Retrieves a registration, including canceled records.
     * @param id registration identifier
     * @return the matching registration snapshot
     * @throws EventRegistrationException if the ID is invalid or does not exist
     */
    public synchronized Registration getRegistration(String id) {
        requireText(id, "Registration identifier");
        Registration registration = registrations.get(id);
        require(registration != null, Reason.NOT_FOUND, "Registration not found: " + id);
        return registration;
    }

    /**
     * Cancels a registration and releases its seat; repeating is harmless.
     * @param id registration identifier
     * @return the canceled registration snapshot
     * @throws EventRegistrationException if the ID is invalid or does not exist
     */
    public synchronized Registration cancelRegistration(String id) {
        Registration canceled = canceledSnapshot(getRegistration(id));
        registrations.put(id, canceled);
        return canceled;
    }

    /**
     * Lists only attendees with ACTIVE registrations, in registration insertion order.
     * @param eventId existing event identifier
     * @return an unmodifiable snapshot of the selected event's active attendees
     * @throws EventRegistrationException if the ID is invalid or does not exist
     */
    public synchronized List<Attendee> getAttendeesForEvent(String eventId) {
        getEvent(eventId);
        List<Attendee> result = new ArrayList<>();
        for (Registration registration : registrations.values()) {
            if (registration.getEventId().equals(eventId)
                    && registration.getStatus() == RegistrationStatus.ACTIVE) {
                result.add(registration.getAttendee());
            }
        }
        return Collections.unmodifiableList(result);
    }

    /**
     * Lists only events for which the attendee has an ACTIVE registration.
     * @param attendeeId existing attendee identifier
     * @return an unmodifiable snapshot in registration insertion order
     * @throws EventRegistrationException if the ID is invalid or does not exist
     */
    public synchronized List<Event> getEventsForAttendee(String attendeeId) {
        getAttendee(attendeeId);
        List<Event> result = new ArrayList<>();
        for (Registration registration : registrations.values()) {
            if (registration.getAttendee().getId().equals(attendeeId)
                    && registration.getStatus() == RegistrationStatus.ACTIVE) {
                result.add(events.get(registration.getEventId()));
            }
        }
        return Collections.unmodifiableList(result);
    }

    /**
     * Counts seat reservations, excluding canceled registrations.
     * @param eventId event identifier
     * @return active registration count
     */
    private int countActive(String eventId) {
        int count = 0;
        for (Registration registration : registrations.values()) {
            if (registration.getEventId().equals(eventId)
                    && registration.getStatus() == RegistrationStatus.ACTIVE) {
                count++;
            }
        }
        return count;
    }

    /**
     * Stores a new event snapshot with the specified lifecycle status.
     * @param event existing event
     * @param status new status
     * @return replacement snapshot
     */
    private Event replaceEventStatus(Event event, EventStatus status) {
        Event updated = new Event(event.getId(), event.getName(), event.getDescription(),
                event.getLocation(), event.getDateTime(), event.getCapacity(), status);
        events.put(event.getId(), updated);
        return updated;
    }

    /**
     * Copies a registration while preserving its identity, associations, and timestamp.
     * @param registration existing registration
     * @return a CANCELED registration snapshot
     */
    private Registration canceledSnapshot(Registration registration) {
        return new Registration(registration.getId(), registration.getAttendee(),
                registration.getEventId(), registration.getTimestamp(), RegistrationStatus.CANCELED);
    }

    /**
     * Validates all editable event values before any mutation occurs.
     * @param name event name
     * @param description event description
     * @param location event location
     * @param dateTime event start instant
     * @param capacity maximum attendance
     * @throws EventRegistrationException if any value is invalid
     */
    private void validateEventDetails(String name, String description, String location,
            Instant dateTime, int capacity) {
        requireText(name, "Event name");
        requireText(description, "Event description");
        requireText(location, "Event location");
        require(dateTime != null, Reason.INVALID_INPUT, "Event start is required.");
        require(capacity > 0, Reason.INVALID_INPUT, "Capacity must be positive.");
    }

    /**
     * Checks the library's organizer-only management policy.
     * @param role caller role from a trusted adapter
     * @throws EventRegistrationException if the caller is not an organizer
     */
    private void requireOrganizer(UserRole role) {
        require(role == UserRole.ORGANIZER, Reason.UNAUTHORIZED, "Organizer role is required.");
    }

    /**
     * Rejects missing or whitespace-only text without normalizing identifiers.
     * @param value supplied text
     * @param label field name for the error message
     * @throws EventRegistrationException if the text is null or blank
     */
    private void requireText(String value, String label) {
        require(value != null && !value.isBlank(), Reason.INVALID_INPUT, label + " is required.");
    }

    /**
     * Throws the project's custom exception when a condition is false.
     * @param condition required condition
     * @param reason rejection reason
     * @param message explanation of the failed condition
     * @throws EventRegistrationException if the condition is false
     */
    private void require(boolean condition, Reason reason, String message) {
        if (!condition) {
            throw new EventRegistrationException(reason, message);
        }
    }
}
