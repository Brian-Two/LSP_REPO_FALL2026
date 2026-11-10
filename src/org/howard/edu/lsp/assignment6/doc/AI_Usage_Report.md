# AI Usage Report — Assignment 6

## AI conversations

- [Assignment 6 implementation and verification with OpenAI Codex](https://chatgpt.com/s/cx_6af26509f9148191afdf636c89d6d404)
- [Assignment 5 implementation of the reused IntegerSet class](https://chatgpt.com/s/cx_6ae235c9ca408191880e609fdd0fad2c)

OpenAI Codex was used to read the instructor's `JUnit_testing` document, copy the
Assignment 5 implementation into the required package, complete all 47 supplied
JUnit test bodies, and validate their behavior. Codex also prepared this report,
the README instructions, and the Git submission. The Assignment 6 conversation
link is a snapshot taken after testing and before this report and the final push.

`IntegerSet.java` is unchanged from Assignment 5 except for its package
declaration. Every predefined test name, `@Test` annotation, and Javadoc
description was preserved. Private helpers create fresh fixtures and check
membership, size, and result identity through the public interface. The equality
tests call `equals(IntegerSet)` directly rather than relying on object equality
through `assertEquals`.

## External references

- [JUnit 5.11.4 User Guide](https://docs.junit.org/5.11.4/user-guide/): JUnit 5
  assertions, exception testing, Eclipse support, and Console Launcher options.
- [JUnit Platform Console Standalone 1.11.4 on Maven Central](https://repo.maven.apache.org/maven2/org/junit/platform/junit-platform-console-standalone/1.11.4/junit-platform-console-standalone-1.11.4.jar):
  the JUnit runner and dependencies used for local compilation and execution.

The supplied `JUnit_testing` document provided the requirements and test template.
The existing Assignment 5 source provided the implementation being tested.

## Validation

- Compiled both Java files targeting Java 17 with `-Xlint:all -Werror`.
- Ran JUnit Jupiter 5.11.4: **47 tests found, 47 successful, 0 skipped, 0 failed**.
- Ran every predefined method independently in a separate JVM: **47/47 passed**.
- Verified that the test suite detects **35/35 deliberately defective versions**
  of the implementation, including reversed differences, incorrect equality and
  subset checks, input mutation, returning an input instead of a fresh result,
  incorrect empty-set exceptions, boundary mistakes, and formatting defects.
  These are local quality checks, not the instructor's hidden tests or a grade.
- Confirmed that all 47 predefined methods and their descriptions are retained,
  no TODO test bodies remain, and no implementation redesign was made.

Only the required source and report are in the assignment6 package. The local
JUnit dependency, compiled classes, deliberately defective copies, and test logs
are excluded from the submission. The instructor determines the final score by
reviewing test quality and the report.
