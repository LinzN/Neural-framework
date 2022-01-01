package de.linzn.neuralFramework.voiceEngine;

import de.linzn.neuralFramework.NeuralFrameworkPlugin;
import de.linzn.neuralFramework.voiceEngine.stemBox.StemBoxClient;
import de.linzn.neuralFramework.voiceEngine.stemLinkVoice.VoiceServer;
import de.stem.stemSystem.STEMSystemApp;
import org.vosk.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;

import static de.linzn.neuralFramework.NeuralFrameworkPlugin.neuralFrameworkPlugin;

public class VoiceEngine {

    private VoiceServer voiceServer;
    private List<String> keywords;
    private Model model;
    private HashMap<UUID, StemBoxClient> clientHashMap;

    public VoiceEngine(NeuralFrameworkPlugin neuralFrameworkPlugin) {
        this.clientHashMap = new HashMap<>();
        List<String> defaultKeywords = new ArrayList<>();
        defaultKeywords.add("alexa");
        defaultKeywords.add("cortana");
        this.keywords = (List<String>) neuralFrameworkPlugin.getDefaultConfig().get("KEYWORDS", defaultKeywords);
        String host = neuralFrameworkPlugin.getDefaultConfig().getString("voiceServer.host", "10.50.0.10");
        int port = neuralFrameworkPlugin.getDefaultConfig().getInt("voiceServer.port", 11105);

        List<String> fallbackUUID = new ArrayList<>();
        fallbackUUID.add(UUID.randomUUID().toString());
        fallbackUUID.add(UUID.randomUUID().toString());
        neuralFrameworkPlugin.getDefaultConfig().get("STEMBOXES", fallbackUUID);

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
        for(String stemBoxString : (List<String>) neuralFrameworkPlugin.getDefaultConfig().get("STEMBOXES")){
            UUID stemBoxUUID = UUID.fromString(stemBoxString);
            StemBoxClient stemBoxClient = new StemBoxClient(stemBoxUUID, this.model, 120000f, this);
            this.clientHashMap.put(stemBoxClient.getStemBoxUUID(), stemBoxClient);
            Executors.newSingleThreadExecutor().submit(stemBoxClient);
        }
    }

    private void loadModel() {
        STEMSystemApp.LOGGER.CORE("Loading VOSK Model!");
        this.model = new Model(neuralFrameworkPlugin.getDataFolder().getAbsolutePath() + "/model");
        STEMSystemApp.LOGGER.CORE("VOSK model loaded!");
    }

    public StemBoxClient getStemBoxVoiceClient(UUID stemBoxUUID) {
        return this.clientHashMap.get(stemBoxUUID);
    }

    public Model getModel() {
        return model;
    }
}
