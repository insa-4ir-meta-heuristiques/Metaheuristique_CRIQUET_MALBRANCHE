package jobshop.solvers.neighborhood;

import jobshop.encodings.PairTask;
import jobshop.encodings.ResourceOrder;
import jobshop.encodings.Task;

public class PairRessourceOrderPairTask {

    public final ResourceOrder resourceOrder;
    public final PairTask pairTask;

    public PairRessourceOrderPairTask(ResourceOrder resourceOrder, PairTask pairTask) {
        this.resourceOrder  = resourceOrder;
        this.pairTask = pairTask;
    }

}
