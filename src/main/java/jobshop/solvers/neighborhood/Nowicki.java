package jobshop.solvers.neighborhood;

import jobshop.encodings.PairTask;
import jobshop.encodings.ResourceOrder;
import jobshop.encodings.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/** Implementation of the Nowicki and Smutnicki neighborhood.
 *
 * It works on the ResourceOrder encoding by generating two neighbors for each block
 * of the critical path.
 * For each block, two neighbors should be generated that respectively swap the first two and
 * last two tasks of the block.
 */
public class Nowicki extends Neighborhood {

    /** A block represents a subsequence of the critical path such that all tasks in it execute on the same machine.
     * This class identifies a block in a ResourceOrder representation.
     *
     * Consider the solution in ResourceOrder representation
     * machine 0 : (0,1) (1,2) (2,2)
     * machine 1 : (0,2) (2,1) (1,1)
     * machine 2 : ...
     *
     * The block with : machine = 1, firstTask= 0 and lastTask = 1
     * Represent the task sequence : [(0,2) (2,1)]
     *
     * */
    public static class Block {
        /** machine on which the block is identified */
        public final int machine;
        /** index of the first task of the block */
        public final int firstTask;
        /** index of the last task of the block */
        public final int lastTask;

        /** Creates a new block. */
        Block(int machine, int firstTask, int lastTask) {
            this.machine = machine;
            this.firstTask = firstTask;
            this.lastTask = lastTask;
        }
    }

    /**
     * Represents a swap of two tasks on the same machine in a ResourceOrder encoding.
     *
     * Consider the solution in ResourceOrder representation
     * machine 0 : (0,1) (1,2) (2,2)
     * machine 1 : (0,2) (2,1) (1,1)
     * machine 2 : ...
     *
     * The swap with : machine = 1, t1= 0 and t2 = 1
     * Represent inversion of the two tasks : (0,2) and (2,1)
     * Applying this swap on the above resource order should result in the following one :
     * machine 0 : (0,1) (1,2) (2,2)
     * machine 1 : (2,1) (0,2) (1,1)
     * machine 2 : ...
     */
    public static class Swap {
        /** machine on which to perform the swap */
        public final int machine;

        /** index of one task to be swapped (in the resource order encoding).
         * t1 should appear earlier than t2 in the resource order. */
        public final int t1;

        /** index of the other task to be swapped (in the resource order encoding) */
        public final int t2;

        /** Creates a new swap of two tasks. */
        Swap(int machine, int t1, int t2) {
            this.machine = machine;
            if (t1 < t2) {
                this.t1 = t1;
                this.t2 = t2;
            } else {
                this.t1 = t2;
                this.t2 = t1;
            }
        }


        /** Creates a new ResourceOrder order that is the result of performing the swap in the original ResourceOrder.
         *  The original ResourceOrder MUST NOT be modified by this operation.
         */
        public ResourceOrder generateFrom(ResourceOrder original) {
            ResourceOrder plagiat = original.copy();
            plagiat.swapTasks(machine,t1,t2);
            return plagiat;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Swap swap = (Swap) o;
            return machine == swap.machine && t1 == swap.t1 && t2 == swap.t2;
        }

        @Override
        public int hashCode() {
            return Objects.hash(machine, t1, t2);
        }
    }


    @Override
    public List<ResourceOrder> generateNeighbors(ResourceOrder current) {
        // convert the list of swaps into a list of neighbors (function programming FTW)
        return allSwaps(current).stream().map(swap -> swap.generateFrom(current)).collect(Collectors.toList());
    }


    public List<PairRessourceOrderPairTask> generateNeighborsTaboo(ResourceOrder current,  PairTask[] tab ) {
        // convert the list of swaps into a list of neighbors (function programming FTW)
        List<Swap> list = allSwapsTaboo(current, tab);
        List<PairRessourceOrderPairTask> out = new ArrayList<>();

        for(var swap : list){
            out.add(new PairRessourceOrderPairTask(swap.generateFrom(current), swapToPair(current, swap)));
        }
        return out;
    }

    /** Generates all swaps of the given ResourceOrder.
     * This method can be used if one wants to access the inner fields of a neighbors. */
    public List<Swap> allSwaps(ResourceOrder current) {
        List<Swap> neighbors = new ArrayList<>();
        // iterate over all blocks of the critical path
        for(var block : blocksOfCriticalPath(current)) {
            // for this block, compute all neighbors and add them to the list of neighbors
            neighbors.addAll(neighbors(block));
        }
        return neighbors;
    }
    private PairTask swapToPair(ResourceOrder current,Swap swap){
        return new PairTask (current.getTaskOfMachine(swap.machine,swap.t1), current.getTaskOfMachine(swap.machine,swap.t2));
    }

    public List<Swap> allSwapsTaboo(ResourceOrder current, PairTask[] tab ) {
        List<Swap> neighbors = new ArrayList<>();
        // iterate over all blocks of the critical path
        for(var block : blocksOfCriticalPath(current)) {
            // for this block, compute all neighbors and add them to the list of neighbors
            neighbors.addAll(neighbors(block));
        }

        for(int index =0 ; index < tab.length; index++){
            if (tab[index]!=null) {
                List<Swap> listIndex = new ArrayList<>();

                for(int i =0; i < neighbors.size(); i++){
                    PairTask pairTask = swapToPair(current,neighbors.get(i));

                    if (Objects.equals(pairTask.toString(), tab[index].toString())) {
                        listIndex.add(neighbors.get(i));
                    }
                }

                for (var i : listIndex){
                    neighbors.remove(i);
                }
            }

        }
        return neighbors;
    }

    private int FindTaskIndex (ResourceOrder order, int machine, Task task){

        int trouver = 0;
        while (!(order.getTaskOfMachine(machine, trouver).equals(task))){
            trouver++;
        }
        return trouver;
    }

    /** Returns a list of all the blocks of the critical path. */
    List<Block> blocksOfCriticalPath(ResourceOrder order) {
        List<Task> chemin = order.toSchedule().get().criticalPath();

        Task task = chemin.get(0);
        int machine = order.instance.machine(task.job,task.task);
        int debut = 0;
        int fin =0;

        List<Block> out = new ArrayList<>();

        for (Task t : chemin){

            int machine_tmp = order.instance.machine(t.job,t.task);
            if (machine_tmp==machine){

                fin = FindTaskIndex(order, machine, t);
            }else{
                if (debut<fin){
                    out.add(new Block(machine,debut,fin));
                }

                machine = machine_tmp;
                debut = FindTaskIndex(order, machine_tmp, t);
            }
        }

        if (debut<fin){
            out.add(new Block(machine,debut,fin));
        }

        return out;

    }

    /** For a given block, return the possible swaps for the Nowicki and Smutnicki neighborhood */
    List<Swap> neighbors(Block block) {
        List<Swap> out = new ArrayList<>();

        out.add(new Swap(block.machine, block.firstTask, block.firstTask+1));
        if(block.firstTask!=block.lastTask+1){
            out.add(new Swap(block.machine, block.lastTask-1, block.lastTask));
        }
        return out;
    }

}
