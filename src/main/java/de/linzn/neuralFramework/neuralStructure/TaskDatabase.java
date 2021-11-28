package de.linzn.neuralFramework.neuralStructure;


import de.linzn.neuralFramework.neuralTasks.LightOff;
import de.linzn.neuralFramework.neuralTasks.LightOn;
import de.stem.stemSystem.STEMSystemApp;

import java.util.HashMap;

public class TaskDatabase {


    private static final HashMap<Long, NeuralTask> taskHashMap = new HashMap<>();

    static {
        loadTask(new LightOn());
        loadTask(new LightOff());
    }

    public TaskDatabase() {
        STEMSystemApp.LOGGER.CONFIG("TaskDatabase loaded!");
    }

    private static void loadTask(NeuralTask neuralTask) {
        if (!taskHashMap.containsKey(neuralTask.GET_TASK_ID())) {
            STEMSystemApp.LOGGER.CONFIG("Load neuralTask " + neuralTask.GET_TASK_ID());
            taskHashMap.put(neuralTask.GET_TASK_ID(), neuralTask);
        } else {
            STEMSystemApp.LOGGER.ERROR("NeuralTask " + neuralTask.GET_TASK_ID() + " already exist and loaded!");
        }
    }

    public static NeuralTask getTask(long taskId) {
        return taskHashMap.get(taskId);
    }
}
