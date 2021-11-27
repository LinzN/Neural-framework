package de.linzn.neuralFramework.neuralStructure;

import de.linzn.neuralFramework.neuralStructure.objects.NeuralCombination;
import de.linzn.neuralFramework.neuralStructure.objects.NeuralLocation;
import de.linzn.neuralFramework.neuralStructure.objects.NeuralObject;
import de.stem.stemSystem.STEMSystemApp;

import java.util.Collection;
import java.util.HashMap;

public class NeuralDatabase {

    private final HashMap<Long, NeuralCombination> combinationMap;
    private final HashMap<Long, NeuralLocation> locationMap;
    private final HashMap<Long, NeuralObject> objectMap;

    public NeuralDatabase() {
        this.combinationMap = new HashMap<>();
        this.locationMap = new HashMap<>();
        this.objectMap = new HashMap<>();
        STEMSystemApp.LOGGER.CONFIG("NeuralDatabase loaded!");
    }

    public void addLocation(NeuralLocation neuralLocation) {
        this.locationMap.put(neuralLocation.GET_LOCATION_ID(), neuralLocation);
    }

    public void addCombination(NeuralCombination neuralCombination) {
        this.combinationMap.put(neuralCombination.GET_COMBINATION_ID(), neuralCombination);
    }

    public void addObject(NeuralObject neuralObject) {
        objectMap.put(neuralObject.GET_OBJECT_ID(), neuralObject);
    }

    public Collection<NeuralCombination> getCombinationSet() {
        return this.combinationMap.values();
    }

    public Collection<NeuralLocation> getLocationSet() {
        return this.locationMap.values();
    }

    public Collection<NeuralObject> getObjectSet() {
        return this.objectMap.values();
    }

    public NeuralLocation getLocation(long id) {
        return this.locationMap.get(id);
    }

    public NeuralObject getObject(long id) {
        return this.objectMap.get(id);
    }

    public NeuralCombination getCombination(long id) {
        return this.combinationMap.get(id);
    }

}
