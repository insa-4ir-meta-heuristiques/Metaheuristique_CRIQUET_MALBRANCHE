package jobshop.solvers;

import jobshop.Instance;
import jobshop.encodings.ResourceOrder;
import jobshop.encodings.Schedule;
import jobshop.encodings.Task;

import java.util.Optional;
import java.util.ArrayList;

/** An empty shell to implement a greedy solver. */
public class GreedySolver implements Solver {

    /** All possible priorities for the greedy solver. */
    public enum Priority {
        SPT, LPT, SRPT, LRPT, EST_SPT, EST_LPT, EST_SRPT, EST_LRPT
    }

    /** Priority that the solver should use. */
    final Priority priority;

    /** Creates a new greedy solver that will use the given priority. */
    public GreedySolver(Priority p) {
        this.priority = p;
    }

    private int myComparator (int [] possible, Instance instance) {

        int selectJobs=0;
        for (int t = 0; t < instance.numJobs ; t++) {
            if (possible[t]< instance.numTasks){
                selectJobs = t;
            }
        }


        int selectTask = possible[selectJobs];

        int temps_restant = 0;
        for(int taskNumber = selectTask; taskNumber<instance.numTasks ; taskNumber++) {
            temps_restant+= instance.duration(0,taskNumber);
        }

        int temps_avant = 0;
        for(int taskNumber = 0 ; taskNumber<selectTask ; taskNumber++) {
            temps_avant+= instance.duration(0,taskNumber);
        }

        for (int t = 0; t < instance.numJobs ; t++) {

            if (possible[t]<instance.numTasks){


                int tmps =0;
                switch (priority) {
                    case SPT:
                        if (instance.duration(t, possible[t]) < instance.duration(selectJobs, selectTask)) {
                            selectJobs = t;
                            selectTask = possible[t];
                        }
                        break;

                    case LPT:
                        if (instance.duration(t, possible[t]) > instance.duration(selectJobs, selectTask)) {
                            selectJobs = t;
                            selectTask = possible[t];
                        }
                        break;

                    case LRPT:

                        for (int taskNumber = possible[t]; taskNumber < instance.numTasks; taskNumber++) {
                            tmps += instance.duration(t, taskNumber);
                        }
                        if (temps_restant <= tmps) {
                            temps_restant = tmps;
                            selectJobs = t;
                            selectTask = possible[t];
                        }
                        break;

                    case EST_SPT:

                        for (int taskNumber = 0; taskNumber < possible[t]; taskNumber++) {
                            tmps += instance.duration(t, taskNumber);
                        }
                        if (temps_avant < tmps) {
                            temps_avant = tmps;
                            selectJobs = t;
                            selectTask = possible[t];
                        } else if (temps_avant == tmps && instance.duration(t, possible[t]) < instance.duration(selectJobs, selectTask)) {
                            selectJobs = t;
                            selectTask = possible[t];
                        }
                }
            }





        }
        return selectJobs ;
    }

    private boolean fini (int [] possible, int max ){
        boolean out = false;

        for(int jobNumber = 0 ; jobNumber<possible.length ; jobNumber++) {
            if (possible[jobNumber]< max){
                out = true;
            }
        }
        return out;
    }

    @Override
    public Optional<Schedule> solve(Instance instance, long deadline) {

        ResourceOrder sol = new ResourceOrder(instance);
        int [] possible = new int [instance.numJobs];
        int selection;

        for(int jobNumber = 0 ; jobNumber<instance.numJobs ; jobNumber++) {
            possible[jobNumber]=0;
        }

        while (fini(possible, instance.numTasks)) {
            selection = myComparator(possible, instance);

            sol.addTaskToMachine(instance.machine(selection, possible[selection]), new Task(selection,possible[selection]));

            possible[selection] ++;

        }




        return sol.toSchedule();
    }
}
