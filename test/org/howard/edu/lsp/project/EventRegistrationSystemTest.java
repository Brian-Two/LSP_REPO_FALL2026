package org.howard.edu.lsp.project;

import static org.junit.jupiter.api.Assertions.*;
import static org.howard.edu.lsp.project.EventRegistrationException.Reason.*;
import static org.howard.edu.lsp.project.UserRole.*;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/** Demonstrates required behavior, business failures, boundaries, and atomic seat allocation. */
class EventRegistrationSystemTest {
    /** Constructs the JUnit test fixture. */
    EventRegistrationSystemTest() {
    }

    /** Fixed reference instant used by deterministic tests. */
    private static final Instant NOW = Instant.parse("2026-11-18T15:00:00Z");
    /** Event start one day after the test reference instant. */
    private static final Instant FUTURE = NOW.plusSeconds(86400);
    /** Fresh application instance for each test. */
    private EventRegistrationSystem system;

    /** Creates a deterministic system and two attendee records before each test. */
    @BeforeEach
    void setUp() {
        system = new EventRegistrationSystem(Clock.fixed(NOW, ZoneOffset.UTC));
        system.createAttendee("A1", "Brian", "Too", "brian@example.edu");
        system.createAttendee("A2", "Jordan", "Lee", "jordan@example.edu");
    }

    /** Checks that the default constructor supplies a real registration timestamp. */
    @Test
    void defaultClockWorks() {
        EventRegistrationSystem real = new EventRegistrationSystem();
        real.createAttendee("A", "First", "Last", "a@example.edu");
        real.createEvent(ORGANIZER, "E", "Name", "Description", "Location", FUTURE, 1);
        Instant before = Instant.now();
        Registration registration = real.register("A", "E");
        Instant after = Instant.now();
        assertFalse(registration.getTimestamp().isBefore(before));
        assertFalse(registration.getTimestamp().isAfter(after));
    }

    /** Checks that a null clock is rejected through the custom exception. */
    @Test
    void rejectsNullClock() {
        fails(INVALID_INPUT, () -> new EventRegistrationSystem(null));
    }

    /** Verifies every event field and retrieval of a newly OPEN event. */
    @Test
    void createsAndRetrievesEvent() {
        Event event = event("E1", 2);
        assertAll(
                () -> assertEquals("E1", event.getId()),
                () -> assertEquals("Campus event", event.getName()),
                () -> assertEquals("Student activities", event.getDescription()),
                () -> assertEquals("Armour J Blackburn Center", event.getLocation()),
                () -> assertEquals(FUTURE, event.getDateTime()),
                () -> assertEquals(2, event.getCapacity()),
                () -> assertEquals(EventStatus.OPEN, event.getStatus()),
                () -> assertSame(event, system.getEvent("E1")));
    }

    /** Checks authorization before event creation and verifies no record was inserted. */
    @Test
    void unauthorizedCallerCannotCreateEvent() {
        fails(UNAUTHORIZED, () -> system.createEvent(ATTENDEE, "E1", "Name", "Desc", "Place", FUTURE, 1));
        fails(UNAUTHORIZED, () -> system.createEvent(null, "E1", "Name", "Desc", "Place", FUTURE, 1));
        fails(NOT_FOUND, () -> system.getEvent("E1"));
    }

    /** Verifies duplicate event IDs do not overwrite the original. */
    @Test
    void rejectsDuplicateEventId() {
        Event original = event("E1", 2);
        fails(DUPLICATE_ID, () -> event("E1", 99));
        assertSame(original, system.getEvent("E1"));
    }

    /**
     * Verifies that every required event value is validated without inserting a record.
     * @param id candidate identifier
     * @param name candidate name
     * @param description candidate description
     * @param location candidate location
     * @param date candidate start time
     * @param capacity candidate capacity
     */
    @ParameterizedTest
    @MethodSource("invalidEvents")
    void rejectsInvalidEventValues(String id, String name, String description,
            String location, Instant date, int capacity) {
        fails(INVALID_INPUT, () -> system.createEvent(ORGANIZER, id, name, description, location, date, capacity));
        fails(NOT_FOUND, () -> system.getEvent("E1"));
    }

