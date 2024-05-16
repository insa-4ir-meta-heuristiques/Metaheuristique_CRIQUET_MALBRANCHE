package jobshop.solvers;

import jobshop.Instance;
import jobshop.encodings.ResourceOrder;
import jobshop.encodings.Schedule;
import jobshop.solvers.neighborhood.Neighborhood;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** An empty shell to implement a descent solver. */
public class DescentSolver implements Solver {

    final Neighborhood neighborhood;
    final ArrayList<Solver> baseSolver;

    /** Creates a new descent solver with a given neighborhood and a solver for the initial solution.
     *
     * @param neighborhood Neighborhood object that should be used to generates neighbor solutions to the current candidate.
     * @param baseSolver A solver to provide the initial solution.
     */
    public DescentSolver(Neighborhood neighborhood, Solver baseSolver) {
        this.neighborhood = neighborhood;
        this.baseSolver = new ArrayList<>();
        this.baseSolver.add(baseSolver);
    }

    public DescentSolver(Neighborhood neighborhood, ArrayList<Solver> baseSolver) {
        this.neighborhood = neighborhood;
        assert !baseSolver.isEmpty();
        this.baseSolver = baseSolver;
    }

    @Override
    public void setRandom(boolean random){
        for (Solver solver : baseSolver){
            solver.setRandom(random);
        }
    }
    @Override
    public Optional<Schedule> solve(Instance instance, long deadline) {
        Optional<Schedule> sol = baseSolver.get(0).solve(instance,deadline);
        assert sol.isPresent();
        int bestSpan = sol.get().makespan();

        for (Solver solver : baseSolver){
            Optional<Schedule> sol_bis = solveOne(instance,deadline,solver);

            if(sol_bis.isPresent()){
                if(sol_bis.get().isValid() && sol_bis.get().makespan()<bestSpan){
                    bestSpan = sol_bis.get().makespan();
                    sol=sol_bis;
                }
            }
        }
        return sol;
    }



    private Optional<Schedule> solveOne(Instance instance, long deadline, Solver solver) {
        Optional<Schedule> sol = solver.solve(instance,deadline);
        assert sol.isPresent();

        ResourceOrder current  = new ResourceOrder(solver.solve(instance,deadline).get());
        int bestSpan = current.toSchedule().get().makespan();
        int lastSpan = bestSpan+1;

        while (bestSpan < lastSpan){
            lastSpan = bestSpan;
            List<ResourceOrder> list = neighborhood.generateNeighbors(current);

            for(ResourceOrder neighbors : list){
                Optional<Schedule> sol_bis= neighbors.toSchedule();

                if(sol_bis.isPresent()){
                    int actualSpan =sol_bis.get().makespan();

                    if (actualSpan<bestSpan){
                        bestSpan=actualSpan;
                        current = neighbors;
                    }
                }
            }
        }

        Optional<Schedule> out = current.toSchedule();

        return out;
    }
}
