# Supporting storage and confirmation choices

The Part 2 design already gives RegistrationService two stable collaboration contracts: RegistrationStore and ConfirmationSender. FileRegistrationStore and EmailConfirmationSender are the initial implementations. Supporting another storage location or message channel therefore changes concrete collaborators without putting file/database or email/text conditionals into the registration workflow.

## Additional CRC cards

**Class:** DatabaseRegistrationStore (implements RegistrationStore)

**Responsibilities:** Save a Registration using a configured database connection; map domain values to parameters in an insert statement; manage transaction and resource boundaries; report success only after committing, and propagate a failed save through the existing contract. Keep connection credentials and SQL outside Registration and RegistrationService.

**Collaborators:** Registration, Student, Course, database connection/transaction library.

**Class:** TextMessageConfirmationSender (implements ConfirmationSender)

**Responsibilities:** Obtain the student's mobile number; format a concise confirmation; submit it to a configured text-message service; report acceptance or failure using the same contract as email. Reject a missing or unusable destination explicitly rather than silently switching channels.

**Collaborators:** Registration, Student, Course, configured text-message service.

**Class:** ApplicationConfiguration

**Responsibilities:** Read the deployment's storage and confirmation settings; validate the selected options and required configuration; construct either FileRegistrationStore or DatabaseRegistrationStore and either EmailConfirmationSender or TextMessageConfirmationSender; supply those objects, TuitionPolicy, and RegistrationLogger to RegistrationService; provide that service to RegistrationPresenter. Reject unknown options at startup.

**Collaborators:** FileRegistrationStore, DatabaseRegistrationStore, EmailConfirmationSender, TextMessageConfirmationSender, TuitionPolicy, RegistrationLogger, RegistrationService, RegistrationPresenter.

ApplicationConfiguration represents startup configuration and object assembly, not registration business rules. Its concrete-class knowledge is localized to startup; RegistrationService continues to depend on the two interfaces.

## Changes to existing cards

Student gains an optional mobile number with validation when supplied. Email becomes an optional contact value as well, so a text-only deployment does not require an unused email address. The selected sender validates that its required destination is present before attempting delivery. Student still owns identity/contact validity and has no dependency on either sender.

RegistrationPresenter gains responsibility for accepting a mobile number when text confirmation is configured. RegistrationService, RegistrationStore, and ConfirmationSender keep their existing responsibilities and collaborators. FileRegistrationStore and EmailConfirmationSender continue to work unchanged apart from the email sender explicitly handling absent email contact data under its existing failure contract.

The four storage/channel combinations can be selected independently. For example, one deployment can use file storage with email, while another uses database storage with text messages. Neither choice affects tuition calculation.

## Responsibility ownership and failure behavior

The important ownership decision is that RegistrationService controls the order of saving and requesting confirmation, while each store owns the mechanics of persistence. Only the coordinator knows that a successful save is a prerequisite for confirmation. Making a store send confirmations would couple storage to communication and force both file and database implementations to duplicate workflow decisions. Making Registration perform I/O would mix the enrollment record with deployment concerns again.

TuitionPolicy similarly retains the pricing decision because neither a database nor a message channel should determine a student's charge. These boundaries keep related responsibilities together and distribute work across focused collaborators, applying the Riel principles discussed in Part 1.

If storage fails, the service does not request a success confirmation. If sending fails after a committed save, it reports that the registration was saved but confirmation failed; retrying notification must not repeat registration creation. This design does not claim that a database transaction can atomically include an external email or text service. The separate outcomes make that boundary explicit without expanding this exercise into a distributed delivery system.