    /**
     * Returns null, empty, blank, date, and capacity cases for event validation.
     * @return null, empty, blank, date, and capacity cases for event validation
     */
    private static Stream<Arguments> invalidEvents() {
        List<Arguments> cases = new ArrayList<>();
        for (int field = 0; field < 4; field++) {
            for (String bad : new String[] {null, "", " \t"}) {
                String[] values = {"E1", "Name", "Description", "Location"};
                values[field] = bad;
                cases.add(Arguments.of(values[0], values[1], values[2], values[3], FUTURE, 1));
            }
        }
        cases.add(Arguments.of("E1", "Name", "Desc", "Place", null, 1));
        cases.add(Arguments.of("E1", "Name", "Desc", "Place", FUTURE, 0));
        cases.add(Arguments.of("E1", "Name", "Desc", "Place", FUTURE, -1));
        return cases.stream();
    }

    /** Verifies all attendee fields, retrieval, and independent attendee and event ID namespaces. */
    @Test
    void createsAndRetrievesAttendee() {
        Attendee attendee = system.getAttendee("A1");
        assertAll(
                () -> assertEquals("A1", attendee.getId()),
                () -> assertEquals("Brian", attendee.getFirstName()),
                () -> assertEquals("Too", attendee.getLastName()),
                () -> assertEquals("brian@example.edu", attendee.getEmail()));
        assertEquals("A1", event("A1", 1).getId());
    }

    /** Verifies duplicate attendee IDs do not overwrite the original. */
    @Test
    void rejectsDuplicateAttendeeId() {
        Attendee original = system.getAttendee("A1");
        fails(DUPLICATE_ID, () -> system.createAttendee("A1", "Other", "Person", "other@example.edu"));
        assertSame(original, system.getAttendee("A1"));
    }

    /**
     * Verifies null, empty, and blank attendee fields are rejected without insertion.
     * @param id candidate identifier
     * @param first candidate first name
     * @param last candidate last name
     * @param email candidate email address
     */
    @ParameterizedTest
    @MethodSource("invalidAttendees")
    void rejectsInvalidAttendeeValues(String id, String first, String last, String email) {
        fails(INVALID_INPUT, () -> system.createAttendee(id, first, last, email));
        fails(NOT_FOUND, () -> system.getAttendee("A3"));
    }

    /**
     * Returns invalid values for each required attendee field.
     * @return invalid values for each required attendee field
     */
    private static Stream<Arguments> invalidAttendees() {
        List<Arguments> cases = new ArrayList<>();
        for (int field = 0; field < 4; field++) {
            for (String bad : new String[] {null, "", " \t"}) {
                String[] values = {"A3", "First", "Last", "a@example.edu"};
                values[field] = bad;
                cases.add(Arguments.of((Object[]) values));
            }
        }
        return cases.stream();
    }

    /** Verifies changing every editable field preserves event identity and its old snapshot. */
    @Test
    void updatesFutureEvent() {
        Event original = event("E1", 2);
        Event updated = system.updateEvent(ORGANIZER, "E1", "New name", "New description",
                "New location", FUTURE.plusSeconds(3600), 3);
        assertAll(
                () -> assertEquals("E1", updated.getId()),
                () -> assertEquals("New name", updated.getName()),
                () -> assertEquals("New description", updated.getDescription()),
                () -> assertEquals("New location", updated.getLocation()),
                () -> assertEquals(FUTURE.plusSeconds(3600), updated.getDateTime()),
                () -> assertEquals(3, updated.getCapacity()),
                () -> assertEquals(EventStatus.OPEN, updated.getStatus()),
                () -> assertSame(updated, system.getEvent("E1")),
                () -> assertEquals("Campus event", original.getName()));
    }

