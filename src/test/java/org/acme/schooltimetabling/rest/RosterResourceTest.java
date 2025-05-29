package org.acme.schooltimetabling.rest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.inject.Inject;

import io.quarkus.test.junit.QuarkusTest;
import org.acme.schooltimetabling.domain.Shift;
import org.acme.schooltimetabling.domain.Employee;
import org.acme.schooltimetabling.domain.Roster;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
public class RosterResourceTest {

    @Inject
    RosterResource rosterResource;

    @Test
    @Timeout(600_000) // 10 minutes max (in milliseconds)
    public void solve() {
        Roster problem = generateProblem();
        Roster solution = rosterResource.solve(problem);

        // Verify all lessons were assigned
        assertFalse(solution.getShiftList().isEmpty());
        for (Shift shift : solution.getShiftList()) {
            assertNotNull(shift.getId());
        }

        // Verify we found a feasible solution (no hard constraints broken)
        assertTrue(solution.getScore().isFeasible());
    }

    private Roster generateProblem() {
        List<Employee> employeeList = new ArrayList<>();
        employeeList.add(new Employee(1L, "B. May"));
        employeeList.add(new Employee(2L, "M. Curie"));
        employeeList.add(new Employee(3L, "I. Jones"));

        List<Shift> shiftList = new ArrayList<>();
        shiftList.add(new Shift(
                100L,
                200L,
                "Cleaning",
                LocalDateTime.of(2025, 1, 1, 8, 0, 0),
                LocalDateTime.of(2025, 1, 1, 10, 0, 0),
                "Toilet"));

        shiftList.add(new Shift(
                101L,
                201L,
                "Maintenance",
                LocalDateTime.of(2025, 1, 1, 10, 0, 0),
                LocalDateTime.of(2025, 1, 1, 12, 0, 0),
                "Kitchen"));

        shiftList.add(new Shift(
                102L,
                202L,
                "Security",
                LocalDateTime.of(2025, 1, 1, 12, 0, 0),
                LocalDateTime.of(2025, 1, 1, 14, 0, 0),
                "Main Gate"));

        return new Roster(employeeList, shiftList);
    }
}