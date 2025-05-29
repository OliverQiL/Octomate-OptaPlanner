package org.acme.schooltimetabling.domain;

import java.time.LocalDateTime;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

/** 
 * Represents a shift.
 * A shift is defined by its unique ID, name, start and end times, job site,
 * and the employee assigned to it.
 * 
 * Planning Entity: Shift is a planning entity for Optaplanner.
 * It can be assigned to different employees during the planning process.
 */
@PlanningEntity
public class Shift {

    @PlanningId
    private Long id; // Unique identifier for the lesson

    private Long ShiftId;
    private String shiftName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String jobSite;

    /**
     * Planning variables are used by Optaplanner to determine the optimal
     * assignment of resources.
     * They are mutable and can change during the planning process.
     * In this case, the timeslot and room are planning variables for the lesson.
     * They will be assigned during the planning phase to find the best schedule.
     */
    @PlanningVariable
    private Employee assignedEmployee;

    public Shift() {
        // Default constructor
    }

    public Shift(Long id, Long shiftId, String shiftName, LocalDateTime startTime, LocalDateTime endTime,
            String jobSite) {
        this.id = id;
        this.ShiftId = shiftId;
        this.shiftName = shiftName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.jobSite = jobSite;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public Long getShiftId() {
        return ShiftId;
    }

    public String getShiftName() {
        return shiftName;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public String getJobSite() {
        return jobSite;
    }

    public Employee getAssignedEmployee() {
        return assignedEmployee;
    }

    // Setters - these are used by Optaplanner during the planning phase
    public void setAssignedEmployee(Employee assignedEmployee) {
        this.assignedEmployee = assignedEmployee;
    }

    @Override
    public String toString() {
        return "Shift{" +
                "id=" + id +
                ", ShiftId=" + ShiftId +
                ", shiftName='" + shiftName + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", jobSite='" + jobSite + '\'' +
                ", assignedEmployee=" + assignedEmployee +
                '}';
    }
}
