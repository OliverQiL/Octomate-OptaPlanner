package org.acme.schooltimetabling.rest;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.acme.schooltimetabling.domain.Roster;
import org.optaplanner.core.api.solver.SolverJob;
import org.optaplanner.core.api.solver.SolverManager;

import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * SIMPLIFIED REST resource for solving the Roster scheduling problem.
 * 
 * Since data preprocessing (filtering, splitting) is handled in the main backend,
 * this service only focuses on optimization.
 */
@Path("/roster")
public class RosterResource {

    @Inject
    SolverManager<Roster, UUID> solverManager;

    /**
     * Solve the roster scheduling problem.
     * 
     * Input: Pre-processed Roster with:
     * - Filtered employees (only valid ones)
     * - Split shifts (each with exactly 1 opening)
     * 
     * Output: Optimized Roster with assignments
     */
    @POST
    @Path("/solve")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Roster solve(Roster problem) {
        // Log input for debugging
        System.out.println("Received roster problem:");
        System.out.println("  Employees: " + (problem.getEmployeeList() != null ? problem.getEmployeeList().size() : 0));
        System.out.println("  Shifts: " + (problem.getShiftList() != null ? problem.getShiftList().size() : 0));
        
        // Validate input
        if (problem.getEmployeeList() == null || problem.getEmployeeList().isEmpty()) {
            throw new IllegalArgumentException("No employees provided for scheduling");
        }
        
        if (problem.getShiftList() == null || problem.getShiftList().isEmpty()) {
            throw new IllegalArgumentException("No shifts provided for scheduling");
        }

        UUID problemId = UUID.randomUUID();
        
        // Submit problem to OptaPlanner solver
        SolverJob<Roster, UUID> solverJob = solverManager.solve(problemId, problem);
        
        Roster solution;
        try {
            // Wait for solving to complete
            solution = solverJob.getFinalBestSolution();
            
            // Log solution quality
            System.out.println("Solving completed:");
            System.out.println("  Score: " + solution.getScore());
            System.out.println("  Assigned shifts: " + solution.getAssignedShiftCount());
            System.out.println("  Unassigned shifts: " + solution.getUnassignedShiftCount());
            
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Solving failed: " + e.getMessage());
            throw new IllegalStateException("Solving failed", e);
        }

        return solution;
    }
    
    /**
     * Health check endpoint
     */
    @POST
    @Path("/health")
    @Produces(MediaType.APPLICATION_JSON)
    public String health() {
        return "{\"status\": \"OptaPlanner service is running\"}";
    }
}