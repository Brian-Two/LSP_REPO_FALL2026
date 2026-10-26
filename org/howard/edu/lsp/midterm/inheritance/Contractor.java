package org.howard.edu.lsp.midterm.inheritance;

/**
 * Pays straight hourly earnings plus a fifteen-percent premium on all hours.
 *
 * @author Brian Too
 */
public class Contractor extends Employee {
    /**
     * Creates a contractor's weekly pay record.
     * @param hoursWorked nonnegative weekly hours
     * @param hourlyRate nonnegative base hourly rate
     */
    public Contractor(double hoursWorked, double hourlyRate) {
        super(hoursWorked, hourlyRate);
    }

    /**
     * Calculates hourly pay with the contractor premium.
     * @return hours worked times the hourly rate times 1.15
     */
    @Override
    public double calculatePay() {
        return getHoursWorked() * getHourlyRate() * 1.15;
    }
}
