package org.howard.edu.lsp.assignment3;

import java.math.BigDecimal;

/**
 * Represents one normalized and validated employee payroll input record.
 */
final class Employee {
    private final int employeeId;
    private final String name;
    private final String department;
    private final BigDecimal hoursWorked;
    private final BigDecimal hourlyRate;

    Employee(
            int employeeId,
            String name,
            String department,
            BigDecimal hoursWorked,
            BigDecimal hourlyRate) {
        this.employeeId = employeeId;
        this.name = name;
        this.department = department;
        this.hoursWorked = hoursWorked;
        this.hourlyRate = hourlyRate;
    }

    int getEmployeeId() {
        return employeeId;
    }

    String getName() {
        return name;
    }

    String getDepartment() {
        return department;
    }

    BigDecimal getHoursWorked() {
        return hoursWorked;
    }

    BigDecimal getHourlyRate() {
        return hourlyRate;
    }
}
