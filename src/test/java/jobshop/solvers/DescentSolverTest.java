package jobshop.solvers;

import jobshop.Instance;
import jobshop.encodings.Schedule;
import jobshop.solvers.neighborhood.Nowicki;
import junit.framework.TestCase;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;

public class DescentSolverTest extends TestCase {

    public void testSolveEST_SRPT() throws IOException {
        Nowicki neighborhood = new Nowicki();

        Instance instance = Instance.fromFile(Paths.get("instances/aaa3"));
        Solver solver = new GreedySolver(GreedySolver.Priority.EST_SRPT);
        solver.setRandom(false);

        DescentSolver descentSolver = new DescentSolver(neighborhood,solver);
        Optional<Schedule> result = descentSolver.solve(instance, System.currentTimeMillis() + 10);

        assert result.isPresent() : "The solver did not find a solution";
        // extract the schedule associated to the solution
        Schedule schedule = result.get();
        assert  schedule.isValid() : "The solution is not valid";

        System.out.println("Makespan: " + schedule.makespan());
        System.out.println("Schedule: \n" + schedule);
        System.out.println(schedule.asciiGantt());

        assert schedule.makespan() < 53 : "The basic solver should have produced a makespan better than 53 for this instance.";
    }

    public void testSolveLRPT() throws IOException {
        Nowicki neighborhood = new Nowicki();

        Instance instance = Instance.fromFile(Paths.get("instances/aaa3"));
        Solver solver = new GreedySolver(GreedySolver.Priority.LRPT);
        solver.setRandom(false);

        DescentSolver descentSolver = new DescentSolver(neighborhood,solver);
        Optional<Schedule> result = descentSolver.solve(instance, System.currentTimeMillis() + 10);

        assert result.isPresent() : "The solver did not find a solution";
        // extract the schedule associated to the solution
        Schedule schedule = result.get();
        assert  schedule.isValid() : "The solution is not valid";

        System.out.println("Makespan: " + schedule.makespan());
        System.out.println("Schedule: \n" + schedule);
        System.out.println(schedule.asciiGantt());

        assert schedule.makespan() < 54 : "The basic solver should have produced a makespan better than 53 for this instance.";
    }

    public void testSolveSPT() throws IOException {
        Nowicki neighborhood = new Nowicki();

        Instance instance = Instance.fromFile(Paths.get("instances/aaa3"));
        Solver solver = new GreedySolver(GreedySolver.Priority.SPT);
        solver.setRandom(false);

        DescentSolver descentSolver = new DescentSolver(neighborhood,solver);
        Optional<Schedule> result = descentSolver.solve(instance, System.currentTimeMillis() + 10);

        assert result.isPresent() : "The solver did not find a solution";
        // extract the schedule associated to the solution
        Schedule schedule = result.get();
        assert  schedule.isValid() : "The solution is not valid";

        System.out.println("Makespan: " + schedule.makespan());
        System.out.println("Schedule: \n" + schedule);
        System.out.println(schedule.asciiGantt());

        assert schedule.makespan() < 53 : "The basic solver should have produced a makespan better than 53 for this instance.";
    }

    public void testMultiStart() throws IOException {
        Nowicki neighborhood = new Nowicki();

        Instance instance = Instance.fromFile(Paths.get("instances/abz7"));
        ArrayList<Solver> list = new ArrayList<>();

        Solver solver = new GreedySolver(GreedySolver.Priority.EST_LPT);
        solver.setRandom(true);
        list.add(solver);

        solver = new GreedySolver(GreedySolver.Priority.EST_LRPT);
        solver.setRandom(true);
        list.add(solver);

        solver = new GreedySolver(GreedySolver.Priority.LRPT);
        solver.setRandom(true);
        list.add(solver);

        solver = new GreedySolver(GreedySolver.Priority.SRPT);
        solver.setRandom(true);
        list.add(solver);

        solver = new GreedySolver(GreedySolver.Priority.LPT);
        solver.setRandom(true);
        list.add(solver);

        DescentSolver descentSolver = new DescentSolver(neighborhood,list);
        Optional<Schedule> result = descentSolver.solve(instance, System.currentTimeMillis() + 10);

        assert result.isPresent() : "The solver did not find a solution";
        // extract the schedule associated to the solution
        Schedule schedule = result.get();
        assert  schedule.isValid() : "The solution is not valid";

        System.out.println("Makespan: " + schedule.makespan());
        System.out.println("Schedule: \n" + schedule);
        System.out.println(schedule.asciiGantt());

        assert schedule.makespan() < 1000 : "The basic solver should have produced a makespan better than 1000 for this instance.";
    }
}