package jobshop.encodings;

public class PairTask {


    public final Task task1;
    public final Task task2;

    public PairTask(Task task1, Task task2) {
        this.task1 = task1;
        this.task2 = task2;
    }

    @Override
    public String toString() {
        return "tache 1  "+ task1.toString() + "    tache 2  "+ task2.toString();
    }

    public PairTask Reverse(){
        return new PairTask(task2,task1);
    }
}
