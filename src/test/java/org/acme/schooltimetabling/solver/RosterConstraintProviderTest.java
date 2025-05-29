package org.acme.schooltimetabling.solver;

import java.time.LocalDateTime;
import jakarta.inject.Inject;

import io.quarkus.test.junit.QuarkusTest;
import org.acme.schooltimetabling.domain.Shift;
import org.acme.schooltimetabling.domain.Employee;
import org.acme.schooltimetabling.domain.Roster;
import org.junit.jupiter.api.Test;
import org.optaplanner.test.api.score.stream.ConstraintVerifier;

@QuarkusTest
class RosterConstraintProviderTest {

    // Test data - reused across multiple tests
    private static final Employee ALICE = new Employee(1L, "Alice Johnson");
    private static final Employee BOB = new Employee(2L, "Bob Smith");
    private static final Employee CHARLIE = new Employee(3L, "Charlie Brown");

    // Test time periods
    private static final LocalDateTime MON_9AM = LocalDateTime.of(2024, 1, 15, 9, 0);
    private static final LocalDateTime MON_5PM = LocalDateTime.of(2024, 1, 15, 17, 0);
    private static final LocalDateTime MON_1PM = LocalDateTime.of(2024, 1, 15, 13, 0);
    private static final LocalDateTime MON_9PM = LocalDateTime.of(2024, 1, 15, 21, 0);
    private static final LocalDateTime TUE_9AM = LocalDateTime.of(2024, 1, 16, 9, 0);
    private static final LocalDateTime TUE_5PM = LocalDateTime.of(2024, 1, 16, 17, 0);

    @Inject
    ConstraintVerifier<RosterConstraintProvider, Roster> constraintVerifier;

    @Test
    void employeeOverlappingShiftConflict() {
        // Create overlapping shifts for the same employee
        Shift morningShift = new Shift(1L, 100L, "Clean", MON_9AM, MON_5PM, "Store Front");
        Shift afternoonShift = new Shift(2L, 200L, "Clean", MON_1PM, MON_9PM, "Warehouse"); // Overlaps 1PM-5PM
        Shift nonOverlappingShift = new Shift(3L, 300L, "Clean", TUE_9AM, TUE_5PM, "Store Front"); // Different day

        // Assign same employee to overlapping shifts
        morningShift.setAssignedEmployee(ALICE);
        afternoonShift.setAssignedEmployee(ALICE);   // CONFLICT: Same employee, overlapping time
        nonOverlappingShift.setAssignedEmployee(ALICE); // OK: Different day

        // Verify: Alice working 2 overlapping shifts = 1 penalty
        constraintVerifier.verifyThat(RosterConstraintProvider::employeeOverlappingShiftConflict)
                .given(morningShift, afternoonShift, nonOverlappingShift)
                .penalizesBy(1);
    }

    @Test
    void noConflictWhenDifferentEmployees() {
        // Same time periods, but different employees
        Shift shift1 = new Shift(1L, 100L, "Clean",MON_9AM, MON_5PM, "Store Front");
        Shift shift2 = new Shift(2L, 200L, "Clean", MON_1PM, MON_9PM, "Store Front"); // Overlapping time
        
        // Assign different employees
        shift1.setAssignedEmployee(ALICE);
        shift2.setAssignedEmployee(BOB);  // Different employee = NO conflict

        // Verify: Different employees can work overlapping shifts
        constraintVerifier.verifyThat(RosterConstraintProvider::employeeOverlappingShiftConflict)
                .given(shift1, shift2)
                .penalizesBy(0);
    }

    @Test
    void noConflictWhenNonOverlappingTimes() {
        // Sequential shifts (no time overlap)
        Shift morningShift = new Shift(1L, 100L, "Clean", MON_9AM, MON_5PM, "Store");
        Shift eveningShift = new Shift(2L, 200L, "Clean",MON_5PM, MON_9PM, "Store"); // Starts when morning ends
        
        // Same employee working sequential shifts
        morningShift.setAssignedEmployee(ALICE);
        eveningShift.setAssignedEmployee(ALICE); // Same employee, but no overlap

        // Verify: Same employee can work back-to-back shifts (no overlap)
        constraintVerifier.verifyThat(RosterConstraintProvider::employeeOverlappingShiftConflict)
                .given(morningShift, eveningShift)
                .penalizesBy(0);
    }

    @Test
    void multipleOverlappingConflicts() {
        // One employee assigned to 3 overlapping shifts
        Shift shift1 = new Shift(1L, 100L, "Clean",MON_9AM, MON_5PM, "Store");
        Shift shift2 = new Shift(2L, 200L, "Clean",MON_1PM, MON_9PM, "Warehouse");
        Shift shift3 = new Shift(3L, 300L, "Clean",MON_1PM, MON_5PM, "Security");
        
        // All assigned to Charlie
        shift1.setAssignedEmployee(CHARLIE);
        shift2.setAssignedEmployee(CHARLIE);
        shift3.setAssignedEmployee(CHARLIE);

        // Verify: 3 overlapping shifts = 3 pairwise conflicts
        // (1 vs 2) + (1 vs 3) + (2 vs 3) = 3 penalties
        constraintVerifier.verifyThat(RosterConstraintProvider::employeeOverlappingShiftConflict)
                .given(shift1, shift2, shift3)
                .penalizesBy(3);
    }

    @Test
    void noConflictWhenUnassignedShifts() {
        // Shifts with no assigned employees
        Shift shift1 = new Shift(1L, 100L, "Clean",MON_9AM, MON_5PM, "Store");
        Shift shift2 = new Shift(2L, 200L, "Clean",MON_1PM, MON_9PM, "Store");
        
        // Leave assignedEmployee as null (unassigned)
        // shift1.setAssignedEmployee(null); // null by default
        // shift2.setAssignedEmployee(null); // null by default

        // Verify: Unassigned shifts don't create conflicts
        constraintVerifier.verifyThat(RosterConstraintProvider::employeeOverlappingShiftConflict)
                .given(shift1, shift2)
                .penalizesBy(0);
    }

    @Test
    void partiallyAssignedShifts() {
        // Mix of assigned and unassigned shifts
        Shift assignedShift = new Shift(1L, 100L, "Clean",MON_9AM, MON_5PM, "Store");
        Shift unassignedShift = new Shift(2L, 200L, "Clean",MON_1PM, MON_9PM, "Store");
        
        assignedShift.setAssignedEmployee(ALICE);
        // unassignedShift.setAssignedEmployee(null); // remains null

        // Verify: One assigned, one unassigned = no conflict
        constraintVerifier.verifyThat(RosterConstraintProvider::employeeOverlappingShiftConflict)
                .given(assignedShift, unassignedShift)
                .penalizesBy(0);
    }
}