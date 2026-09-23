package org.howard.edu.lsp.assignment3;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Starts the Assignment 3 object-oriented employee payroll ETL pipeline.
 *
 * @author Brian Too
 */
public class ETLPipeline {
    private static final Path INPUT_PATH = Paths.get("data/employees.csv");
    private static final Path OUTPUT_PATH = Paths.get("data/transformed_employees.csv");

    /**
     * Runs the complete ETL process using the required relative file paths.
     *
     * @param args command-line arguments; not used by this program
     */
    public static void main(String[] args) {
        EmployeeParser employeeParser = new EmployeeParser();
        PayrollCalculator payrollCalculator = new PayrollCalculator();
        PayrollFileProcessor processor = new PayrollFileProcessor(
                INPUT_PATH,
                OUTPUT_PATH,
                employeeParser,
                payrollCalculator);

        try {
            ProcessingSummary summary = processor.process();
            summary.printTo(System.out);
        } catch (IOException exception) {
            System.err.println("Unable to complete the ETL process: " + exception.getMessage());
        }
    }
}