    /**
     * Checks the exact start-time boundary and times after the start.
     * @param seconds offset from the current time
     */
    @ParameterizedTest
    @ValueSource(longs = {0, -1})
    void cannotUpdateAtOrAfterStart(long seconds) {
        EventRegistrationSystem timed = new EventRegistrationSystem(Clock.fixed(NOW, ZoneOffset.UTC));
        Event original = timed.createEvent(ORGANIZER, "E1", "Name", "Desc", "Place", NOW.plusSeconds(seconds), 2);
        fails(EVENT_STARTED, () -> timed.updateEvent(ORGANIZER, "E1", "Changed", "Desc", "Place", FUTURE, 2));
        assertSame(original, timed.getEvent("E1"));
    }

    /** Checks that editing is permitted immediately before the start instant. */
    @Test
    void canUpdateOneNanosecondBeforeStart() {
        system.createEvent(ORGANIZER, "E1", "Name", "Desc", "Place", NOW.plusNanos(1), 1);
        assertEquals(FUTURE, system.updateEvent(ORGANIZER, "E1", "Name", "Desc", "Place", FUTURE, 1).getDateTime());
    }

    /**
     * Verifies the replacement event date must be in the future.
     * @param seconds new date's offset from now
     */
    @ParameterizedTest
    @ValueSource(longs = {0, -1})
    void cannotMoveEventIntoPresentOrPast(long seconds) {
        Event original = event("E1", 2);
        fails(INVALID_INPUT, () -> system.updateEvent(ORGANIZER, "E1", "Name", "Desc", "Place", NOW.plusSeconds(seconds), 2));
        assertSame(original, system.getEvent("E1"));
    }

    /**
     * Checks update validation uses the same field rules as event creation.
     * @param id ignored creation ID from shared cases
     * @param name replacement name
     * @param description replacement description
     * @param location replacement location
     * @param date replacement start time
     * @param capacity replacement capacity
     */
    @ParameterizedTest
    @MethodSource("invalidEvents")
    void invalidUpdateLeavesOriginalUntouched(String id, String name, String description,
            String location, Instant date, int capacity) {
        Event original = event("E1", 2);
        // Invalid identifiers are already covered by the lookup test; use them here too.
        fails(INVALID_INPUT, () -> system.updateEvent(ORGANIZER, id, name, description, location, date, capacity));
        assertSame(original, system.getEvent("E1"));
    }

    /** Checks reducing capacity to the active count, rejecting lower capacity, and later expansion. */
    @Test
    void capacityChangesRespectExistingRegistrations() {
        event("E1", 3);
        system.register("A1", "E1");
        system.register("A2", "E1");
        fails(CAPACITY_TOO_SMALL, () -> system.updateEvent(ORGANIZER, "E1", "Name", "Desc", "Place", FUTURE, 1));
        assertEquals(3, system.getEvent("E1").getCapacity());
        assertEquals(2, system.updateEvent(ORGANIZER, "E1", "Name", "Desc", "Place", FUTURE, 2).getCapacity());
        system.createAttendee("A3", "New", "Student", "new@example.edu");
        fails(EVENT_FULL, () -> system.register("A3", "E1"));
        system.updateEvent(ORGANIZER, "E1", "Name", "Desc", "Place", FUTURE, 3);
        assertEquals(RegistrationStatus.ACTIVE, system.register("A3", "E1").getStatus());
    }

    /** Verifies closing preserves active participants and blocks registration, even after editing. */
    @Test
    void closeEventBlocksNewRegistration() {
        event("E1", 2);
        system.register("A1", "E1");
        assertEquals(EventStatus.CLOSED, system.closeEvent(ORGANIZER, "E1").getStatus());
        assertEquals(EventStatus.CLOSED, system.closeEvent(ORGANIZER, "E1").getStatus());
        assertEquals(1, system.getAttendeesForEvent("E1").size());
        assertEquals(EventStatus.CLOSED, system.updateEvent(ORGANIZER, "E1", "New", "Desc", "Place", FUTURE, 3).getStatus());
        fails(INVALID_EVENT_STATUS, () -> system.register("A2", "E1"));
    }

