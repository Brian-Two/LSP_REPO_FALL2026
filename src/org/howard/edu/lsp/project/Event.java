package org.howard.edu.lsp.project;

import java.time.Instant;

/** An immutable snapshot of a university event. */
public final class Event {
    /** Stable identifier of this record. */
    private final String id;
    /** Human-readable event name. */
    private final String name;
    /** Description of the event activities. */
    private final String description;
    /** Location of the event. */
    private final String location;
    /** Event start instant. */
    private final Instant dateTime;
    /** Maximum number of active registrations. */
    private final int capacity;
    /** Lifecycle status of this snapshot. */
    private final EventStatus status;

    /**
     * Creates a snapshot from values validated by the service.
     * @param id unique event identifier
     * @param name event name
     * @param description event description
     * @param location event location
     * @param dateTime event start instant
     * @param capacity maximum attendee capacity
     * @param status current event status
     */
    Event(String id, String name, String description, String location, Instant dateTime, int capacity, EventStatus status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.location = location;
        this.dateTime = dateTime;
        this.capacity = capacity;
        this.status = status;
    }

    /**
     * Returns the unique event identifier.
     * @return the unique event identifier
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the event name.
     * @return the event name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the event description.
     * @return the event description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the event location.
     * @return the event location
     */
    public String getLocation() {
        return location;
    }

    /**
     * Returns the event start instant.
     * @return the event start instant
     */
    public Instant getDateTime() {
        return dateTime;
    }

    /**
     * Returns the maximum attendee capacity.
     * @return the maximum attendee capacity
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Returns the current event status.
     * @return the current event status
     */
    public EventStatus getStatus() {
        return status;
    }
}
