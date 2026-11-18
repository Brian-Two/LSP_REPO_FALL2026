package org.howard.edu.lsp.project;

/** Describes whether an event accepts registrations. */
public enum EventStatus {
    /** Accepts registrations while capacity remains. */
    OPEN,
    /** Exists but does not accept new registrations. */
    CLOSED,
    /** Has been canceled and does not accept registrations. */
    CANCELED
}
