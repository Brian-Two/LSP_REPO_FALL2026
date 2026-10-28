# CSCI 363 midterm

Brian Too — Fall 2026

Questions 1–3 are implemented and documented, and the student-authored Question 4 reflection is included. Both instructor-provided drivers are preserved as extracted from the supplied Word document, including their original formatting. The midterm is located at `src/org/howard/edu/lsp/midterm`, alongside prior coursework under `src`, as clarified by Professor Woolfolk on October 27, 2026. Java package declarations remain unchanged.

## Compile and run

From the repository root, with JDK 17 or later:

```sh
mkdir -p out/midterm
javac --release 17 -Xlint:all -Werror -d out/midterm \
  src/org/howard/edu/lsp/midterm/crccards/*.java \
  src/org/howard/edu/lsp/midterm/inheritance/*.java
java -cp out/midterm org.howard.edu.lsp.midterm.crccards.Driver
java -cp out/midterm org.howard.edu.lsp.midterm.inheritance.Driver
```

Verified results on October 26, 2026, using JDK 23.0.1 with Java 17 compatibility:

- Question 1: `RESULT: 44 PASSED, 0 FAILED` and `FUNCTIONAL SCORE: 25.00 / 25`.
- Question 3: `RESULT: 12 PASSED, 0 FAILED`.
- Javadoc generation for all six implementation classes passed with full doclint and warnings treated as errors. The provided drivers were excluded from this documentation check because they must remain unchanged.
- Nineteen supplemental local checks passed for lexicographical ISBN ordering, returned-list independence, failed-operation state preservation, preborrowed-book accounting, fractional hours, zero rates, and polymorphic dispatch. The supplemental harness is not an instructor grading driver.

## Written deliverables

- [Question 1 CRC explanation](crccards/doc/crccards.md)
- [Question 1 development log and AI transcript](crccards/doc/development_log.md)
- [Question 2 evaluation](design/doc/evaluation.md)
- [Question 2 CRC cards](design/doc/design.md)
- [Question 2 design rationale](design/doc/design_rationale.md)
- [Question 2 development log and AI transcript](design/doc/development_log_q2.md)
- [Question 3 development log and AI transcript](inheritance/doc/development_log_q3.md)

## Question 4 status

The student's dictated response is saved in [ai_reflection.md](reflection/doc/ai_reflection.md) verbatim. Only file placement and exact-text verification were performed; the response was not rewritten, edited, or evaluated by AI. The earlier development logs record the state at their stated development checkpoint, before the student supplied this response.

## Submission

The student confirmed a deadline of October 26, 2026, at 8:00 p.m. America/New_York and no solution edits afterward. The solutions were pushed before that deadline. On October 27, 2026, Professor Woolfolk requested relocation under `src` so the work could be executed for grading; that correction changes file placement and these run instructions only. Compiled classes and IDE project files are excluded from the midterm submission.