    /** Verifies event cancellation cascades only to that event and remains idempotent. */
    @Test
    void cancelEventCancelsItsRegistrations() {
        event("E1", 2);
        event("E2", 2);
        Registration first = system.register("A1", "E1");
        Registration second = system.register("A2", "E1");
        system.cancelRegistration(second.getId());
        Registration other = system.register("A1", "E2");
        assertEquals(EventStatus.CANCELED, system.cancelEvent(ORGANIZER, "E1").getStatus());
        assertEquals(EventStatus.CANCELED, system.cancelEvent(ORGANIZER, "E1").getStatus());
        assertEquals(RegistrationStatus.CANCELED, system.getRegistration(first.getId()).getStatus());
        assertEquals(RegistrationStatus.CANCELED, system.getRegistration(second.getId()).getStatus());
        assertEquals(RegistrationStatus.ACTIVE, system.getRegistration(other.getId()).getStatus());
        assertTrue(system.getAttendeesForEvent("E1").isEmpty());
        assertEquals(List.of("E2"), system.getEventsForAttendee("A1").stream().map(Event::getId).toList());
        fails(INVALID_EVENT_STATUS, () -> system.register("A2", "E1"));
        fails(INVALID_EVENT_STATUS, () -> system.closeEvent(ORGANIZER, "E1"));
        fails(INVALID_EVENT_STATUS, () -> system.updateEvent(ORGANIZER, "E1", "Name", "Desc", "Place", FUTURE, 2));
    }

    /** Verifies a closed event may subsequently be canceled. */
    @Test
    void cancelsClosedEvent() {
        event("E1", 1);
        system.closeEvent(ORGANIZER, "E1");
        assertEquals(EventStatus.CANCELED, system.cancelEvent(ORGANIZER, "E1").getStatus());
    }

    /** Checks every organizer mutation rejects a non-organizer and leaves the event intact. */
    @Test
    void eventManagementRequiresOrganizer() {
        Event original = event("E1", 2);
        for (UserRole role : new UserRole[] {ATTENDEE, null}) {
            fails(UNAUTHORIZED, () -> system.updateEvent(role, "E1", "New", "Desc", "Place", FUTURE, 3));
            fails(UNAUTHORIZED, () -> system.closeEvent(role, "E1"));
            fails(UNAUTHORIZED, () -> system.cancelEvent(role, "E1"));
        }
        assertSame(original, system.getEvent("E1"));
    }

    /** Verifies registration identity, timestamp, status, attendee, event, and retrieval. */
    @Test
    void createsAndRetrievesRegistration() {
        event("E1", 2);
        Registration registration = system.register("A1", "E1");
        assertAll(
                () -> assertFalse(registration.getId().isBlank()),
                () -> assertSame(system.getAttendee("A1"), registration.getAttendee()),
                () -> assertEquals("E1", registration.getEventId()),
                () -> assertEquals(NOW, registration.getTimestamp()),
                () -> assertEquals(RegistrationStatus.ACTIVE, registration.getStatus()),
                () -> assertSame(registration, system.getRegistration(registration.getId())));
        Registration second = system.register("A2", "E1");
        assertNotEquals(registration.getId(), second.getId());
    }

    /** Checks both referenced records must exist and failed requests do not reserve seats. */
    @Test
    void registrationRequiresExistingAttendeeAndEvent() {
        event("E1", 1);
        fails(NOT_FOUND, () -> system.register("missing", "E1"));
        fails(NOT_FOUND, () -> system.register("A1", "missing"));
        assertTrue(system.getAttendeesForEvent("E1").isEmpty());
        assertEquals(RegistrationStatus.ACTIVE, system.register("A1", "E1").getStatus());
    }

    /** Verifies the same attendee cannot reserve a second seat, including after cancellation. */
    @Test
    void rejectsDuplicateParticipation() {
        event("E1", 2);
        Registration registration = system.register("A1", "E1");
        fails(ALREADY_REGISTERED, () -> system.register("A1", "E1"));
        assertEquals(1, system.getAttendeesForEvent("E1").size());
        system.cancelRegistration(registration.getId());
        fails(ALREADY_REGISTERED, () -> system.register("A1", "E1"));
        assertTrue(system.getAttendeesForEvent("E1").isEmpty());
    }

