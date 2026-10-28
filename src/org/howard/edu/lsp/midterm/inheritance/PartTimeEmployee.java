package org.howard.edu.lsp.midterm.inheritance;

/**
 * Pays the base hourly rate for every hour, without an overtime premium.
 *
 * @author Brian Too
 */
public class PartTimeEmployee extends Employee {
    /**
     * Creates a part-time employee's weekly pay record.
     * @param hoursWorked nonnegative weekly hours
     * @param hourlyRate nonnegative base hourly rate
     */
    public PartTimeEmployee(double hoursWorked, double hourlyRate) {
        super(hoursWorked, hourlyRate);
    }

    /**
     * Calculates straight hourly pay for all hours.
     * @return hours worked multiplied by the hourly rate
     */
    @Override
    public double calculatePay() {
        return getHoursWorked() * getHourlyRate();
    }
}
