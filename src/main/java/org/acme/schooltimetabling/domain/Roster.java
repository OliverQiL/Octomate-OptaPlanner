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
 * A roster is defined by a list of employees and a list of shifts.
 * It is a planning solution for the Optaplanner framework.
 * The roster can be solved to find an optimal assignment of shifts to employees.
 */
@PlanningSolution
public class Roster {

    @ValueRangeProvider
    @ProblemFactCollectionProperty
    private List<Employee> employeeList;

    @PlanningEntityCollectionProperty
    private List<Shift> shiftList;

    @PlanningScore
    private HardSoftScore score;

    public Roster() {
        // Default constructor
    }

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
}
