# CSCI 363 midterm

Brian Too — Fall 2026

Questions 1–3 are implemented and documented in the directories required by the exam. Both instructor-provided drivers are preserved as extracted from the supplied Word document, including their original formatting. Prior coursework remains under the repository's existing `src` directory; this midterm uses the exact root-level `org` structure requested in the exam.

## Compile and run

From the repository root, with JDK 17 or later:

```sh
mkdir -p out/midterm
javac --release 17 -Xlint:all -Werror -d out/midterm \
  org/howard/edu/lsp/midterm/crccards/*.java \
  org/howard/edu/lsp/midterm/inheritance/*.java
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

Pending the student's independently written `reflection/doc/ai_reflection.md`. The exam prohibits AI generation, rewriting, editing, and evaluation of this response, so no response or substitute placeholder has been generated. Questions 1–3 being complete does not mean the entire exam is complete.

## Submission

The student confirmed a deadline of October 26, 2026, at 8:00 p.m. America/New_York and no solution edits afterward. All final files must be pushed before that deadline. The student will send the instructor the repository link using a prepared email draft after completing Question 4. Compiled classes and IDE project files are excluded from the midterm submission.
