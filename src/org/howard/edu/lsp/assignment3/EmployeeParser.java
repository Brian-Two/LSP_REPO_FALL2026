package org.howard.edu.lsp.assignment3;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Optional;

/**
 * Converts a CSV input line into a validated {@link Employee}.
 */
final class EmployeeParser {

    Optional<Employee> parse(String line) {
        if (line == null || line.trim().isEmpty()) {
            return Optional.empty();
        }

        String[] fields = line.split(",", -1);
        if (fields.length != 5) {
            return Optional.empty();
        }

        for (int index = 0; index < fields.length; index++) {
            fields[index] = fields[index].trim();
        }

        try {
            int employeeId = Integer.parseInt(fields[0]);
            BigDecimal hoursWorked = new BigDecimal(fields[3]);
            BigDecimal hourlyRate = new BigDecimal(fields[4]);

            if (hoursWorked.signum() < 0 || hourlyRate.signum() < 0) {
                return Optional.empty();
            }

            Employee employee = new Employee(
                    employeeId,
                    fields[1].toUpperCase(Locale.ROOT),
                    fields[2],
                    hoursWorked,
                    hourlyRate);
            return Optional.of(employee);
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }
}
