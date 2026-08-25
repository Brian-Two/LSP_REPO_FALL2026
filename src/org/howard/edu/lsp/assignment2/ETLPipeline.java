package org.howard.edu.lsp.assignment2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

/**
 * Extracts employee payroll data, transforms each valid row, and loads the
 * results into a new CSV file.
 *
 * @author Brian Too
 */
public class ETLPipeline {
    private static final Path INPUT_PATH = Paths.get("data/employees.csv");
    private static final Path OUTPUT_PATH = Paths.get("data/transformed_employees.csv");

    private static final String OUTPUT_HEADER =
            "EmployeeID,Name,Department,HoursWorked,HourlyRate,GrossPay,"
                    + "PayLevel,EmploymentStatus";

    private static final BigDecimal THIRTY = new BigDecimal("30.00");
    private static final BigDecimal FORTY = new BigDecimal("40.00");
    private static final BigDecimal OVERTIME_MULTIPLIER = new BigDecimal("1.5");
    private static final BigDecimal IT_BONUS_MULTIPLIER = new BigDecimal("1.05");
    private static final BigDecimal FIVE_HUNDRED = new BigDecimal("500.00");
    private static final BigDecimal ONE_THOUSAND = new BigDecimal("1000.00");
    private static final BigDecimal TWO_THOUSAND = new BigDecimal("2000.00");

    /**
     * Runs the complete ETL process using the required relative file paths.
     *
     * @param args command-line arguments; not used by this program
     */
    public static void main(String[] args) {
        int rowsRead = 0;
        int rowsTransformed = 0;
        int rowsSkipped = 0;

        try {
            Path outputDirectory = OUTPUT_PATH.getParent();
            if (outputDirectory != null) {
                Files.createDirectories(outputDirectory);
            }

            try (BufferedReader reader = Files.newBufferedReader(INPUT_PATH, StandardCharsets.UTF_8);
                    BufferedWriter writer = Files.newBufferedWriter(
                            OUTPUT_PATH, StandardCharsets.UTF_8)) {

                writer.write(OUTPUT_HEADER);
                writer.newLine();

                // The first input row is the header and is not transformed.
                reader.readLine();

                String line;
                while ((line = reader.readLine()) != null) {
                    rowsRead++;

                    String transformedRow = transform(line);
                    if (transformedRow == null) {
                        rowsSkipped++;
                        continue;
                    }

                    writer.write(transformedRow);
                    writer.newLine();
                    rowsTransformed++;
                }
            }

            System.out.println("Rows read: " + rowsRead);
            System.out.println("Rows transformed: " + rowsTransformed);
            System.out.println("Rows skipped: " + rowsSkipped);
            System.out.println("Output file: " + OUTPUT_PATH);
        } catch (IOException exception) {
            System.err.println("Unable to complete the ETL process: " + exception.getMessage());
        }
    }

    /**
     * Normalizes, validates, and transforms one non-header input row.
     *
     * @param line a row from the input CSV file
     * @return the transformed CSV row, or {@code null} when the row is invalid
     */
    private static String transform(String line) {
        if (line.trim().isEmpty()) {
            return null;
        }

        String[] fields = line.split(",", -1);
        if (fields.length != 5) {
            return null;
        }

        for (int index = 0; index < fields.length; index++) {
            fields[index] = fields[index].trim();
        }

        int employeeId;
        BigDecimal hoursWorked;
        BigDecimal hourlyRate;

        try {
            employeeId = Integer.parseInt(fields[0]);
            hoursWorked = new BigDecimal(fields[3]);
            hourlyRate = new BigDecimal(fields[4]);
        } catch (NumberFormatException exception) {
            return null;
        }

        if (hoursWorked.signum() < 0 || hourlyRate.signum() < 0) {
            return null;
        }

        String name = fields[1].toUpperCase(Locale.ROOT);
        String department = fields[2];

        BigDecimal grossPay = calculatePay(hoursWorked, hourlyRate);
        if ("IT".equals(department)) {
            grossPay = grossPay.multiply(IT_BONUS_MULTIPLIER);
        }
        grossPay = grossPay.setScale(2, RoundingMode.HALF_UP);

        String payLevel = determinePayLevel(grossPay);
        String employmentStatus = hoursWorked.compareTo(THIRTY) < 0
                ? "Part-Time"
                : "Full-Time";

        return employeeId + ","
                + name + ","
                + department + ","
                + formatDecimal(hoursWorked) + ","
                + formatDecimal(hourlyRate) + ","
                + formatDecimal(grossPay) + ","
                + payLevel + ","
                + employmentStatus;
    }

    /**
     * Calculates normal and overtime pay before any department bonus.
     */
    private static BigDecimal calculatePay(BigDecimal hoursWorked, BigDecimal hourlyRate) {
        if (hoursWorked.compareTo(FORTY) <= 0) {
            return hoursWorked.multiply(hourlyRate);
        }

        BigDecimal regularPay = FORTY.multiply(hourlyRate);
        BigDecimal overtimeHours = hoursWorked.subtract(FORTY);
        BigDecimal overtimePay = overtimeHours
                .multiply(hourlyRate)
                .multiply(OVERTIME_MULTIPLIER);

        return regularPay.add(overtimePay);
    }

    /**
     * Determines the pay level from the final rounded gross pay.
     */
    private static String determinePayLevel(BigDecimal grossPay) {
        if (grossPay.compareTo(FIVE_HUNDRED) < 0) {
            return "Low";
        }
        if (grossPay.compareTo(ONE_THOUSAND) < 0) {
            return "Standard";
        }
        if (grossPay.compareTo(TWO_THOUSAND) < 0) {
            return "High";
        }
        return "Executive";
    }

    /**
     * Formats a decimal value with exactly two digits after the decimal point.
     */
    private static String formatDecimal(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}
