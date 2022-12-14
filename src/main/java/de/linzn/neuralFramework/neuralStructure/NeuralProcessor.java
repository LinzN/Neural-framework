package de.linzn.neuralFramework.neuralStructure;

import de.linzn.neuralFramework.neuralStructure.objects.NeuralCombination;
import de.linzn.neuralFramework.neuralStructure.objects.NeuralLocation;
import de.linzn.neuralFramework.neuralStructure.objects.NeuralObject;
import de.stem.stemSystem.STEMSystemApp;
import org.json.JSONObject;

import java.util.List;

public class NeuralProcessor {

    private final NeuralEngine neuralEngine;
    private JSONObject completeData;
    private boolean success = false;

    NeuralProcessor(NeuralEngine neuralEngine) {
        this.neuralEngine = neuralEngine;
        STEMSystemApp.LOGGER.CONFIG("New NeuralProcessor loaded!");
    }

    public void submit(List<String> input) {
        NeuralObject neuralObject = null;
        NeuralCombination neuralCombination = null;
        NeuralLocation neuralLocation = null;
        NeuralTask neuralTask = null;
        JSONObject combinationLocationData = null;

        searchLoop:
        for (String word : input) {
            for (NeuralObject object : neuralEngine.getNeuralDatabase().getObjectSet()) {
                if (object.hasName(word)) {
                    neuralObject = object;
                    STEMSystemApp.LOGGER.DEBUG("Found neuralObject");
                    break searchLoop;
                }
            }
        }

        if (neuralObject == null) {
            STEMSystemApp.LOGGER.DEBUG("No neuralObject found. Return");
            return;
        }

        for (String word : input) {
            neuralCombination = neuralObject.searchCombination(word);
            if (neuralCombination != null) {
                STEMSystemApp.LOGGER.DEBUG("Found neuralCombination");
                break;
            }
        }

        if (neuralCombination == null) {
            STEMSystemApp.LOGGER.DEBUG("No neuralCombination found. Return");
            return;
        }

        for (String word : input) {
            neuralLocation = neuralObject.searchLocation(word);
            if (neuralLocation != null) {
                STEMSystemApp.LOGGER.DEBUG("Found neuralLocation");
                break;
            }
        }

        if (neuralLocation == null) {
            STEMSystemApp.LOGGER.DEBUG("No neuralLocation found. Continue");
        }

        neuralTask = neuralObject.searchTask(neuralCombination.GET_COMBINATION_ID());
        if (neuralTask == null) {
            STEMSystemApp.LOGGER.ERROR("No neuralTask found! Return");
            return;
        }

        combinationLocationData = neuralObject.getCombinationLocationData(neuralCombination, neuralLocation);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("words", input);
        jsonObject.put("data", combinationLocationData);

        try {
            neuralTask.runTask(neuralObject, neuralCombination, neuralLocation, jsonObject);
            this.completeData = neuralTask.taskCompleteData();
            this.success = neuralTask.wasSuccess();
        } catch (Exception e) {
            STEMSystemApp.LOGGER.ERROR(e);
        }
    }

    public JSONObject getCompleteData() {
        return completeData;
    }

    public boolean wasSuccess() {
        return success;
    }
}
