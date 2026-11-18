package org.howard.edu.lsp.project;

/** Trusted caller roles used by the library's event management policy. */
public enum UserRole {
    /** May create, update, close, and cancel events. */
    ORGANIZER,
    /** Cannot perform event management operations. */
    ATTENDEE
}
