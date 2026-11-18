package org.howard.edu.lsp.project;

/** An immutable attendee record. */
public final class Attendee {
    /** Stable identifier of this record. */
    private final String id;
    /** Attendee first name. */
    private final String firstName;
    /** Attendee last name. */
    private final String lastName;
    /** Attendee email address. */
    private final String email;

    /**
     * Creates a snapshot from values validated by the service.
     * @param id unique attendee identifier
     * @param firstName first name
     * @param lastName last name
     * @param email email address
     */
    Attendee(String id, String firstName, String lastName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    /**
     * Returns the unique attendee identifier.
     * @return the unique attendee identifier
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the first name.
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Returns the last name.
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Returns the email address.
     * @return the email address
     */
    public String getEmail() {
        return email;
    }
}
