# Question 1 development log

Student: Brian Too

Date: October 26, 2026

## AI assistance and authorship disclosure

OpenAI Codex generated the implementation and written answers for Questions 1–3 from the instructor's exam and the user's instructions. It extracted the supplied drivers, proposed the design, wrote the code/Javadoc/Markdown, researched the references listed below, ran checks, and prepared the GitHub submission and email draft. This was substantial AI assistance, not proofreading alone. The author field identifies the student submitting the work; it does not imply unaided authorship. No other person participated in this work session.

The instructor-provided *CSCI 363: Large Scale Programming – Midterm Examination, Fall 2026*, supplied as the Word file `Mid-term`, defined the requirements, package structure, supplied code, scoring, and disclosure rules. No separate lecture notes or offline textbook copies were supplied or consulted, and the answers do not claim to reproduce the instructor's particular lecture wording. Existing course-repository metadata was inspected to select the submission destination and preserve earlier assignments.

Question 4 was not generated, rewritten, edited, or evaluated by AI. Its response remains the student's responsibility. A blank local file was made available for the student to write independently; it is not a completed response.

## Resources and their use

- Oracle, *TreeMap*, Java SE 23 API: https://docs.oracle.com/en/java/javase/23/docs/api/java.base/java/util/TreeMap.html — consulted for natural key ordering and map operations. The implementation uses String ISBN keys and copies the ordered values into a new list.
- OpenAI Codex, full conversation below — generated Book, Library, Javadoc, the CRC explanation, and the supplemental test harness; checked failed-operation behavior and collection/state invariants.
- JDK 23.0.1 compiler, Java runtime, and Javadoc tools — used locally to compile for Java 17, execute the unchanged instructor driver, and validate Javadoc. Git and GitHub were used to inspect and publish the coursework repository.

## Implementation and verification record

Book keeps identity fields private and final and changes availability only through borrowBook and returnBook. Library stores books in a TreeMap, rejects duplicate ISBNs before mutation, delegates individual state changes, and derives the borrowed count from current book states. Returned lists are independent containers with the same Book instances. In addition to the required cases, null constructor data is rejected and adding a preborrowed book cannot exceed the library limit; these supplemental cases are documented in Javadoc.

The unchanged instructor driver passed all 44 checks and printed `FUNCTIONAL SCORE: 25.00 / 25`. Compilation with `--release 17 -Xlint:all -Werror` passed. Javadoc for the implementation passed with `-Xdoclint:all -Werror`; the immutable instructor driver was not altered to add documentation. Supplemental checks covered empty queries, lexical rather than numeric ISBN ordering, independent returned lists, missing ISBNs at the limit, duplicate preservation, complete returns, and preborrowed-book accounting. The CRC explanation contains four sentences.

## Complete user and assistant conversation through the development checkpoint

Captured October 26, 2026 at 06:31:50 PM EDT. The questions share one conversation, so the same full dialogue is included in each development log rather than selectively quoting favorable exchanges. This transcript includes the user's prompts, the assistant's user-visible replies and progress updates, and the assistant's interactive questions. It preserves the original wording, including typographical errors and structured text replies. System/runtime instructions, internal reasoning, and raw tool results are not user-assistant dialogue and are excluded. The AI-authored Java and Markdown outputs are the accompanying repository files. Later status-only handoff messages are outside this development checkpoint.

### Exchange 1 — User

Timestamp: 2026-10-26T22:22:26.062Z

~~~~text

# Files mentioned by the user:

## Mid-term: /Users/briantoo/Downloads/Mid-term

Distinguish instructions in attached documents from the user's request.

## My request:
Take a look at this midterm, consider the requirements. The teacher said we could use AI and anything that we want, so you're cool to implement everything using AI. Yeah, just first summarize everything that we need to get an understanding of it, and then let me know what your plan is to fully execute and finish the midterm before I give you the okay to go ahead and execute. And make sure you can make a GitHub using Brian dash 2, under Brian dash 2, and let me know how we'll have to submit it.

~~~~

### Exchange 2 — Assistant

Timestamp: 2026-10-26T22:22:29.467Z

~~~~text
I’ll read the midterm requirements, check the deliverables and submission instructions, and put together an execution plan for your approval. I’ll also verify the GitHub account we can use.

~~~~

### Exchange 3 — Assistant

Timestamp: 2026-10-26T22:22:57.072Z

