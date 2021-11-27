package de.linzn.neuralFramework.neuralStructure.objects;

import java.util.HashSet;
import java.util.Set;

public class NeuralCombination {

    private final long id;
    private final Set<String> nameSet;

    public NeuralCombination(long id) {
        this.id = id;
        this.nameSet = new HashSet<>();
    }

    public boolean hasName(String name) {
        return this.nameSet.contains(name.toLowerCase());
    }

    public void ADD_NAME(String name) {
        this.nameSet.add(name.toLowerCase());
    }

    public long GET_COMBINATION_ID() {
        return id;
    }
}
