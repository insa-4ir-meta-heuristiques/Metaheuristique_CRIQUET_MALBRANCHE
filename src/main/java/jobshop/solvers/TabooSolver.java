package jobshop.solvers;

import jobshop.Instance;
import jobshop.encodings.PairTask;
import jobshop.encodings.ResourceOrder;
import jobshop.encodings.Schedule;
import jobshop.solvers.neighborhood.Neighborhood;
import jobshop.solvers.neighborhood.Nowicki;
import jobshop.solvers.neighborhood.PairRessourceOrderPairTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class TabooSolver implements Solver{


    /* TO DO:
        Il faut implementer la class tabou
        Faire une liste de pairmutation interdite
        si interdite dans un sens alors dans les deux
        Taille max liste

        Taboo =! Descent solveur mais <=
        Et on recherche dans le voisinage sans ce se regarder soit même
        sauvegarde du maxima dans une autre var
             */

    final Nowicki neighborhood;
    final ArrayList<Solver> baseSolver;
    final int maxIter;
    final int sizeList;

    private boolean optional;

    /** Creates a new descent solver with a given neighborhood and a solver for the initial solution.
     *
     * @param neighborhood Neighborhood object that should be used to generates neighbor solutions to the current candidate.
     * @param baseSolver A solver to provide the initial solution.
     */
    public TabooSolver(Nowicki neighborhood, Solver baseSolver, int maxIter, int forget) {
        this.neighborhood = neighborhood;
        this.baseSolver = new ArrayList<>();
        this.baseSolver.add(baseSolver);
        this.maxIter=maxIter;
        sizeList = forget;
    }

    public TabooSolver(Nowicki neighborhood, ArrayList<Solver> baseSolver, int maxIter,  int forget) {
        this.neighborhood = neighborhood;
        assert !baseSolver.isEmpty();
        this.baseSolver = baseSolver;
        this.maxIter=maxIter;
        sizeList = forget;
    }

    @Override
    public void setRandom(boolean random){
        for (Solver solver : baseSolver){
            solver.setRandom(random);
        }

        optional =random;
    }


    @Override
    public Optional<Schedule> solve(Instance instance, long deadline) {
        Optional<Schedule> sol = baseSolver.get(0).solve(instance,deadline);
        assert sol.isPresent();
        int bestSpan = sol.get().makespan();

        for (Solver solver : baseSolver){
            Optional<Schedule> sol_bis = solveOne(instance,deadline,solver, optional);

            if(sol_bis.isPresent()){
                if(sol_bis.get().isValid() && sol_bis.get().makespan()<bestSpan){
                    bestSpan = sol_bis.get().makespan();
                    sol=sol_bis;
                }
            }
        }
        return sol;
    }

    private void addtoTab ( PairTask[] tab, PairTask pair, int index){
        tab[index]=pair;

        if(index<sizeList-1){
            index++;
        }else {
            index =0;
        }
    }

    private Optional<Schedule> solveOne(Instance instance, long deadline, Solver solver, boolean optionnal) {
        Optional<Schedule> sol = solver.solve(instance,deadline);
        assert sol.isPresent();

        ResourceOrder current  = new ResourceOrder(solver.solve(instance,deadline).get());
        int currentSpan;
        PairTask currentPairTask = null;

        ResourceOrder best  = new ResourceOrder(solver.solve(instance,deadline).get());
        int bestSpan = current.toSchedule().get().makespan();

        int iter= 0;

        PairTask[] tab = new PairTask[sizeList];
        int index = 0;

        Random randomGene = new Random();
        ResourceOrder currentOptionnal  = new ResourceOrder(solver.solve(instance,deadline).get());
        int currentSpanOptionnal;
        PairTask currentPairTaskOptionnal = null;


        while (iter < maxIter){
            List<PairRessourceOrderPairTask> list = neighborhood.generateNeighborsTaboo(current,tab);
            currentSpan = 0;
            currentSpanOptionnal = 0;

            for(PairRessourceOrderPairTask pair : list){
                Optional<Schedule> sol_bis= pair.resourceOrder.toSchedule();

                if(sol_bis.isPresent()){
                    int trySpan =sol_bis.get().makespan();

                    if (trySpan<currentSpan || currentSpan==0 || (optionnal && (randomGene.nextInt(100)< 1 ))){
                        currentSpan = trySpan;
                        current = pair.resourceOrder;
                        currentPairTask = pair.pairTask.Reverse();
                    }else if(optionnal && (trySpan < currentSpanOptionnal || currentSpanOptionnal ==0)){
                        currentSpanOptionnal = trySpan;
                        currentOptionnal = pair.resourceOrder;
                        currentPairTaskOptionnal = pair.pairTask.Reverse();
                    }
                }
            }

            addtoTab(tab,currentPairTask , index);

            if((optionnal && (randomGene.nextInt(100)< 1 ))){
                currentSpan = currentSpanOptionnal;
                current = currentOptionnal;
                addtoTab(tab,currentPairTaskOptionnal , index);
            }

            if (currentSpan<bestSpan){
                bestSpan=currentSpan;
                best = current;
            }
            iter++;
        }

        Optional<Schedule> out = best.toSchedule();

        return out;
    }
}
