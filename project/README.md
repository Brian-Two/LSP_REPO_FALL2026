# Howard University Event Registration System

Brian Too | CSCI 363 Large Scale Programming | Option 2 | AWS

This Java library manages university events, attendees, and registrations. JUnit 5 tests demonstrate the required functionality; the handout does not require a GUI, command-line menu, or driver. The cloud architecture describes a possible AWS deployment and does not require creating cloud resources.

## Deliverables

- `../src/org/howard/edu/lsp/project/` — application source, base package `org.howard.edu.lsp.project`.
- `../test/org/howard/edu/lsp/project/` — JUnit 5 tests in the corresponding project package.
- `LSP_Project.zip` — official software package containing only those Java sources and tests, with the required `src/` and `test/` paths.
- `CloudArchitecture.pdf` — three-page AWS architecture covering all nine questions.
- `CloudArchitecture.md` — editable text of the architecture and links to official AWS references.
- `run-tests.sh` — repeatable project-only compilation and JUnit run.
- `package_submission.py` — recreates and verifies the source-only ZIP.

## Run the project tests

Use **JDK 17 or newer** (`java`, `javac`, and `javadoc` on PATH). The shell helper works on macOS/Linux and requires `curl` plus `shasum` or `sha256sum`. From the repository root:

```sh
./project/run-tests.sh
```

The helper downloads the pinned **JUnit 5.11.4 / JUnit Platform 1.11.4** standalone console JAR from Maven Central, verifies its SHA-256 digest, compiles only the project source and tests with `--release 17 -Xlint:all -Werror`, and executes the project test package. It fails when no tests are discovered or any test fails. It does not compile previous assignments. Build products and XML test results are under `out/project/`, and the cached dependency is under `out/project-tools/`.

For an offline run, obtain that exact JAR in advance and set `JUNIT_JAR` to its location; its checksum is still verified:

```sh
JUNIT_JAR=/absolute/path/junit-platform-console-standalone-1.11.4.jar ./project/run-tests.sh
```

In Eclipse or another Java IDE, use JDK 17+, add `src` and `test` as source folders, add JUnit 5, and run `test/org/howard/edu/lsp/project/EventRegistrationSystemTest.java` as a JUnit test. Existing assignment test files may also require the JUnit library because this is a shared course repository.

### Evaluate the official ZIP independently

Extract `project/LSP_Project.zip` into an empty directory. The ZIP intentionally excludes build helpers and dependencies to comply with the handout's source-and-test-only requirement. With the standalone JUnit JAR available, run these commands from the extracted root (replace `/absolute/path/`):

```sh
mkdir -p out/main out/test
javac --release 17 -Xlint:all -Werror -d out/main src/org/howard/edu/lsp/project/*.java
javac --release 17 -Xlint:all -Werror -cp /absolute/path/junit-platform-console-standalone-1.11.4.jar:out/main -d out/test test/org/howard/edu/lsp/project/*.java
java -jar /absolute/path/junit-platform-console-standalone-1.11.4.jar execute --class-path out/main:out/test --select-package org.howard.edu.lsp.project --fail-if-no-tests
```

On Windows, use `;` instead of `:` between classpath entries.

## Object-oriented design

| Class | Responsibility |
| --- | --- |
| `Event` | Immutable event snapshot: identifier, name, description, location, start instant, capacity, status. |
| `Attendee` | Immutable attendee identity, first name, last name, and email. |
| `Registration` | Immutable registration snapshot linking one attendee to one event identifier, with a unique identifier, timestamp, and status. |
| `EventRegistrationSystem` | Owns record collections and enforces operations and business rules. |
| `EventRegistrationException` | Custom exception with a descriptive message and a testable `Reason`. |
| `EventStatus`, `RegistrationStatus`, `UserRole` | Restrict lifecycle and role values to known choices. |

Private maps keep storage encapsulated. Clients cannot change returned entities because fields are private and final and there are no setters. The service replaces snapshots when details or statuses change, so previously retrieved objects remain historical snapshots. Query results are unmodifiable independent lists. `Registration` associates an attendee object with an event identifier; the service resolves that identifier to the current event snapshot for queries.

Each public operation is synchronized on the service, making a capacity check and its registration insertion indivisible within one service instance. This is appropriate for the submitted in-memory application. A future multi-server system needs database transactions, as explained in the cloud document. Injecting `Clock` lets tests check date boundaries and exact timestamps without waiting or depending on today's date.

## API example

The tests are the executable demonstration. A typical sequence is:

```java
EventRegistrationSystem system = new EventRegistrationSystem();
system.createEvent(UserRole.ORGANIZER, "EV-1", "Career Fair",
        "Meet employers", "Blackburn Center",
        Instant.parse("2027-02-10T18:00:00Z"), 100);
system.createAttendee("AT-1", "Brian", "Too", "brian@example.edu");
Registration registration = system.register("AT-1", "EV-1");
List<Attendee> participants = system.getAttendeesForEvent("EV-1");
system.cancelRegistration(registration.getId());
```

The example requires imports from `org.howard.edu.lsp.project`, `java.time.Instant`, and `java.util.List`. All dates use `Instant` to avoid machine-dependent time zones.

## Implemented rules and lifecycle choices

