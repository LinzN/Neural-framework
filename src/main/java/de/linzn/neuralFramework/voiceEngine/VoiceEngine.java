package de.linzn.neuralFramework.voiceEngine;

import de.linzn.neuralFramework.NeuralFrameworkPlugin;
import de.linzn.neuralFramework.voiceEngine.stemBox.StemBoxClient;
import de.linzn.neuralFramework.voiceEngine.stemLinkLite.VoiceServer;
import de.stem.stemSystem.STEMSystemApp;
import org.vosk.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;

public class VoiceEngine {

    private VoiceServer voiceServer;
    private List<String> keywords;
    private Model model;
    private HashMap<Integer, StemBoxClient> clientHashMap;

    public VoiceEngine(NeuralFrameworkPlugin neuralFrameworkPlugin) {
        this.clientHashMap = new HashMap<>();
        List<String> defaultKeywords = new ArrayList<>();
        defaultKeywords.add("alexa");
        defaultKeywords.add("cortana");
        this.keywords = (List<String>) neuralFrameworkPlugin.getDefaultConfig().get("KEYWORDS", defaultKeywords);
        String host = neuralFrameworkPlugin.getDefaultConfig().getString("voiceServer.host", "10.50.0.10");
        int port = neuralFrameworkPlugin.getDefaultConfig().getInt("voiceServer.port", 11105);
        neuralFrameworkPlugin.getDefaultConfig().save();
        this.loadModel();
        this.voiceServer = new VoiceServer(host, port, this);
    }

    public void start() {
        this.loadStemBoxClients();
        this.voiceServer.openServer();
    }

    public void close() {
        this.voiceServer.closeServer();
        for(StemBoxClient stemBoxClient : this.clientHashMap.values()){
            stemBoxClient.close();
        }
        this.clientHashMap.clear();
    }

    public List<String> getKeywords() {
        return keywords;
    }

    private void loadStemBoxClients() {
        int stemBoxId = 1;
        StemBoxClient stemBoxClient = new StemBoxClient(stemBoxId, this.model, 120000f, this);
        this.clientHashMap.put(stemBoxClient.getStemBoxId(), stemBoxClient);
        Executors.newSingleThreadExecutor().submit(stemBoxClient);
        //todo load boxes
    }

    private void loadModel() {
        STEMSystemApp.LOGGER.CORE("Loading VOSK Model!");
        this.model = new Model(NeuralFrameworkPlugin.neuralFrameworkPlugin.getDataFolder().getAbsolutePath() + "/model");
        STEMSystemApp.LOGGER.CORE("VOSK model loaded!");
    }

    public StemBoxClient getStemBoxVoiceClient(int stemBoxId) {
        return this.clientHashMap.get(stemBoxId);
    }

    public Model getModel() {
        return model;
    }
}
