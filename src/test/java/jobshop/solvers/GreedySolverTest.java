package jobshop.solvers;

import jobshop.Instance;
import jobshop.encodings.Schedule;
import junit.framework.TestCase;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

public class GreedySolverTest extends TestCase {

    public void testSolveSPT() throws IOException  {
        Instance instance = Instance.fromFile(Paths.get("instances/aaa3"));

        Solver solver = new GreedySolver(GreedySolver.Priority.SPT);
        solver.setRandom(false);
        Optional<Schedule> result = solver.solve(instance, System.currentTimeMillis() + 10);

        assert result.isPresent() : "The solver did not find a solution";
        // extract the schedule associated to the solution
        Schedule schedule = result.get();
        assert  schedule.isValid() : "The solution is not valid";

        System.out.println("Makespan: " + schedule.makespan());
        System.out.println("Schedule: \n" + schedule);
        System.out.println(schedule.asciiGantt());

        assert schedule.makespan() == 53 : "The basic solver should have produced a makespan of 53 for this instance.";
    }

    public void testSolveLPT() throws IOException  {
        Instance instance = Instance.fromFile(Paths.get("instances/aaa3"));

        Solver solver = new GreedySolver(GreedySolver.Priority.LPT);
        solver.setRandom(false);
        Optional<Schedule> result = solver.solve(instance, System.currentTimeMillis() + 10);

        assert result.isPresent() : "The solver did not find a solution";
        // extract the schedule associated to the solution
        Schedule schedule = result.get();
        assert  schedule.isValid() : "The solution is not valid";

        System.out.println("Makespan: " + schedule.makespan());
        System.out.println("Schedule: \n" + schedule);
        System.out.println(schedule.asciiGantt());

        assert schedule.makespan() == 92 : "The basic solver should have produced a makespan of 92 for this instance.";
    }

    public void testSolveSRPT() throws IOException  {
        Instance instance = Instance.fromFile(Paths.get("instances/aaa3"));

        Solver solver = new GreedySolver(GreedySolver.Priority.SRPT);
        solver.setRandom(false);
        Optional<Schedule> result = solver.solve(instance, System.currentTimeMillis() + 10);

        assert result.isPresent() : "The solver did not find a solution";
        // extract the schedule associated to the solution
        Schedule schedule = result.get();
        assert  schedule.isValid() : "The solution is not valid";

        System.out.println("Makespan: " + schedule.makespan());
        System.out.println("Schedule: \n" + schedule);
        System.out.println(schedule.asciiGantt());

        assert schedule.makespan() == 78 : "The basic solver should have produced a makespan of 78 for this instance.";
    }

    public void testSolveLRPT() throws IOException  {
        Instance instance = Instance.fromFile(Paths.get("instances/aaa3"));

        Solver solver = new GreedySolver(GreedySolver.Priority.LRPT);
        solver.setRandom(false);
        Optional<Schedule> result = solver.solve(instance, System.currentTimeMillis() + 10);

        assert result.isPresent() : "The solver did not find a solution";
        // extract the schedule associated to the solution
        Schedule schedule = result.get();
        assert  schedule.isValid() : "The solution is not valid";

        System.out.println("Makespan: " + schedule.makespan());
        System.out.println("Schedule: \n" + schedule);
        System.out.println(schedule.asciiGantt());

        assert schedule.makespan() == 54 : "The basic solver should have produced a makespan of 54 for this instance.";
    }

    public void testSolveEST_SPT() throws IOException  {
        Instance instance = Instance.fromFile(Paths.get("instances/aaa3"));

        Solver solver = new GreedySolver(GreedySolver.Priority.EST_SPT);
        solver.setRandom(false);
        Optional<Schedule> result = solver.solve(instance, System.currentTimeMillis() + 10);

        assert result.isPresent() : "The solver did not find a solution";
        // extract the schedule associated to the solution
        Schedule schedule = result.get();
        assert  schedule.isValid() : "The solution is not valid";

        System.out.println("Makespan: " + schedule.makespan());
        System.out.println("Schedule: \n" + schedule);
        System.out.println(schedule.asciiGantt());

        assert schedule.makespan() == 48 : "The basic solver should have produced a makespan of 48 for this instance.";
    }
    public void testSolveEST_LPT() throws IOException  {
        Instance instance = Instance.fromFile(Paths.get("instances/aaa3"));

        Solver solver = new GreedySolver(GreedySolver.Priority.EST_LPT);
        solver.setRandom(false);
        Optional<Schedule> result = solver.solve(instance, System.currentTimeMillis() + 10);

        assert result.isPresent() : "The solver did not find a solution";
        // extract the schedule associated to the solution
        Schedule schedule = result.get();
        assert  schedule.isValid() : "The solution is not valid";

        System.out.println("Makespan: " + schedule.makespan());
        System.out.println("Schedule: \n" + schedule);
        System.out.println(schedule.asciiGantt());

        assert schedule.makespan() == 56 : "The basic solver should have produced a makespan of 56 for this instance.";
    }

    public void testSolveEST_SRPT() throws IOException  {
        Instance instance = Instance.fromFile(Paths.get("instances/aaa3"));

        Solver solver = new GreedySolver(GreedySolver.Priority.EST_SRPT);
        solver.setRandom(false);
        Optional<Schedule> result = solver.solve(instance, System.currentTimeMillis() + 10);

        assert result.isPresent() : "The solver did not find a solution";
        // extract the schedule associated to the solution
        Schedule schedule = result.get();
        assert  schedule.isValid() : "The solution is not valid";

        System.out.println("Makespan: " + schedule.makespan());
        System.out.println("Schedule: \n" + schedule);
        System.out.println(schedule.asciiGantt());

        assert schedule.makespan() == 53 : "The basic solver should have produced a makespan of 53 for this instance.";
    }

    public void testSolveEST_EST_LRPT() throws IOException  {
        Instance instance = Instance.fromFile(Paths.get("instances/aaa3"));

        Solver solver = new GreedySolver(GreedySolver.Priority.EST_LRPT);
        solver.setRandom(false);
        Optional<Schedule> result = solver.solve(instance, System.currentTimeMillis() + 10);

        assert result.isPresent() : "The solver did not find a solution";
        // extract the schedule associated to the solution
        Schedule schedule = result.get();
        assert  schedule.isValid() : "The solution is not valid";

        System.out.println("Makespan: " + schedule.makespan());
        System.out.println("Schedule: \n" + schedule);
        System.out.println(schedule.asciiGantt());

        assert schedule.makespan() == 56 : "The basic solver should have produced a makespan of 56 for this instance.";
    }

}