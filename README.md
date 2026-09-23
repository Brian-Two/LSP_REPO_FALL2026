# LSP Fall 2026 Assignments

This repository contains coursework for LSP Fall 2026.

## Assignment 2: Employee Payroll ETL Pipeline

This plain-Java program reads `data/employees.csv`, transforms each valid payroll row, and writes `data/transformed_employees.csv`.

### Compile and run

From the repository root:

```text
javac -d out src/org/howard/edu/lsp/assignment2/ETLPipeline.java
java -cp out org.howard.edu.lsp.assignment2.ETLPipeline
```

### AI/Internet disclosure

OpenAI Codex was used to help interpret the assignment specification, implement the ETL pipeline, and verify the required output.

## Assignment 3: Object-Oriented Refactoring

Assignment 3 preserves the Assignment 2 payroll behavior while separating CSV parsing, payroll calculations, file processing, output records, and run reporting into focused classes.

### Compile and run

From the repository root:

```text
javac -d out src/org/howard/edu/lsp/assignment3/*.java
java -cp out org.howard.edu.lsp.assignment3.ETLPipeline
```

The design comparison and resource disclosure are in `src/org/howard/edu/lsp/assignment3/doc/DESIGN.md`.
