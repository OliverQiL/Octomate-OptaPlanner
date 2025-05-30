package org.acme.schooltimetabling.domain;

import java.util.List;

/**
 * Represents an employee.
 * An employee is defined by a unique ID and a name.
 * This class is immutable after creation, meaning that
 * once an Employee object is created, its ID and name cannot be changed.
 */
public class Employee {

    private String assignmentId; // "_id" from assignments - needed for output
    private String associateId; // for constraint matching
    private List<String> shiftPatternIds; // Which shift patterns this employee can work
    private String jobOrderId; // must match roleId in Shift

    public Employee() {
        // Default constructor
    }

    public Employee(String assignmentId, String associateId, List<String> shiftPatternIds, String jobOrderId) {
        this.assignmentId = assignmentId;
        this.associateId = associateId;
        this.shiftPatternIds = shiftPatternIds;
        this.jobOrderId = jobOrderId;
    }

    // Getters
    public String getAssignmentId() {
        return assignmentId;
    }

    public String getAssociateId() {
        return associateId;
    }

    public List<String> getShiftPatternIds() {
        return shiftPatternIds;
    }

    public String getJobOrderId() {
        return jobOrderId;
    }

    // Setters for JSON deserialization
    public void setAssignmentId(String assignmentId) {
        this.assignmentId = assignmentId;
    }

    public void setAssociateId(String associateId) {
        this.associateId = associateId;
    }

    public void setShiftPatternIds(List<String> shiftPatternIds) {
        this.shiftPatternIds = shiftPatternIds;
    }

    public void setJobOrderId(String jobOrderId) {
        this.jobOrderId = jobOrderId;
    }

    /**
     * Checks if this employee can work a given shift pattern.
     */
    public boolean canWorkShiftPattern(String shiftPatternId) {
        return shiftPatternIds != null && shiftPatternIds.contains(shiftPatternId);
    }

    /**
     * Returns a unique identifier for this employee for Optaplanner.
     */
    public String getUniqueId() {
        return assignmentId; // Using assignmentId as the unique identifier
    }

    @Override
    public String toString() {
        return "Employee{" +
                "assignmentId='" + assignmentId + '\'' +
                ", associateId='" + associateId + '\'' +
                ", shiftPatternIds=" + shiftPatternIds +
                '}';
    }
}
