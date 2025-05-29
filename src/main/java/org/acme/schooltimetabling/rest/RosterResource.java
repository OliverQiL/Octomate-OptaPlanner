package org.acme.schooltimetabling.rest;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.acme.schooltimetabling.domain.Roster;
import org.optaplanner.core.api.solver.SolverJob;
import org.optaplanner.core.api.solver.SolverManager;

import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.POST;

/**
 * REST resource for solving the Roster scheduling problem.
 * This resource provides an endpoint to submit a Roster problem
 * and receive a solution.
 */
@Path("/roster")
public class RosterResource {

    @Inject
    SolverManager<Roster, UUID> solverManager;

    // Endpoint to solve the timetable
    @POST
    @Path("/solve")
    public Roster solve(Roster problem) {
        UUID problemId = UUID.randomUUID();
        // Submit the problem to the solver manager
        SolverJob<Roster, UUID> solverJob = solverManager.solve(problemId, problem);
        Roster solution;
        try {
            // Wait until to solving ends
            solution = solverJob.getFinalBestSolution();
        } catch (InterruptedException | ExecutionException e) {
            // Handle exceptions, e.g., if the solving fails
            throw new IllegalStateException("Solving failed", e);
        }

        return solution;
    }
}
