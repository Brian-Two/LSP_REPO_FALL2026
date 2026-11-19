package org.howard.edu.lsp.project;

import java.time.Instant;

/** An immutable link between exactly one attendee and one event. */
public final class Registration {
    /** Stable identifier of this record. */
    private final String id;
    /** Attendee associated with this registration. */
    private final Attendee attendee;
    /** Identifier of the associated event. */
    private final String eventId;
    /** Time when the registration was first created. */
    private final Instant timestamp;
    /** Lifecycle status of this snapshot. */
    private final RegistrationStatus status;

    /**
     * Creates a snapshot from values validated by the service.
     * @param id unique registration identifier
     * @param attendee registered attendee
     * @param eventId associated event identifier
     * @param timestamp registration creation instant
     * @param status current registration status
     */
    Registration(String id, Attendee attendee, String eventId, Instant timestamp, RegistrationStatus status) {
        this.id = id;
        this.attendee = attendee;
        this.eventId = eventId;
        this.timestamp = timestamp;
        this.status = status;
    }

    /**
     * Returns the unique registration identifier.
     * @return the unique registration identifier
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the registered attendee.
     * @return the registered attendee
     */
    public Attendee getAttendee() {
        return attendee;
    }

    /**
     * Returns the associated event identifier.
     * @return the associated event identifier
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * Returns the registration creation instant.
     * @return the registration creation instant
     */
    public Instant getTimestamp() {
        return timestamp;
    }

    /**
     * Returns the current registration status.
     * @return the current registration status
     */
    public RegistrationStatus getStatus() {
        return status;
    }
}
