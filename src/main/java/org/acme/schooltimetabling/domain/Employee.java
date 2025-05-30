package org.acme.schooltimetabling.domain;

import java.util.List;

/**
 * SIMPLIFIED Employee domain - only what's needed for optimization
 * Data filtering/validation is handled in main backend
 */
public class Employee {

    private String assignmentId;     // For output mapping
    private String associateId;      // For constraint matching  
    private List<String> shiftPatternIds; // Which patterns this employee can work
    private String jobOrderId;       // Must match shift's role requirements

    // Constructors
    public Employee() {}

    public Employee(String assignmentId, String associateId, List<String> shiftPatternIds, String jobOrderId) {
        this.assignmentId = assignmentId;
        this.associateId = associateId;
        this.shiftPatternIds = shiftPatternIds;
        this.jobOrderId = jobOrderId;
    }

    // Getters and Setters
    public String getAssignmentId() { return assignmentId; }
    public void setAssignmentId(String assignmentId) { this.assignmentId = assignmentId; }

    public String getAssociateId() { return associateId; }
    public void setAssociateId(String associateId) { this.associateId = associateId; }

    public List<String> getShiftPatternIds() { return shiftPatternIds; }
    public void setShiftPatternIds(List<String> shiftPatternIds) { this.shiftPatternIds = shiftPatternIds; }

    public String getJobOrderId() { return jobOrderId; }
    public void setJobOrderId(String jobOrderId) { this.jobOrderId = jobOrderId; }

    /**
     * Check if employee can work a specific shift pattern
     */
    public boolean canWorkShiftPattern(String shiftPatternId) {
        return shiftPatternIds != null && shiftPatternIds.contains(shiftPatternId);
    }

    @Override
    public String toString() {
        return "Employee{assignmentId='" + assignmentId + "', associateId='" + associateId + "'}";
    }
}