# Evaluation of CourseRegistration

The class combines registration data, tuition rules, presentation, persistence, notification, and logging in one operation. Its short length does not make those responsibilities cohesive: changing a storage format or a tuition rule requires modifying the same class. The following evaluation applies Arthur Riel's design heuristics to the actual behavior of the supplied code.

## Encapsulation and valid state

Riel's heuristic 2.1 calls for hiding a class's data behind its interface [1]. Here, all five fields are public, so any caller can change student identity, contact information, course code, or credit hours without validation. An instance can be registered before required fields are initialized, and negative credit hours can produce negative tuition. A constructor and focused operations should establish valid state rather than relying on callers to remember an undocumented initialization sequence. Student identity and course information should belong to their respective objects, with a Registration representing their association.

## Cohesion and responsibility allocation

Riel recommends representing one central abstraction in a class, keeping related state and behavior together, and moving unrelated concerns elsewhere [2]. Tuition depends on credit hours and pricing rules; file output depends on storage configuration; confirmation depends on contact information and message delivery. Those concerns change for different reasons. Keeping them in registerStudent() makes even a small change require retesting unrelated behavior. A TuitionPolicy should own the rate and discount, while storage and notification objects should own their respective operations.

The class also concentrates most system behavior in one place, conflicting with Riel's guidance against god classes and in favor of distributing responsibilities [2]. Simply moving each block into a private helper would shorten registerStudent() without correcting ownership. A small RegistrationService may coordinate the workflow, but it should delegate domain decisions and infrastructure work instead of implementing every step itself.

## Coupling to presentation and infrastructure

Riel separates the domain model from its user interface [2]. Direct System.out calls tie registration behavior to a console. A future web interface would have to change or suppress those statements. A presenter should display a registration result without requiring the domain objects to know where they are shown.

Constructing FileWriter inside the registration method hard-codes local file storage, the filename, append behavior, and a particular record format. Replacing the file with a database would require editing the business workflow. Depending on a RegistrationStore contract would isolate this change and permit tests with an in-memory substitute. Likewise, the notification concern should be accessed through a delivery contract rather than embedded in registration processing.

## Correctness and failure handling

The code prints a confirmation before attempting persistence. If file writing fails, the broad catch prints a stack trace and then execution continues to the notification and processed log messages. A caller receives no explicit failure outcome because the method returns void. The system can therefore report success for a registration that was never saved. The storage operation should report failure to the coordinator, which should only proceed with success notification after a successful save.

FileWriter is closed only on the success path; an exception during write can skip close. The persistence implementation should use try-with-resources. Concatenating fields with commas and newlines also leaves escaping undefined, so unexpected input can corrupt the record format. The file implementation should own encoding and escaping decisions. The line labeled as email delivery only prints a message; it does not send email and should not be treated as evidence of delivery.

Finally, the 750.0 rate, 12-credit threshold, and 10% discount are embedded literals. The meaning of creditHours is ambiguous: the object has one courseCode, but the discount comment refers to a heavy course load. If creditHours means one course's credits, the discount test cannot establish the student's total load. That domain assumption must be made explicit. For the proposed design, it means the credits being priced in this registration, preserving the supplied calculation; a real semester-wide discount would require a separate source of total enrolled credits. A monetary representation with an explicit rounding rule would also be preferable to unrestricted double arithmetic in a production tuition system.

## References

1. Arthur J. Riel, *Object-Oriented Design Heuristics*, Addison-Wesley, 1996, Appendix A, heuristic 2.1. Online book preview: https://www.oreilly.com/library/view/object-oriented-design-heuristics/020163385X/apa.html
2. University of Bern Software Composition Group, *60 design heuristics*, teaching summary of Riel's heuristics, sections A and B: https://scg.unibe.ch/teaching/ese/60-design-heuristics

The references support the heuristic descriptions; the concrete defects and consequences above come from analysis of the class provided in the exam.
