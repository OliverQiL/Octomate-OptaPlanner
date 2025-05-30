package org.acme.schooltimetabling.domain;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

/**
 * IMPROVED Shift domain with separate date and time handling
 * This allows for more precise constraint checking
 */
@PlanningEntity
public class Shift {

    @PlanningId
    private String shiftDayId; // Unique ID (may include "_opening_X" suffix)

    private String originalShiftDayId; // Original ID for output mapping
    private String shiftPatternId; // Pattern type
    private String shiftPatternName; // Human-readable name

    // SEPARATE DATE AND TIME for better constraint handling
    private LocalDate shiftDate; // Just the date (2025-05-26)
    private String shiftTime; // Time range string ("08:30 Am - 09:30 Pm")
    private LocalTime startTime; // Parsed start time (08:30)
    private LocalTime endTime; // Parsed end time (21:30)

    private int openings = 1; // Always 1 for individual shifts
    private int currentNumConfirmedShifts = 0; // Always 0 for unassigned shifts

    /**
     * The employee assigned to this shift (OptaPlanner will modify this)
     */
    @PlanningVariable
    private Employee assignedEmployee;

    // Constructors
    public Shift() {
    }

    public Shift(String shiftDayId, String originalShiftDayId, String shiftPatternId,
            String shiftPatternName, String shiftTime, LocalDate shiftDate) {
        this.shiftDayId = shiftDayId;
        this.originalShiftDayId = originalShiftDayId;
        this.shiftPatternId = shiftPatternId;
        this.shiftPatternName = shiftPatternName;
        this.shiftTime = shiftTime;
        this.shiftDate = shiftDate;

        // Parse start and end times from shiftTime string
        parseShiftTimes();
    }

    /**
     * Parse start and end times from shiftTime string
     * Example: "08:30 Am - 09:30 Pm" -> startTime: 08:30, endTime: 21:30
     */
    private void parseShiftTimes() {
        if (shiftTime == null || !shiftTime.contains(" - ")) {
            System.err.println("Invalid shift time format: " + shiftTime);
            return;
        }

        try {
            String[] parts = shiftTime.split(" - ");
            if (parts.length != 2) {
                System.err.println("Could not split shift time: " + shiftTime);
                return;
            }

            this.startTime = parseTimeString(parts[0].trim());
            this.endTime = parseTimeString(parts[1].trim());

            // Handle overnight shifts (end time before start time)
            if (endTime.isBefore(startTime)) {
                System.out.println("Detected overnight shift: " + shiftTime);
                // For overnight shifts, you might want to adjust the logic
                // For now, we'll keep as-is but log it
            }

        } catch (Exception e) {
            System.err.println("Error parsing shift times from: " + shiftTime + " - " + e.getMessage());
        }
    }

    /**
     * Parse individual time string like "08:30 Am" or "09:30 Pm"
     */
    private LocalTime parseTimeString(String timeStr) {
        try {
            // Handle different formats
            timeStr = timeStr.replace("Am", "AM").replace("Pm", "PM");

            if (timeStr.contains("AM") || timeStr.contains("PM")) {
                // 12-hour format
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
                return LocalTime.parse(timeStr, formatter);
            } else {
                // 24-hour format fallback
                return LocalTime.parse(timeStr);
            }
        } catch (Exception e) {
            System.err.println("Could not parse time: " + timeStr + " - " + e.getMessage());
            return LocalTime.of(0, 0); // Default fallback
        }
    }

    // Getters and Setters
    public String getShiftDayId() {
        return shiftDayId;
    }

    public void setShiftDayId(String shiftDayId) {
        this.shiftDayId = shiftDayId;
    }

    public String getOriginalShiftDayId() {
        return originalShiftDayId;
    }

    public void setOriginalShiftDayId(String originalShiftDayId) {
        this.originalShiftDayId = originalShiftDayId;
    }

    public String getShiftPatternId() {
        return shiftPatternId;
    }

    public void setShiftPatternId(String shiftPatternId) {
        this.shiftPatternId = shiftPatternId;
    }

    public String getShiftPatternName() {
        return shiftPatternName;
    }

    public void setShiftPatternName(String shiftPatternName) {
        this.shiftPatternName = shiftPatternName;
    }

    public LocalDate getShiftDate() {
        return shiftDate;
    }

    public void setShiftDate(LocalDate shiftDate) {
        this.shiftDate = shiftDate;
    }

    public String getShiftTime() {
        return shiftTime;
    }

    public void setShiftTime(String shiftTime) {
        this.shiftTime = shiftTime;
        parseShiftTimes(); // Re-parse when time string changes
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public int getOpenings() {
        return openings;
    }

    public void setOpenings(int openings) {
        this.openings = openings;
    }

    public int getCurrentNumConfirmedShifts() {
        return currentNumConfirmedShifts;
    }

    public void setCurrentNumConfirmedShifts(int currentNumConfirmedShifts) {
        this.currentNumConfirmedShifts = currentNumConfirmedShifts;
    }

    public Employee getAssignedEmployee() {
        return assignedEmployee;
    }

    public void setAssignedEmployee(Employee assignedEmployee) {
        this.assignedEmployee = assignedEmployee;
    }

    /**
     * Check if this shift overlaps with another shift's time
     * (assumes both shifts are on the same date)
     */
    public boolean overlapsTime(Shift other) {
        if (this.startTime == null || this.endTime == null ||
                other.startTime == null || other.endTime == null) {
            return false;
        }

        // Check if time ranges overlap
        return this.startTime.isBefore(other.endTime) && other.startTime.isBefore(this.endTime);
    }

    /**
     * Check if this shift is on the same date as another shift
     */
    public boolean isSameDate(Shift other) {
        return this.shiftDate != null && this.shiftDate.equals(other.shiftDate);
    }

    /**
     * Check if this shift conflicts with another (same date AND overlapping time)
     */
    public boolean conflictsWith(Shift other) {
        return isSameDate(other) && overlapsTime(other);
    }

    /**
     * Get formatted date for output (e.g., "26/May/2025")
     */
    public String getFormattedDate() {
        if (shiftDate == null)
            return "";
        return shiftDate.format(DateTimeFormatter.ofPattern("dd/MMM/yyyy"));
    }

    @Override
    public String toString() {
        return "Shift{" +
                "shiftDayId='" + shiftDayId + '\'' +
                ", shiftPatternName='" + shiftPatternName + '\'' +
                ", date=" + shiftDate +
                ", time='" + shiftTime + '\'' +
                ", assignedEmployee=" + (assignedEmployee != null ? assignedEmployee.getAssignmentId() : "None") +
                '}';
    }
}