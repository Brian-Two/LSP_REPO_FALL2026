package org.howard.edu.lsp.assignment3;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Applies the payroll business rules to a validated employee.
 */
final class PayrollCalculator {
    private static final BigDecimal THIRTY = new BigDecimal("30.00");
    private static final BigDecimal FORTY = new BigDecimal("40.00");
    private static final BigDecimal OVERTIME_MULTIPLIER = new BigDecimal("1.5");
    private static final BigDecimal IT_BONUS_MULTIPLIER = new BigDecimal("1.05");
    private static final BigDecimal FIVE_HUNDRED = new BigDecimal("500.00");
    private static final BigDecimal ONE_THOUSAND = new BigDecimal("1000.00");
    private static final BigDecimal TWO_THOUSAND = new BigDecimal("2000.00");

    PayrollRecord calculate(Employee employee) {
        BigDecimal grossPay = calculateBaseAndOvertimePay(employee);

        if ("IT".equals(employee.getDepartment())) {
            grossPay = grossPay.multiply(IT_BONUS_MULTIPLIER);
        }

        grossPay = grossPay.setScale(2, RoundingMode.HALF_UP);
        String payLevel = determinePayLevel(grossPay);
        String employmentStatus = employee.getHoursWorked().compareTo(THIRTY) < 0
                ? "Part-Time"
                : "Full-Time";

        return new PayrollRecord(employee, grossPay, payLevel, employmentStatus);
    }

    private BigDecimal calculateBaseAndOvertimePay(Employee employee) {
        BigDecimal hoursWorked = employee.getHoursWorked();
        BigDecimal hourlyRate = employee.getHourlyRate();

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

    private String determinePayLevel(BigDecimal grossPay) {
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
}