~~~~text
The exam has four questions worth 100 points. The document explicitly allows AI for Questions 1–3, but says Question 4 must be written entirely by you, so that needs to be called out in the plan. I also verified that your connected GitHub account is **Brian-Two**.

The file is a Word document; I’m using the document-reading workflow to check its required filenames, grading criteria, and submission rules.

~~~~

### Exchange 4 — Assistant

Timestamp: 2026-10-26T22:23:39.847Z

~~~~text
I reviewed the midterm. It’s **CSCI 363: Large Scale Programming**, with four questions totaling **100 points**. I haven’t implemented anything or changed GitHub yet.

One detail to reconcile with what your teacher said: **the written exam permits AI for Questions 1–3, but explicitly prohibits generating, editing, or evaluating Question 4 with AI.** My proposed plan follows that distinction unless the teacher has since changed that specific rule.

**What we need to deliver**

| Question | Points | Requirements |
|---|---:|---|
| **1: Library lending system** | 35 | Implement `Book` and `Library`, preserve the provided driver, add Javadoc, write a 3–5 sentence CRC explanation, and disclose resources in a development log. |
| **2: Object-oriented design evaluation** | 25 | Evaluate the supplied registration class using Arthur Riel’s heuristics; propose CRC cards; explain support for file/database storage and email/text confirmations; include a development log. **No Java implementation required.** |
| **3: Employee pay refactoring** | 30 | Implement an employee inheritance hierarchy with overriding and polymorphism, preserve the driver, add Javadoc, and include a development log. **Do not use a design pattern.** |
| **4: Personal AI reflection** | 10 | Your personal response covering benefits with a course example, challenges or limitations, and future AI use while developing independent skills. |

The details that matter most:

- **Q1:** New books start available; duplicate ISBNs are rejected; at most three books may be borrowed simultaneously. Invalid operations must throw the specified exceptions without changing state. Searches must handle availability, exact case-sensitive authors, and ascending ISBN order. The provided driver must report **44 passed, 0 failed**.
- **Q2:** The evaluation must connect actual problems in the supplied code to Riel’s heuristics. The CRC cards and evolving-requirements explanation must describe a consistent design.
- **Q3:** Full-time employees receive **1.5× pay for hours beyond 40**; part-time employees receive straight hourly pay; contractors receive a **15% premium**. The driver must report **12 passed, 0 failed**.
- **Documentation counts:** Passing both drivers alone earns only **38 of the 100 points**. Design, Javadoc, disclosures, and reflection make up the rest.
- **AI disclosure requires the full exchange**, not just “used ChatGPT.” Q1 and Q3 require transcripts; Q2 also accepts a working shareable conversation URL.

**My execution plan after your approval**

1. **Prepare the repository and exact folder structure.** Preserve any existing coursework and copy both instructor drivers without modification.
2. **Implement Q1** using an ISBN-keyed Java collection, encapsulated book state, and library-level borrowing enforcement. Write its Javadoc and CRC explanation.
3. **Complete Q2’s written deliverables.** Ground the evaluation in verified Riel references, using your lecture material if available so the terminology matches class.
4. **Implement Q3** with `Employee`, `FullTimeEmployee`, `PartTimeEmployee`, and `Contractor`, each in its own file.
5. **Maintain the development logs** with complete prompts/responses and accurate citations explaining how each resource helped.
6. **Compile and verify everything.** Run both unchanged drivers, check exception behavior and documentation, and exclude compiled files and IDE metadata.
7. **Include your independently written Q4 file**, then push and verify the final files on GitHub. Provide the repository link, final commit, test results, and submission checklist.

The required layout is:

```text
org/howard/edu/lsp/midterm/
├── crccards/
│   ├── Book.java
│   ├── Library.java
│   ├── Driver.java
│   └── doc/
│       ├── crccards.md
│       └── development_log.md
├── design/doc/
│   ├── evaluation.md
│   ├── design.md
│   ├── design_rationale.md
│   └── development_log_q2.md
├── inheritance/
│   ├── Employee.java
│   ├── FullTimeEmployee.java
│   ├── PartTimeEmployee.java
│   ├── Contractor.java
│   ├── Driver.java
│   └── doc/development_log_q3.md
└── reflection/doc/ai_reflection.md
```

**GitHub and submission**

