package org.howard.edu.lsp.midterm.inheritance;

/**
 * Pays the base rate through 40 hours and time-and-a-half for additional hours.
 *
 * @author Brian Too
 */
public class FullTimeEmployee extends Employee {
    /**
     * Creates a full-time employee's weekly pay record.
     * @param hoursWorked nonnegative weekly hours
     * @param hourlyRate nonnegative base hourly rate
     */
    public FullTimeEmployee(double hoursWorked, double hourlyRate) {
        super(hoursWorked, hourlyRate);
    }

    /**
     * Calculates straight-time pay plus any overtime above 40 hours.
     * @return weekly pay including overtime at 1.5 times the base rate
     */
    @Override
    public double calculatePay() {
        double regularHours = Math.min(getHoursWorked(), 40.0);
        double overtimeHours = Math.max(getHoursWorked() - 40.0, 0.0);
        return regularHours * getHourlyRate()
                + overtimeHours * getHourlyRate() * 1.5;
    }
}
