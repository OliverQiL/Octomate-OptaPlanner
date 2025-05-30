package org.acme.schooltimetabling.solver;

import org.acme.schooltimetabling.domain.Shift;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;

/**
 * ENHANCED constraint provider with two-level date/time checking
 */
public class RosterConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] {
                // Hard constraints
                employeeConflictingSameDayOverlappingShifts(constraintFactory),
                employeeCannotWorkIncompatibleShiftPattern(constraintFactory),

                // Soft constraints
                preferFewerUnassignedShifts(constraintFactory),
        };
    }

    /**
     * HARD: Employee cannot work overlapping shifts on the same day
     * Two-level check: (1) Same date AND (2) Overlapping times
     */
    Constraint employeeConflictingSameDayOverlappingShifts(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Shift.class)
                .join(Shift.class,
                        // Level 1: Same employee assigned
                        Joiners.equal(Shift::getAssignedEmployee),
                        // Level 2: Same date
                        Joiners.equal(Shift::getShiftDate),
                        // Prevent duplicate pairs
                        Joiners.lessThan(Shift::getShiftDayId))
                // Level 3: Check time overlap and ensure employee is assigned
                .filter((shift1, shift2) -> shift1.getAssignedEmployee() != null &&
                        shift1.overlapsTime(shift2))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Employee cannot work overlapping shifts on same day");
    }

    /**
     * HARD: Employee can only work shift patterns they're qualified for
     */
    Constraint employeeCannotWorkIncompatibleShiftPattern(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Shift.class)
                .filter(shift -> shift.getAssignedEmployee() != null &&
                        !shift.getAssignedEmployee().canWorkShiftPattern(shift.getShiftPatternId()))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Employee cannot work incompatible shift pattern");
    }

    /**
     * SOFT: Minimize unassigned shifts
     */
    Constraint preferFewerUnassignedShifts(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Shift.class)
                .filter(shift -> shift.getAssignedEmployee() == null)
                .penalize(HardSoftScore.ONE_SOFT)
                .asConstraint("Prefer fewer unassigned shifts");
    }
}