package de.linzn.neuralFramework.neuralStructure;

import de.linzn.neuralFramework.neuralStructure.objects.NeuralCombination;
import de.linzn.neuralFramework.neuralStructure.objects.NeuralLocation;
import de.linzn.neuralFramework.neuralStructure.objects.NeuralObject;
import org.json.JSONObject;

public interface NeuralTask {

    void runTask(NeuralObject neuralObject, NeuralCombination neuralCombination, NeuralLocation neuralLocation, JSONObject otherInput);

    JSONObject taskCompleteData();

    boolean wasSuccess();

    long GET_TASK_ID();
}
