# Proposed registration design using CRC cards

The design separates student and course data from the registration workflow and its external effects. In the initial configuration, records are saved locally and confirmations are sent by email. Interface cards identify collaboration contracts; their concrete implementation cards identify who performs the work.

For this exercise, creditHours means the credits being priced for the registration, matching the supplied formula. TuitionPolicy applies 750 per credit and a 10% discount when that value is at least 12. This assumption does not claim to calculate a student's total semester load from one course record.

## Student

**Class:** Student

**Responsibilities:** Maintain a nonblank student ID, name, and email address; validate required identity/contact values when created; provide the information needed for registration and confirmation without public mutable fields.

**Collaborators:** None.

## Course

**Class:** Course

**Responsibilities:** Maintain a nonblank course code and positive credit hours; validate course data; provide the course information used for registration and tuition calculation.

**Collaborators:** None.

## Registration

**Class:** Registration

**Responsibilities:** Associate a Student with a Course; retain the assessed tuition as an immutable monetary value; require valid references and a nonnegative amount; provide an immutable record of the assessed registration so later policy changes do not alter its historical price.

**Collaborators:** Student, Course.

## TuitionPolicy

**Class:** TuitionPolicy

**Responsibilities:** Own the per-credit rate, discount threshold, discount fraction, and monetary rounding rule; calculate tuition from a Course's credit hours; keep pricing decisions independent of storage and delivery. The initial policy uses the exam's rate and discount and rounds the final amount to cents.

**Collaborators:** Course.

## RegistrationService

**Class:** RegistrationService

**Responsibilities:** Coordinate one registration using valid Student and Course objects; request a tuition assessment; construct a Registration; ask the configured store to save it; request confirmation only after saving succeeds; report a result that distinguishes save failure, saved with confirmation sent, and saved with confirmation failure; record the outcome. Collaborators are supplied when the service is constructed.

**Collaborators:** Student, Course, TuitionPolicy, Registration, RegistrationStore, ConfirmationSender, RegistrationLogger.

## RegistrationStore

**Class:** RegistrationStore (interface)

**Responsibilities:** Define save(Registration) with the same success/failure contract for every implementation; report success only after its storage operation completes; report failure rather than printing and suppressing it. The contract exposes no file paths or database connections.

**Collaborators:** Registration.

## FileRegistrationStore

**Class:** FileRegistrationStore (implements RegistrationStore)

**Responsibilities:** Save registration records to a configured local file; own the serialization format, escaping, encoding, and resource lifecycle; close resources with try-with-resources; propagate write failures through the storage contract.

**Collaborators:** Registration, Student, Course, Java file I/O library.

## ConfirmationSender

**Class:** ConfirmationSender (interface)

**Responsibilities:** Define send(Registration) independently of the communication channel; report delivery-service acceptance or a sending failure; avoid claiming that acceptance proves the recipient read the message.

**Collaborators:** Registration.

## EmailConfirmationSender

**Class:** EmailConfirmationSender (implements ConfirmationSender)

**Responsibilities:** Obtain the student's email address, format a confirmation from the registration, send it through a configured mail service, and report the sending outcome. Own email-specific addressing and formatting concerns.

**Collaborators:** Registration, Student, Course, configured mail service.

## RegistrationLogger

**Class:** RegistrationLogger

**Responsibilities:** Record timestamped registration outcomes and failures using a configured logging destination; distinguish persistence failure from notification failure; avoid recording unnecessary personal contact information. Logging must not falsely label a failed registration as successful.

**Collaborators:** Registration, logging and time libraries.

## RegistrationPresenter

**Class:** RegistrationPresenter

**Responsibilities:** Accept user input and construct validated Student and Course objects; invoke RegistrationService; display the returned outcome and assessed amount; display validation or processing failures. Own console formatting so domain and storage objects do not depend on System.out.

**Collaborators:** Student, Course, RegistrationService, Registration.

## Collaboration sequence

The presenter creates validated input objects and calls the service. The service asks TuitionPolicy to calculate the amount, constructs Registration, and calls RegistrationStore.save. If saving fails, it records and returns failure without sending confirmation. After a successful save, it calls ConfirmationSender.send, records the outcome, and returns either success or a saved-but-confirmation-failed result for the presenter to display. A notification failure does not trigger a second save of the registration.
