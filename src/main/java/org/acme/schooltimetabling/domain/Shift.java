package org.acme.schooltimetabling.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
    private String shiftDayId; // Within each shift pattern, shift day - eg. Monday, Tuesday, etc. - actually
                               // unique

    private String originalShiftDayId; // Original ID from the database - used for output
    private String shiftPatternId; // Shift type - eg. morning, evening, off-day - many shifts with same pattern
    private String shiftPatternName; // Name of the shift pattern - for easy reference

    private String shiftTime;
    private LocalDateTime date;

    private int openings; // Number of openings for this shift - always 1 after processing
    private int currentNumConfirmedShifts; // Number of confirmed shifts for this shift - always 0 after processing

    /**
     * Planning variables are used by Optaplanner to determine the optimal
     * assignment of resources.
     * They are mutable and can change during the planning process.
     * In this case, the assignedEmployee variable represents the employee
     * assigned to this shift.
     * They will be assigned during the planning phase to find the best schedule.
     */
    @PlanningVariable
    private Employee assignedEmployee;

    public Shift() {
        // Default constructor
    }

    public Shift(String shiftDayId, String shiftPatternId, String shiftPatternName,
            String shiftTime, LocalDateTime date, int openings, int currentNumConfirmedShifts) {
        this.shiftDayId = shiftDayId;
        this.originalShiftDayId = shiftDayId; // Default to same ID
        this.shiftPatternId = shiftPatternId;
        this.shiftPatternName = shiftPatternName;
        this.shiftTime = shiftTime;
        this.date = date;
        this.openings = openings;
        this.currentNumConfirmedShifts = currentNumConfirmedShifts;
    }

    // Constructor with original shift day ID for tracking multiple openings
    public Shift(String shiftDayId, String originalShiftDayId, String shiftPatternId,
            String shiftPatternName, String shiftTime, LocalDateTime date,
            int openings, int currentNumConfirmedShifts) {
        this.shiftDayId = shiftDayId;
        this.originalShiftDayId = originalShiftDayId;
        this.shiftPatternId = shiftPatternId;
        this.shiftPatternName = shiftPatternName;
        this.shiftTime = shiftTime;
        this.date = date;
        this.openings = openings;
        this.currentNumConfirmedShifts = currentNumConfirmedShifts;
    }

    // Getters
    public String getShiftDayId() {
        return shiftDayId;
    }

    public String getOriginalShiftDayId() {
        return originalShiftDayId;
    }

    public String getShiftPatternId() {
        return shiftPatternId;
    }

    public String getShiftPatternName() {
        return shiftPatternName;
    }

    public String getShiftTime() {
        return shiftTime;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public int getOpenings() {
        return openings;
    }

    public int getCurrentNumConfirmedShifts() {
        return currentNumConfirmedShifts;
    }

    public Employee getAssignedEmployee() {
        return assignedEmployee;
    }

    // Setters
    public void setShiftDayId(String shiftDayId) {
        this.shiftDayId = shiftDayId;
    }

    public void setOriginalShiftDayId(String originalShiftDayId) {
        this.originalShiftDayId = originalShiftDayId;
    }

    public void setShiftPatternId(String shiftPatternId) {
        this.shiftPatternId = shiftPatternId;
    }

    public void setShiftPatternName(String shiftPatternName) {
        this.shiftPatternName = shiftPatternName;
    }

    public void setShiftTime(String shiftTime) {
        this.shiftTime = shiftTime;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setOpenings(int openings) {
        this.openings = openings;
    }

    public void setCurrentNumConfirmedShifts(int currentNumConfirmedShifts) {
        this.currentNumConfirmedShifts = currentNumConfirmedShifts;
    }

    public void setAssignedEmployee(Employee assignedEmployee) {
        this.assignedEmployee = assignedEmployee;
    }

    /**
     * Gets the date formatted for output (e.g., "26/May/2025")
     */
    public String getFormattedDate() {
        if (date == null)
            return "";
        return date.format(DateTimeFormatter.ofPattern("dd/MMM/yyyy"));
    }

    /**
     * Checks if this shift can be worked by the given employee
     */
    public boolean canBeWorkedBy(Employee employee) {
        return employee != null && employee.canWorkShiftPattern(this.shiftPatternId);
    }

    @Override
    public String toString() {
        return "Shift{" +
                "shiftDayId='" + shiftDayId + '\'' +
                ", shiftPatternName='" + shiftPatternName + '\'' +
                ", shiftTime='" + shiftTime + '\'' +
                ", date=" + (date != null ? date.toLocalDate() : "null") +
                ", assignedEmployee=" + (assignedEmployee != null ? assignedEmployee.getUniqueId() : "None") +
                '}';
    }
}
