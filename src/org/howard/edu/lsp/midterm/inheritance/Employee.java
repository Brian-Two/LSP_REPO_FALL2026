package org.howard.edu.lsp.midterm.inheritance;

/**
 * Represents an employee's weekly hours and rate, with pay defined by subclasses.
 * Hours and rates are assumed nonnegative as specified by the exam.
 *
 * @author Brian Too
 */
public abstract class Employee {
    private final double hoursWorked;
    private final double hourlyRate;

    /**
     * Initializes the values shared by all employee types.
     * @param hoursWorked nonnegative hours worked during the week
     * @param hourlyRate nonnegative base hourly rate
     */
    protected Employee(double hoursWorked, double hourlyRate) {
        this.hoursWorked = hoursWorked;
        this.hourlyRate = hourlyRate;
    }

    /**
     * Supplies the week's hours to subclasses without exposing mutable fields.
     * @return the hours worked
     */
    protected final double getHoursWorked() {
        return hoursWorked;
    }

    /**
     * Supplies the base rate to subclasses.
     * @return the hourly rate
     */
    protected final double getHourlyRate() {
        return hourlyRate;
    }

    /**
     * Calculates weekly pay according to the concrete employee's rules.
     * @return the weekly pay
     */
    public abstract double calculatePay();
}
