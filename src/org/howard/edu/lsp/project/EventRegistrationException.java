package org.howard.edu.lsp.project;

/** Reports a rejected operation without changing application state. */
public final class EventRegistrationException extends RuntimeException {
    /** Serialization version for this exception. */
    private static final long serialVersionUID = 1L;
    /** Specific rule or lookup responsible for rejection. */
    private final Reason reason;

    /** Identifies the business rule or lookup that rejected an operation. */
    public enum Reason {
        /** A required value was null, blank, or outside its valid range. */
        INVALID_INPUT,
        /** An event or attendee identifier is already in use. */
        DUPLICATE_ID,
        /** The requested record does not exist. */
        NOT_FOUND,
        /** The caller does not have the organizer role. */
        UNAUTHORIZED,
        /** The event cannot accept this operation in its current status. */
        INVALID_EVENT_STATUS,
        /** All available seats are occupied. */
        EVENT_FULL,
        /** This attendee already has a registration record for this event. */
        ALREADY_REGISTERED,
        /** Event details cannot be edited at or after the event start time. */
        EVENT_STARTED,
        /** The requested capacity is below the active registration count. */
        CAPACITY_TOO_SMALL
    }

    /**
     * Constructs a business exception.
     * @param reason machine-readable reason for rejection
     * @param message explanation for the caller
     */
    EventRegistrationException(Reason reason, String message) {
        super(message);
        this.reason = reason;
    }

    /**
     * Returns the business rule or lookup that rejected the operation.
     * @return the business rule or lookup that rejected the operation
     */
    public Reason getReason() {
        return reason;
    }
}
