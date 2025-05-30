package org.acme.schooltimetabling.domain;

import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

/**
 * Represents a roster for scheduling shifts for employees.
 * This is the main planning solution that OptaPlanner will optimize.
 * 
 * Contains:
 * - A list of available employees (problem facts)
 * - A list of shift openings that need to be filled (planning entities)
 * - The calculated score representing the quality of the solution
 */
@PlanningSolution
public class Roster {

    /**
     * List of available employees who can be assigned to shifts.
     * This is a value range provider - OptaPlanner will pick from these
     * employees when assigning them to shift planning variables.
     * 
     * These are problem facts because they don't change during solving.
     */
    @ValueRangeProvider
    @ProblemFactCollectionProperty
    private List<Employee> employeeList;

    /**
     * List of individual shift openings that need to be filled.
     * Each Shift object represents ONE position that needs ONE employee.
     * 
     * These are planning entities because OptaPlanner will modify them
     * by assigning employees to the assignedEmployee planning variable.
     */
    @PlanningEntityCollectionProperty
    private List<Shift> shiftList;

    /**
     * The score representing the quality of this roster solution.
     * Higher scores are better. Hard constraints must be satisfied (score >=
     * 0hard).
     * Soft constraints are preferences that improve the score.
     */
    @PlanningScore
    private HardSoftScore score;

    public Roster() {
        // Default constructor required by OptaPlanner
    }

    /**
     * Constructor for creating a new roster problem
     */
    public Roster(List<Employee> employeeList, List<Shift> shiftList) {
        this.employeeList = employeeList;
        this.shiftList = shiftList;
    }

    // Getters
    public List<Employee> getEmployeeList() {
        return employeeList;
    }

    public List<Shift> getShiftList() {
        return shiftList;
    }

    public HardSoftScore getScore() {
        return score;
    }

    // Setters
    public void setEmployeeList(List<Employee> employeeList) {
        this.employeeList = employeeList;
    }

    public void setShiftList(List<Shift> shiftList) {
        this.shiftList = shiftList;
    }

    public void setScore(HardSoftScore score) {
        this.score = score;
    }

    /**
     * Gets the number of shifts that have been assigned to employees
     */
    public int getAssignedShiftCount() {
        if (shiftList == null)
            return 0;
        return (int) shiftList.stream()
                .filter(shift -> shift.getAssignedEmployee() != null)
                .count();
    }

    /**
     * Gets the total number of shift openings to be filled
     */
    public int getTotalShiftCount() {
        return shiftList != null ? shiftList.size() : 0;
    }

    /**
     * Gets the number of unassigned shifts
     */
    public int getUnassignedShiftCount() {
        return getTotalShiftCount() - getAssignedShiftCount();
    }

    /**
     * Checks if this roster solution is complete (all shifts assigned)
     */
    public boolean isComplete() {
        return getUnassignedShiftCount() == 0;
    }

    /**
     * Checks if this roster solution is feasible (no hard constraint violations)
     */
    public boolean isFeasible() {
        return score != null && score.isFeasible();
    }

    @Override
    public String toString() {
        return "Roster{" +
                "employees=" + (employeeList != null ? employeeList.size() : 0) +
                ", shifts=" + getTotalShiftCount() +
                ", assigned=" + getAssignedShiftCount() +
                ", unassigned=" + getUnassignedShiftCount() +
                ", score=" + score +
                '}';
    }
}