    /** Checks exact capacity, rejection without side effects, and release of the final seat. */
    @Test
    void cancellationReleasesCapacityForAnotherAttendee() {
        event("E1", 1);
        Registration original = system.register("A1", "E1");
        fails(EVENT_FULL, () -> system.register("A2", "E1"));
        Registration canceled = system.cancelRegistration(original.getId());
        assertAll(
                () -> assertEquals(original.getId(), canceled.getId()),
                () -> assertEquals(original.getTimestamp(), canceled.getTimestamp()),
                () -> assertSame(original.getAttendee(), canceled.getAttendee()),
                () -> assertEquals(original.getEventId(), canceled.getEventId()),
                () -> assertEquals(RegistrationStatus.CANCELED, canceled.getStatus()),
                () -> assertEquals(RegistrationStatus.ACTIVE, original.getStatus()),
                () -> assertTrue(system.getEventsForAttendee("A1").isEmpty()));
        system.cancelRegistration(original.getId());
        Registration replacement = system.register("A2", "E1");
        assertNotEquals(original.getId(), replacement.getId());
        assertEquals(List.of("A2"), system.getAttendeesForEvent("E1").stream().map(Attendee::getId).toList());
        fails(ALREADY_REGISTERED, () -> system.register("A1", "E1"));
    }

    /** Verifies registrations can be canceled after the event is closed. */
    @Test
    void cancellationWorksForClosedEvent() {
        event("E1", 1);
        Registration registration = system.register("A1", "E1");
        system.closeEvent(ORGANIZER, "E1");
        assertEquals(RegistrationStatus.CANCELED, system.cancelRegistration(registration.getId()).getStatus());
        assertTrue(system.getAttendeesForEvent("E1").isEmpty());
        fails(INVALID_EVENT_STATUS, () -> system.register("A2", "E1"));
    }

    /** Verifies both query directions filter by record, exclude cancellations, and preserve order. */
    @Test
    void queriesReturnOnlySelectedActiveRelationships() {
        event("E1", 2);
        event("E2", 2);
        assertTrue(system.getAttendeesForEvent("E1").isEmpty());
        assertTrue(system.getEventsForAttendee("A1").isEmpty());
        system.register("A1", "E1");
        system.register("A2", "E1");
        Registration canceled = system.register("A1", "E2");
        assertEquals(List.of("A1", "A2"), system.getAttendeesForEvent("E1").stream().map(Attendee::getId).toList());
        assertEquals(List.of("E1", "E2"), system.getEventsForAttendee("A1").stream().map(Event::getId).toList());
        assertEquals(List.of("E1"), system.getEventsForAttendee("A2").stream().map(Event::getId).toList());
        system.cancelRegistration(canceled.getId());
        assertTrue(system.getAttendeesForEvent("E2").isEmpty());
        assertEquals(List.of("E1"), system.getEventsForAttendee("A1").stream().map(Event::getId).toList());
    }

    /** Checks queries expose immutable snapshots and later queries retrieve updated event details. */
    @Test
    void queryResultsCannotMutateSystemState() {
        event("E1", 2);
        system.register("A1", "E1");
        List<Attendee> attendees = system.getAttendeesForEvent("E1");
        List<Event> events = system.getEventsForAttendee("A1");
        assertThrows(UnsupportedOperationException.class, attendees::clear);
        assertThrows(UnsupportedOperationException.class, events::clear);
        system.register("A2", "E1");
        system.updateEvent(ORGANIZER, "E1", "Updated", "Desc", "Place", FUTURE, 2);
        assertEquals(1, attendees.size());
        assertEquals("Campus event", events.get(0).getName());
        assertEquals("Updated", system.getEventsForAttendee("A1").get(0).getName());
        assertEquals(2, system.getAttendeesForEvent("E1").size());
    }

