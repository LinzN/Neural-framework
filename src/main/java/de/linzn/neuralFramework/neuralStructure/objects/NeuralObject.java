package de.linzn.neuralFramework.neuralStructure.objects;

import de.linzn.neuralFramework.neuralStructure.NeuralTask;
import de.linzn.neuralFramework.neuralStructure.TaskDatabase;
import de.linzn.openJL.pairs.Pair;
import de.stem.stemSystem.STEMSystemApp;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class NeuralObject {

    private final long id;
    private final Set<NeuralCombination> neuralCombinationSet;
    private final Set<NeuralLocation> neuralLocationSet;
    private final HashMap<Long, Long> taskAssignment;
    private final HashMap<Pair<NeuralLocation,NeuralCombination>, JSONObject> neuralLocationCombinationData;
    private final Set<String> nameSet;

    public NeuralObject(long id) {
        this.id = id;
        this.neuralCombinationSet = new HashSet<>();
        this.neuralLocationSet = new HashSet<>();
        this.nameSet = new HashSet<>();
        this.taskAssignment = new HashMap<>();
        this.neuralLocationCombinationData = new HashMap<>();
    }

    public boolean hasCombination(NeuralCombination neuralCombination) {
        return this.neuralCombinationSet.contains(neuralCombination);
    }

    public boolean hasLocation(NeuralLocation neuralLocation) {
        return this.neuralLocationSet.contains(neuralLocation);
    }

    public boolean hasName(String name) {
        return this.nameSet.contains(name.toLowerCase());
    }

    public void registerCombination(NeuralCombination neuralCombination, long taskId) {
        if (neuralCombination != null) {
            if (TaskDatabase.getTask(taskId) != null) {
                this.neuralCombinationSet.add(neuralCombination);
                this.taskAssignment.put(neuralCombination.GET_COMBINATION_ID(), taskId);
            } else {
                STEMSystemApp.LOGGER.ERROR("NeuralTask not found!");
            }
        } else {
            STEMSystemApp.LOGGER.ERROR("NeuralCombination is NULL!");
        }
    }

    public void registerLocation(NeuralLocation neuralLocation) {
        if (neuralLocation != null) {
            this.neuralLocationSet.add(neuralLocation);
        } else {
            STEMSystemApp.LOGGER.ERROR("NeuralLocation is NULL!");
        }
    }

    public void addCombinationLocationData(NeuralCombination neuralCombination, NeuralLocation neuralLocation, JSONObject data){
        this.neuralLocationCombinationData.put(new Pair<>(neuralLocation, neuralCombination), data);
    }

    public JSONObject getCombinationLocationData(NeuralCombination neuralCombination, NeuralLocation neuralLocation){
        for(Pair<NeuralLocation,NeuralCombination> pair : this.neuralLocationCombinationData.keySet()){
            if(pair.getKey() == neuralLocation && pair.getValue() == neuralCombination){
                return this.neuralLocationCombinationData.get(pair);
            }
        }
        return null;
    }

    public void ADD_NAME(String name) {
        if (name != null && !name.isEmpty()) {
            this.nameSet.add(name.toLowerCase());
        } else {
            STEMSystemApp.LOGGER.ERROR("Name is NULL or EMPTY");
        }
    }

    public long GET_OBJECT_ID() {
        return id;
    }

    public NeuralCombination searchCombination(String word) {
        for (NeuralCombination neuralCombination : this.neuralCombinationSet) {
            if (neuralCombination.hasName(word)) {
                return neuralCombination;
            }
        }
        return null;
    }

    public NeuralLocation searchLocation(String word) {
        for (NeuralLocation neuralLocation : this.neuralLocationSet) {
            if (neuralLocation.hasName(word)) {
                return neuralLocation;
            }
        }
        return null;
    }

    public NeuralTask searchTask(long combination_id) {
        Long task_id = this.taskAssignment.get(combination_id);

        if (task_id == null) {
            return null;
        }
        return TaskDatabase.getTask(task_id);
    }

}
