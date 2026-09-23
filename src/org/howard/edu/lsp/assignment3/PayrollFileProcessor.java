package org.howard.edu.lsp.assignment3;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Coordinates extraction from the input file and loading to the output file.
 */
final class PayrollFileProcessor {
    private static final String OUTPUT_HEADER =
            "EmployeeID,Name,Department,HoursWorked,HourlyRate,GrossPay,"
                    + "PayLevel,EmploymentStatus";

    private final Path inputPath;
    private final Path outputPath;
    private final EmployeeParser employeeParser;
    private final PayrollCalculator payrollCalculator;

    PayrollFileProcessor(
            Path inputPath,
            Path outputPath,
            EmployeeParser employeeParser,
            PayrollCalculator payrollCalculator) {
        this.inputPath = inputPath;
        this.outputPath = outputPath;
        this.employeeParser = employeeParser;
        this.payrollCalculator = payrollCalculator;
    }

    ProcessingSummary process() throws IOException {
        int rowsRead = 0;
        int rowsTransformed = 0;
        int rowsSkipped = 0;

        Path outputDirectory = outputPath.getParent();
        if (outputDirectory != null) {
            Files.createDirectories(outputDirectory);
        }

        try (BufferedReader reader = Files.newBufferedReader(inputPath, StandardCharsets.UTF_8);
                BufferedWriter writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8)) {
            writer.write(OUTPUT_HEADER);
            writer.newLine();

            // The first input row is the header and is not transformed.
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                rowsRead++;

                Optional<Employee> employee = employeeParser.parse(line);
                if (!employee.isPresent()) {
                    rowsSkipped++;
                    continue;
                }

                PayrollRecord payrollRecord = payrollCalculator.calculate(employee.get());
                writer.write(payrollRecord.toCsvRow());
                writer.newLine();
                rowsTransformed++;
            }
        }

        return new ProcessingSummary(rowsRead, rowsTransformed, rowsSkipped, outputPath);
    }
}
