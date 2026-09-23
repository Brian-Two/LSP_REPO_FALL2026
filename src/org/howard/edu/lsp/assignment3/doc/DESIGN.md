# Assignment 3 Design Discussion

## Assignment 2 Organization

Assignment 2 placed file input and output, CSV parsing, validation, payroll calculations, output formatting, counters, and console reporting in one `ETLPipeline` class. Helper methods separated a few calculations, but the class still had several responsibilities.

## Assignment 3 Design Changes

Assignment 3 keeps `ETLPipeline` as the required entry point and moves the work into objects with focused responsibilities. `PayrollFileProcessor` coordinates the extract and load steps. It delegates input parsing and validation to `EmployeeParser`, payroll rules to `PayrollCalculator`, CSV output formatting to `PayrollRecord`, and run reporting to `ProcessingSummary`.

## Classes and Abstractions

- `Employee` represents one normalized, validated employee input record and encapsulates its fields.
- `EmployeeParser` converts a CSV line into an `Employee` or reports that the row is invalid.
- `PayrollCalculator` applies regular pay, overtime, the IT bonus, rounding, pay-level, and employment-status rules.
- `PayrollRecord` represents a completed transformation and produces the required CSV row.
- `PayrollFileProcessor` owns the file-processing workflow and row counts.
- `ProcessingSummary` stores and prints the required run results.

## Division of Responsibilities

The Assignment 2 class made every ETL decision itself. In Assignment 3, each object has one main reason to change: parsing rules, payroll rules, CSV representation, file workflow, or summary reporting. `ETLPipeline` only creates the collaborating objects and starts the process.

## Why the Design Is an Improvement

The refactor separates data, business rules, file handling, and reporting while preserving the Assignment 2 behavior. Each part can be understood and tested independently, and a change to one responsibility is less likely to affect unrelated code. The classes model actual records and steps in the payroll ETL process rather than existing only to increase the class count.

## AI and External Resources

OpenAI Codex was used to interpret the assignment requirements, help refactor the program, and verify the implementation. [AI interaction transcript](https://chatgpt.com/s/cx_6ab41cecf41c8191bdb843c3a5cc476c)

No other Internet resources were used.
