package org.howard.edu.lsp.assignment3;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Holds one transformed payroll result and formats it for CSV output.
 */
final class PayrollRecord {
    private final Employee employee;
    private final BigDecimal grossPay;
    private final String payLevel;
    private final String employmentStatus;

    PayrollRecord(
            Employee employee,
            BigDecimal grossPay,
            String payLevel,
            String employmentStatus) {
        this.employee = employee;
        this.grossPay = grossPay;
        this.payLevel = payLevel;
        this.employmentStatus = employmentStatus;
    }

    String toCsvRow() {
        return employee.getEmployeeId() + ","
                + employee.getName() + ","
                + employee.getDepartment() + ","
                + formatDecimal(employee.getHoursWorked()) + ","
                + formatDecimal(employee.getHourlyRate()) + ","
                + formatDecimal(grossPay) + ","
                + payLevel + ","
                + employmentStatus;
    }

    private String formatDecimal(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}