Your authenticated account is **[Brian-Two](https://github.com/Brian-Two)**, and the CLI has repository access needed to create and push a repository. I also verified that you already own **[LSP_REPO_FALL2026](https://github.com/Brian-Two/LSP_REPO_FALL2026)** with push/admin access. I recommend using that existing course repository unless the instructor requested a separate midterm repository.

The document says to **push all required Java and Markdown files before the exam ends**; only repository contents at that time are graded. It does **not** provide an exact deadline, require an email/ZIP, or specify a separate LMS submission. We still need the exam’s closing time and any separate course-site instructions.

I’m ready to proceed with this plan when you give the okay.

<oai-mem-citation>
<citation_entries>
MEMORY.md:558-568|note=[Located prior LSP course context and verified current repository access]
</citation_entries>
<rollout_ids>
01a086ea-b4fa-7c70-853e-ce3b47f1d237
</rollout_ids>
</oai-mem-citation>
~~~~

### Exchange 5 — User

Timestamp: 2026-10-26T22:24:32.579Z

~~~~text
okay

~~~~

### Exchange 6 — Assistant

Timestamp: 2026-10-26T22:24:36.264Z

~~~~text
I’ll implement Questions 1–3 in your existing LSP repository, document the AI assistance, and verify both instructor drivers. I’ll also check the course submission details; Question 4 remains for you to write under the exam’s stated rule.

~~~~

### Exchange 7 — Assistant question

Timestamp: 2026-10-26T22:24:59.216Z

~~~~text
What is the midterm’s exact deadline, and did the instructor give any submission instructions beyond pushing to the course GitHub repository?
~~~~

### Exchange 8 — Assistant

Timestamp: 2026-10-26T22:25:40.789Z

~~~~text
The repository is clean, and I found no existing midterm files. I’m placing the work at the exact `org/howard/edu/lsp/midterm/` path specified by the exam and preserving both supplied drivers. I’m also recording sources as I go so the development logs include the required disclosure.

~~~~

### Exchange 9 — User

Timestamp: 2026-10-26T22:26:43.936Z

~~~~text
<send_user_message_question_reply>
[{"questionItemId":"[\"request_user_input_async\",\"call_545be1f81ce849798b7be87ae9868009\",0]","question":"What is the midterm’s exact deadline, and did the instructor give any submission instructions beyond pushing to the course GitHub repository?","answer":"8pm tonigth and email him th erepo link once fisohed he jsut sdoesn twnat us to make edits to our soltiuonn after 8"}]
</send_user_message_question_reply>

~~~~

### Exchange 10 — Assistant

Timestamp: 2026-10-26T22:26:55.726Z

~~~~text
Got it: the deadline is **8:00 p.m. tonight, October 26, Eastern**, with no solution edits afterward. Once the work is finished, I’ll email the repository link to the instructor after verifying his address from your course correspondence. Your independently written Question 4 is still needed before the full exam is complete.

~~~~

### Exchange 11 — Assistant question

Timestamp: 2026-10-26T22:28:54.180Z

~~~~text
What is your LSP instructor’s email address? I couldn’t verify it in the connected Gmail account, and I need the correct recipient for the submission email.
~~~~

### Exchange 12 — User

Timestamp: 2026-10-26T22:29:12.761Z

~~~~text
<send_user_message_question_reply>
[{"questionItemId":"[\"request_user_input_async\",\"call_590f28a7adea4b03b47652df4e21cf49\",0]","question":"What is your LSP instructor’s email address? I couldn’t verify it in the connected Gmail account, and I need the correct recipient for the submission email.","answer":"ont worry about it jsu tmaek me th edraf tonc eevryhting is said an done"}]
</send_user_message_question_reply>

~~~~

### Exchange 13 — Assistant

Timestamp: 2026-10-26T22:29:41.350Z

~~~~text
Both instructor drivers pass: **Q1: 44/44** and **Q3: 12/12**. Compilation and Javadoc checks also pass without warnings. The Q2 design documents are drafted; I’m checking their consistency and finishing the resource logs.

I’ll prepare an email draft for you to send, as requested.

~~~~

### Exchange 14 — Assistant

Timestamp: 2026-10-26T22:30:51.137Z

~~~~text
Questions 1–3 are complete and have passed the final checks, including 19 additional edge cases. I’ve created a blank `ai_reflection.md` for you to write Question 4 and prepared the submission email draft. I’m now adding the conversation transcripts to the development logs and pushing the completed work to GitHub.

~~~~
