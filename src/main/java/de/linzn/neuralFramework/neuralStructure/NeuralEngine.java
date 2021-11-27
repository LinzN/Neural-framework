package de.linzn.neuralFramework.neuralStructure;

import de.linzn.neuralFramework.NeuralDatabaseLoader;
import de.stem.stemSystem.STEMSystemApp;

public class NeuralEngine {

    private final NeuralDatabase neuralDatabase;
    private final TaskDatabase taskDatabase;
    private final NeuralDatabaseLoader neuralDatabaseLoader;

    public NeuralEngine() {
        STEMSystemApp.LOGGER.CONFIG("Loading NeuralEngine!");
        this.taskDatabase = new TaskDatabase();
        this.neuralDatabase = new NeuralDatabase();
        this.neuralDatabaseLoader = new NeuralDatabaseLoader(neuralDatabase);
        this.neuralDatabaseLoader.loadDatabase();
    }

    public NeuralDatabase getNeuralDatabase() {
        return neuralDatabase;
    }

    public NeuralProcessor createNeuralProcessor() {
        return new NeuralProcessor(this);
    }

    public TaskDatabase getTaskDatabase() {
        return taskDatabase;
    }
}
