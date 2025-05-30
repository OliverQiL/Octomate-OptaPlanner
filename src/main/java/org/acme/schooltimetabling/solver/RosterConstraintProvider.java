package org.acme.schooltimetabling.solver;

import org.acme.schooltimetabling.domain.Shift;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;

/**
 * Constraint provider for the Roster scheduling problem.
 * This class defines the constraints used by Optaplanner to solve the
 * scheduling problem for assigning shifts to employees.
 * It includes hard constraints that must be satisfied,
 * such as ensuring that an employee does not have overlapping shifts.
 * Hard constraints are conditions that must always be met for a valid solution.
 * 
 * Soft constraints are preferences that can be optimized but are not mandatory.
 */
public class RosterConstraintProvider implements ConstraintProvider {

        @Override
        public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
                return new Constraint[] {
                                // Hard constraints
                                employeeOverlappingShiftConflict(constraintFactory)
                                // Soft constraints are only implemented in the optaplanner-quickstarts code
                };
        }

        // ! HARD: An employee can only work one shift at a time
        Constraint employeeOverlappingShiftConflict(ConstraintFactory constraintFactory) {
                return constraintFactory.forEach(Shift.class)
                                .join(Shift.class,
                                                // Join shifts assigned to the same employee
                                                Joiners.equal(Shift::getAssignedEmployee),
                                                // Ensure shifts overlap in time
                                                Joiners.overlapping(
                                                                shift -> shift.getStartTime(),
                                                                shift -> shift.getEndTime()),
                                                // To prevent double counting
                                                Joiners.lessThan(Shift::getShiftDayId))
                                .penalize(HardSoftScore.ONE_HARD)
                                .asConstraint("Employee cannot work overlapping shifts");
        }

}