    /** Checks lookups and mutations consistently reject unknown identifiers. */
    @Test
    void missingRecordsProduceCustomExceptions() {
        fails(NOT_FOUND, () -> system.getEvent("missing"));
        fails(NOT_FOUND, () -> system.getAttendee("missing"));
        fails(NOT_FOUND, () -> system.getRegistration("missing"));
        fails(NOT_FOUND, () -> system.getAttendeesForEvent("missing"));
        fails(NOT_FOUND, () -> system.getEventsForAttendee("missing"));
        fails(NOT_FOUND, () -> system.updateEvent(ORGANIZER, "missing", "Name", "Desc", "Place", FUTURE, 1));
        fails(NOT_FOUND, () -> system.closeEvent(ORGANIZER, "missing"));
        fails(NOT_FOUND, () -> system.cancelEvent(ORGANIZER, "missing"));
        fails(NOT_FOUND, () -> system.cancelRegistration("missing"));
    }

    /**
     * Checks null, empty, and whitespace identifiers across public lookup and mutation methods.
     * @param bad invalid identifier
     */
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" \t"})
    void rejectsInvalidLookupAndMutationIds(String bad) {
        event("E1", 1);
        fails(INVALID_INPUT, () -> system.getEvent(bad));
        fails(INVALID_INPUT, () -> system.getAttendee(bad));
        fails(INVALID_INPUT, () -> system.getRegistration(bad));
        fails(INVALID_INPUT, () -> system.getAttendeesForEvent(bad));
        fails(INVALID_INPUT, () -> system.getEventsForAttendee(bad));
        fails(INVALID_INPUT, () -> system.closeEvent(ORGANIZER, bad));
        fails(INVALID_INPUT, () -> system.cancelEvent(ORGANIZER, bad));
        fails(INVALID_INPUT, () -> system.cancelRegistration(bad));
        fails(INVALID_INPUT, () -> system.register(bad, "E1"));
        fails(INVALID_INPUT, () -> system.register("A1", bad));
    }

    /**
     * Demonstrates simultaneous requests cannot exceed capacity or create duplicate participation.
     * @param sameAttendee whether all requests target the same attendee
     * @throws Exception if a worker fails or the bounded wait expires
     */
    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void concurrentRegistrationsRemainAtomic(boolean sameAttendee) throws Exception {
        int capacity = 3;
        int requests = 12;
        event("E1", capacity);
        List<Callable<Boolean>> tasks = new ArrayList<>();
        CountDownLatch start = new CountDownLatch(1);
        for (int index = 0; index < requests; index++) {
            String attendee = "C" + index;
            system.createAttendee(attendee, "First", "Last", attendee + "@example.edu");
            String attendeeId = sameAttendee ? "A1" : attendee;
            tasks.add(() -> {
                assertTrue(start.await(5, TimeUnit.SECONDS));
                try {
                    system.register(attendeeId, "E1");
                    return true;
                } catch (EventRegistrationException exception) {
                    assertEquals(sameAttendee ? ALREADY_REGISTERED : EVENT_FULL, exception.getReason());
                    return false;
                }
            });
        }
        ExecutorService pool = Executors.newFixedThreadPool(requests);
        try {
            List<Future<Boolean>> futures = new ArrayList<>();
            for (Callable<Boolean> task : tasks) {
                futures.add(pool.submit(task));
            }
            start.countDown();
            int successful = 0;
            for (Future<Boolean> future : futures) {
                if (future.get(10, TimeUnit.SECONDS)) {
                    successful++;
                }
            }
            assertEquals(sameAttendee ? 1 : capacity, successful);
            assertEquals(successful, system.getAttendeesForEvent("E1").size());
        } finally {
            start.countDown();
            pool.shutdownNow();
            assertTrue(pool.awaitTermination(5, TimeUnit.SECONDS));
        }
    }

    /**
     * Creates a standard future event for tests.
     * @param id unique event identifier
     * @param capacity maximum attendance
     * @return newly created event
     */
    private Event event(String id, int capacity) {
        return system.createEvent(ORGANIZER, id, "Campus event", "Student activities",
                "Armour J Blackburn Center", FUTURE, capacity);
    }

    /**
     * Checks both the custom exception type and its meaningful failure reason.
     * @param reason expected business rule failure
     * @param operation operation expected to fail
     */
    private void fails(EventRegistrationException.Reason reason, Executable operation) {
        EventRegistrationException exception = assertThrows(EventRegistrationException.class, operation);
        assertEquals(reason, exception.getReason());
        assertFalse(exception.getMessage().isBlank());
    }
}
