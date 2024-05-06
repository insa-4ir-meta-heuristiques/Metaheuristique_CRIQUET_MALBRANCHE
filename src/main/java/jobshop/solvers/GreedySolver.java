package jobshop.solvers;

import jobshop.Instance;
import jobshop.encodings.ResourceOrder;
import jobshop.encodings.Schedule;
import jobshop.encodings.Task;

import java.util.Optional;
import java.util.Random;

/** An empty shell to implement a greedy solver. */
public class GreedySolver implements Solver {

    /** All possible priorities for the greedy solver. */
    public enum Priority {
        SPT, LPT, SRPT, LRPT, EST_SPT, EST_LPT, EST_SRPT, EST_LRPT
    }

    /** Priority that the solver should use. */
    final Priority priority;
    private boolean random ;
    private int [] jobs_end;
    private int [] machine_end;

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
            temps_restant+= instance.duration(selectJobs,taskNumber);
        }

        int temps_avant = Math.max(jobs_end[selectJobs], machine_end[instance.machine(selectJobs,selectTask)]);

        for (int t = 0; t < instance.numJobs ; t++) {

            if (possible[t]<instance.numTasks){


                int temps_restant_tmp =0;
                int temps_avant_tmp =0;
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

                    case SRPT:

                        for (int taskNumber = possible[t]; taskNumber < instance.numTasks; taskNumber++) {
                            temps_restant_tmp += instance.duration(t, taskNumber);
                        }
                        if (temps_restant > temps_restant_tmp) {
                            temps_restant = temps_restant_tmp;
                            selectJobs = t;
                            selectTask = possible[t];
                        }
                        break;


                    case LRPT:

                        for (int taskNumber = possible[t]; taskNumber < instance.numTasks; taskNumber++) {
                            temps_restant_tmp += instance.duration(t, taskNumber);
                        }
                        if (temps_restant <= temps_restant_tmp) {
                            temps_restant = temps_restant_tmp;
                            selectJobs = t;
                            selectTask = possible[t];
                        }
                        break;


                    case EST_SPT:

                        temps_avant_tmp = Math.max(jobs_end[t], machine_end[instance.machine(t,possible[t])]);

                        if (temps_avant > temps_avant_tmp) {
                            temps_avant = temps_avant_tmp;
                            selectJobs = t;
                            selectTask = possible[t];
                        } else if ( temps_avant == temps_avant_tmp && (instance.duration(t, possible[t]) < instance.duration(selectJobs, selectTask))) {
                            selectJobs = t;
                            selectTask = possible[t];

                        }
                        break;

                    case EST_LPT:

                        temps_avant_tmp = Math.max(jobs_end[t], machine_end[instance.machine(t,possible[t])]);

                        if (temps_avant > temps_avant_tmp) {
                            temps_avant = temps_avant_tmp;
                            selectJobs = t;
                            selectTask = possible[t];
                        } else if ( temps_avant == temps_avant_tmp && (instance.duration(t, possible[t]) > instance.duration(selectJobs, selectTask))) {
                            selectJobs = t;
                            selectTask = possible[t];

                        }
                        break;

                    case EST_SRPT:

                        temps_avant_tmp = Math.max(jobs_end[t], machine_end[instance.machine(t,possible[t])]);

                        for (int taskNumber = possible[t]; taskNumber < instance.numTasks; taskNumber++) {
                            temps_restant_tmp += instance.duration(t, taskNumber);
                        }

                        if (temps_avant > temps_avant_tmp) {
                            temps_avant = temps_avant_tmp;
                            selectJobs = t;
                            selectTask = possible[t];
                        } else if ( temps_avant == temps_avant_tmp && (temps_restant > temps_restant_tmp)){
                            selectJobs = t;
                            selectTask = possible[t];

                        }
                        break;

                    case EST_LRPT:

                        temps_avant_tmp = Math.max(jobs_end[t], machine_end[instance.machine(t,possible[t])]);

                        for (int taskNumber = possible[t]; taskNumber < instance.numTasks; taskNumber++) {
                            temps_restant_tmp += instance.duration(t, taskNumber);
                        }

                        if (temps_avant > temps_avant_tmp) {
                            temps_avant = temps_avant_tmp;
                            selectJobs = t;
                            selectTask = possible[t];
                        } else if ( temps_avant == temps_avant_tmp && (temps_restant <= temps_restant_tmp)){
                            selectJobs = t;
                            selectTask = possible[t];

                        }
                        break;


                }
            }





        }
        return selectJobs ;
    }

    private boolean fini (int [] possible, int max ){
        boolean out = true;

        for(int jobNumber = 0 ; jobNumber<possible.length ; jobNumber++) {
            if (possible[jobNumber]< max){
                out = false;
            }
        }
        return out;
    }



    private int select_radom(int [] possible, int max){
        int nb = 0;
        for(int jobNumber = 0 ; jobNumber<possible.length ; jobNumber++) {
            if (possible[jobNumber]< max){
                nb ++;
            }
        }

        Random randomGene = new Random();
        int alea = randomGene.nextInt(nb);
        int index=-1;

        while (alea>=0){
            index++;
            if (possible[index]<max){
                alea--;
            }
        }
        return index;
    }

    public void setRandom(boolean random){
        this.random=random;
    }
    @Override
    public Optional<Schedule> solve(Instance instance, long deadline) {

         // modification rapide de l'utilisation de random
        Optional<Schedule> sol = solveur(instance,random);

        if (random){
            Optional<Schedule> sol_tmp;
            for (int iteration=0; iteration <10; iteration++){
                sol_tmp=solveur(instance,random);

                if(sol_tmp.get().makespan()<sol.get().makespan()){
                    sol = sol_tmp;
                }
            }

        }

        return sol;
    }


    private  Optional<Schedule> solveur(Instance instance, boolean random) {

        // C'est tableau nous servent pour l'éxecution des heuristique EST
        jobs_end = new int[instance.numJobs];
        machine_end = new int[instance.numMachines];

        for (int machineNumber = 0; machineNumber < instance.numMachines; machineNumber++){
            machine_end[machineNumber]=0;
        }
        for(int jobNumber = 0 ; jobNumber<instance.numJobs ; jobNumber++) {
            jobs_end[jobNumber]=0;
        }


        ResourceOrder sol = new ResourceOrder(instance);
        int [] possible = new int [instance.numJobs];
        int selection;

        for(int jobNumber = 0 ; jobNumber<instance.numJobs ; jobNumber++) {
            // On init le tableau qui retient à quelle tache on en est pour chaque job
            possible[jobNumber]=0;
        }

        Random randomGene = new Random();

        while (!fini(possible, instance.numTasks)) {

            if (randomGene.nextInt(100)< 5 && random){  // POUR L'AJOUT D'ALÉATOIRE À DÉSACTIVER POUR TEST
                selection = select_radom(possible, instance.numTasks);
            }else {
                selection = myComparator(possible, instance);
            }

            sol.addTaskToMachine(instance.machine(selection, possible[selection]), new Task(selection,possible[selection]));

            machine_end[instance.machine(selection, possible[selection])]+= instance.duration(selection, possible[selection]);
            jobs_end[selection]+= instance.duration(selection, possible[selection]);

            possible[selection] ++;
        }

        return sol.toSchedule();

    }
}

