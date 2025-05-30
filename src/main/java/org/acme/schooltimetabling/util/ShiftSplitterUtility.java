package org.acme.schooltimetabling.util;

import java.util.ArrayList;
import java.util.List;

import org.acme.schooltimetabling.domain.Shift;

/**
 * Utility class for handling shift splitting operations in the auto-rostering system.
 * 
 * WORKFLOW INTEGRATION:
 * 1. Input: OpenShifts JSON → Parse to Shift objects with multiple openings
 * 2. Split: This utility splits each shift with N openings into N individual Shift instances
 * 3. Solve: OptaPlanner assigns one Employee to each individual Shift
 * 4. Output: Convert solved assignments back to AssignShift JSON format
 * 
 * WHY SPLIT SHIFTS?
 * OptaPlanner works best when each planning entity represents exactly one decision.
 * Instead of "assign multiple employees to one shift", we create multiple individual
 * shifts and assign "one employee to each individual shift".
 */
public class ShiftSplitterUtility {

    /**
     * STEP 2 in the workflow: Split shifts with multiple openings into individual instances.
     * 
     * Example:
     * Input:  1 shift with openings=10, confirmed=1
     * Output: 9 individual Shift objects (10-1=9 available spots)
     * 
     * Each individual shift represents exactly ONE position to be filled.
     */
    public static List<Shift> splitShiftsIntoIndividualOpenings(List<Shift> originalShifts) {
        List<Shift> individualShifts = new ArrayList<>();
        
        for (Shift originalShift : originalShifts) {
            // Calculate how many individual shifts we need to create
            int availableOpenings = calculateAvailableOpenings(originalShift);
            
            if (availableOpenings <= 0) {
                // Skip shifts that have no available openings
                continue;
            }
            
            // Create individual shift instances for each available opening
            for (int openingIndex = 0; openingIndex < availableOpenings; openingIndex++) {
                Shift individualShift = createIndividualShift(originalShift, openingIndex);
                individualShifts.add(individualShift);
            }
        }
        
        return individualShifts;
    }

    /**
     * Calculates available positions = total openings - already confirmed shifts
     */
    private static int calculateAvailableOpenings(Shift shift) {
        int totalOpenings = shift.getOpenings();
        int confirmedShifts = shift.getCurrentNumConfirmedShifts();
        int availableOpenings = totalOpenings - confirmedShifts;
        
        // Ensure we never return negative openings
        return Math.max(0, availableOpenings);
    }

    /**
     * Creates one individual shift instance from the original shift.
     * 
     * Key Design Decisions:
     * - individualShiftId: Unique ID for OptaPlanner (prevents conflicts)
     * - originalShiftDayId: Preserved for output mapping back to API format
     * - openings: Always 1 (each individual shift = 1 position)
     * - confirmed: Always 0 (each individual shift starts unassigned)
     */
    private static Shift createIndividualShift(Shift originalShift, int openingIndex) {
        // Create unique ID for this individual shift instance
        String individualShiftId = generateIndividualShiftId(
            originalShift.getShiftDayId(), 
            openingIndex
        );
        
        // Create new shift instance with unique ID
        Shift individualShift = new Shift(
            individualShiftId,                           // Unique shiftDayId for this opening
            originalShift.getShiftDayId(),               // Original shiftDayId for mapping back
            originalShift.getShiftPatternId(),
            originalShift.getShiftPatternName(),
            originalShift.getShiftTime(),
            originalShift.getDate(),
            1,                                           // Each individual shift has exactly 1 opening
            0                                            // Each individual shift starts with 0 confirmed
        );
        
        return individualShift;
    }

    /**
     * Generates unique IDs to prevent OptaPlanner conflicts.
     * Format: "originalId_opening_0", "originalId_opening_1", etc.
     */
    private static String generateIndividualShiftId(String originalShiftDayId, int openingIndex) {
        return originalShiftDayId + "_opening_" + openingIndex;
    }

    /**
     * STEP 4 in the workflow: Convert solved individual shifts back to AssignShift format.
     * 
     * This maps from OptaPlanner's solution back to your API's expected JSON structure.
     * Only includes shifts that have been assigned to employees.
     */
    public static List<ShiftAssignmentOutput> groupAssignmentsForOutput(List<Shift> individualShifts) {
        List<ShiftAssignmentOutput> outputAssignments = new ArrayList<>();
        
        for (Shift individualShift : individualShifts) {
            if (individualShift.getAssignedEmployee() != null) {
                ShiftAssignmentOutput output = new ShiftAssignmentOutput(
                    individualShift.getAssignedEmployee().getAssignmentId(),
                    individualShift.getFormattedDate(),
                    individualShift.getShiftTime(),
                    individualShift.getOriginalShiftDayId(),  // Use original ID for output
                    individualShift.getShiftPatternId()
                );
                outputAssignments.add(output);
            }
        }
        
        return outputAssignments;
    }

    /**
     * Output data structure that matches your AssignShift JSON format.
     * 
     * FIXED: Field name is now 'shiftDayId' to match your API spec
     */
    public static class ShiftAssignmentOutput {
        private String assignmentId;
        private String date;
        private String shiftTime;
        private String shiftDayId;      // FIXED: Was originalShiftDayId
        private String shiftPatternId;

        public ShiftAssignmentOutput(String assignmentId, String date, String shiftTime, 
                                     String shiftDayId, String shiftPatternId) {
            this.assignmentId = assignmentId;
            this.date = date;
            this.shiftTime = shiftTime;
            this.shiftDayId = shiftDayId;
            this.shiftPatternId = shiftPatternId;
        }

        // Getters
        public String getAssignmentId() {
            return assignmentId;
        }

        public String getDate() {
            return date;
        }

        public String getShiftTime() {
            return shiftTime;
        }

        public String getShiftDayId() {        // FIXED: Method name
            return shiftDayId;
        }

        public String getShiftPatternId() {
            return shiftPatternId;
        }

        // Setters
        public void setAssignmentId(String assignmentId) {
            this.assignmentId = assignmentId;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public void setShiftTime(String shiftTime) {
            this.shiftTime = shiftTime;
        }

        public void setShiftDayId(String shiftDayId) {     // FIXED: Method name
            this.shiftDayId = shiftDayId;
        }

        public void setShiftPatternId(String shiftPatternId) {
            this.shiftPatternId = shiftPatternId;
        }

        @Override
        public String toString() {
            return "ShiftAssignmentOutput{" +
                    "assignmentId='" + assignmentId + '\'' +
                    ", date='" + date + '\'' +
                    ", shiftTime='" + shiftTime + '\'' +
                    ", shiftDayId='" + shiftDayId + '\'' +
                    ", shiftPatternId='" + shiftPatternId + '\'' +
                    '}';
        }
    }
}