- Events start `OPEN`. An organizer can create, update, close, or cancel events. An attendee or null role is rejected before an organizer operation changes state. The role is a **trusted caller input**, not a login mechanism; a future authenticated adapter must derive it from verified identity data.
- Event and attendee identifiers are unique within their respective record types. They are case-sensitive and are not silently trimmed or normalized. Registration identifiers are generated uniquely within each service instance.
- Required text must be nonblank, dates non-null, and capacity positive. Email is stored as supplied after the nonblank check; address verification and a Howard-only email restriction are not specified in the handout and are not added.
- Details can be updated only strictly before the existing event start; the replacement start must also be in the future. ID and status remain unchanged. Capacity may equal the active registration count but cannot fall below it.
- Registration requires an existing attendee, an existing `OPEN` event, available capacity, and no previous registration for the same attendee-event pair. Consistent with the literal “No attendee may register more than once for the same event,” canceled records still block a second registration for that pair. Reactivation is not implemented.
- Only `ACTIVE` registrations consume seats. Canceling one releases capacity for another attendee and removes the relationship from active queries. The canceled record remains retrievable by ID with its original timestamp.
- Closing an event retains its active registrations. Canceling an event marks all its active registrations `CANCELED`; it does not affect other events. Closed events can be canceled. Canceled events cannot be edited or closed, and there is no reopen operation.
- Repeating a close or cancellation is harmless. Active attendee/event queries exclude canceled registrations, preserve insertion order, and validate the selected record exists. Closed events with active registrations remain in attendee queries.
- The handout explicitly limits editing by date. The listed registration guards are implemented as stated; no extra date-based registration cutoff is imposed beyond event status. Organizers can close registration using `closeEvent`.

The handout does not explicitly define reactivation, cancellation propagation, or repeated cancellation. The choices above make that behavior visible for review rather than presenting those choices as additional instructor requirements.

## Tests and validation

**73 JUnit 5 test invocations pass with meaningful assertions.** The suite includes parameterized null/blank and boundary cases. Verified on November 18, 2026 with JDK 17 and JDK 23, compiling to Java 17 bytecode. JaCoCo 0.8.13 measured **100% line coverage (180/180)** and **100% branch coverage (64/64)** for the application classes; the class-level results are in [coverage.csv](coverage.csv). These results describe the submitted suite; coverage alone does not establish correctness for every possible input.

| Requirement | Representative test methods |
| --- | --- |
| Create and retrieve events; unique IDs | `createsAndRetrievesEvent`, `rejectsDuplicateEventId` |
| Authorized event management | `unauthorizedCallerCannotCreateEvent`, `eventManagementRequiresOrganizer` |
| Update all details before the start; capacity boundary | `updatesFutureEvent`, `cannotUpdateAtOrAfterStart`, `canUpdateOneNanosecondBeforeStart`, `capacityChangesRespectExistingRegistrations` |
| Close and cancel events | `closeEventBlocksNewRegistration`, `cancelEventCancelsItsRegistrations`, `cancelsClosedEvent` |
| Create and retrieve attendees; unique IDs | `createsAndRetrievesAttendee`, `rejectsDuplicateAttendeeId` |
| Register; required relationships and timestamp | `createsAndRetrievesRegistration`, `registrationRequiresExistingAttendeeAndEvent` |
| No duplicate participation or overbooking | `rejectsDuplicateParticipation`, `cancellationReleasesCapacityForAnotherAttendee`, `concurrentRegistrationsRemainAtomic` |
| Cancel registration and free capacity | `cancellationReleasesCapacityForAnotherAttendee`, `cancellationWorksForClosedEvent` |
| Queries in both directions | `queriesReturnOnlySelectedActiveRelationships`, `queryResultsCannotMutateSystemState` |
| Custom exceptions and invalid values | `missingRecordsProduceCustomExceptions`, `rejectsInvalidEventValues`, `invalidUpdateLeavesOriginalUntouched`, `rejectsInvalidAttendeeValues`, `rejectsInvalidLookupAndMutationIds` |

Strict JavaDoc generation covers application and test code, including private methods:

```sh
javadoc -quiet -Xdoclint:all -Werror -private -classpath out/project-tools/junit-platform-console-standalone-1.11.4.jar -d out/project/javadoc src/org/howard/edu/lsp/project/*.java test/org/howard/edu/lsp/project/*.java
```

The three-page PDF was rendered and visually inspected. The ZIP was CRC-checked, compared byte-for-byte against the working source, extracted into an empty directory, compiled, and tested independently.

## Rebuild the submission ZIP

After changing any project source or tests, rerun the tests and JavaDoc command, then use Python 3.9+ (standard library only):

```sh
python3 project/package_submission.py
```

The script rebuilds `project/LSP_Project.zip` from only project `.java` files and verifies each entry and its bytes. Prior assignments, compiled classes, libraries, README files, scripts, and cloud documents are excluded from this ZIP. The ZIP preserves `src/org/howard/edu/lsp/project/` and `test/org/howard/edu/lsp/project/`.

## Scope and resources

Data is in memory and is lost when the service instance is discarded. There is no HTTP server, database, persistent identity system, or deployed AWS infrastructure in this submission. Those capabilities belong to the proposed cloud architecture, not to the required local implementation.

The supplied `CSCI363_Capstone_Project.docx` was used as the assignment specification. OpenAI Codex assisted with interpreting requirements, implementing the library and JUnit tests, writing documentation, researching AWS services, and checking the submission package. Official [JUnit documentation](https://junit.org/junit5/docs/5.11.4/user-guide/) and [JaCoCo documentation](https://www.jacoco.org/jacoco/trunk/doc/) informed test execution and coverage tooling. AWS sources are linked in `CloudArchitecture.md` and the PDF. This disclosure does not claim that the instructor has reviewed or approved the implementation.
