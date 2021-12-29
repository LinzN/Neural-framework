package de.linzn.neuralFramework.voiceEngine.stemBox;

import de.linzn.neuralFramework.NeuralFrameworkPlugin;
import de.linzn.neuralFramework.neuralStructure.NeuralProcessor;
import de.linzn.neuralFramework.voiceEngine.VoiceEngine;
import de.linzn.neuralFramework.voiceEngine.VoiceInputEvent;
import de.stem.stemSystem.STEMSystemApp;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;
import org.vosk.Model;
import org.vosk.Recognizer;

import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

public class StemBoxClient implements Runnable {

    private final int stemBoxId;
    private final Recognizer recognizer;
    private final AtomicBoolean keywordSpotted;
    private final AtomicBoolean running;
    private VoiceEngine voiceEngine;
    private StemVoiceSocket stemVoiceSocket;

    public StemBoxClient(int stemBoxId, Model model, float sampleRate, VoiceEngine voiceEngine) {
        this.recognizer = new Recognizer(model, sampleRate);
        this.stemBoxId = stemBoxId;
        this.keywordSpotted = new AtomicBoolean(false);
        this.running = new AtomicBoolean(true);
        this.voiceEngine = voiceEngine;
    }

    @Override
    public void run() {
        while (this.running.get()) {
            try {
                int bytesRead;
                byte[] b = new byte[4096];
                while (this.isValidSocket() && (bytesRead = this.stemVoiceSocket.getInStream().read(b)) >= 0) {
                    if (this.recognizer.acceptWaveForm(b, bytesRead)) {
                        JSONObject result = new JSONObject(this.recognizer.getResult());

                        LinkedList<String> wordList = this.split_to_linked_list(result.getString("text"));

                        if (!wordList.isEmpty()) {
                            for (String KEYWORD : voiceEngine.getKeywords()) {
                                if (wordList.contains(KEYWORD)) {
                                    wordList.remove(KEYWORD);
                                    this.setKeywordSpotted(true);
                                }
                            }

                            if (this.keywordSpotted.get() && !wordList.isEmpty()) {
                                VoiceInputEvent voiceInputEvent = new VoiceInputEvent(result, wordList);
                                STEMSystemApp.getInstance().getEventModule().getStemEventBus().fireEvent(voiceInputEvent);

                                if (!voiceInputEvent.isCanceled()) {
                                    NeuralProcessor neuralProcessor = NeuralFrameworkPlugin.neuralFrameworkPlugin.getNeuralEngine().createNeuralProcessor();
                                    neuralProcessor.submit(wordList);
                                    boolean status = neuralProcessor.wasSuccess();
                                    JSONObject completeData = neuralProcessor.getCompleteData();
                                    STEMSystemApp.LOGGER.DEBUG("NeuralProcessor task exit with status " + status);
                                }
                                this.setKeywordSpotted(false); //todo move higher
                            }
                        } else {
                            this.setKeywordSpotted(false);
                        }
                    } else {
                        JSONObject result = new JSONObject(recognizer.getPartialResult());
                        LinkedList<String> wordList = this.split_to_linked_list(result.getString("partial"));
                        if (!wordList.isEmpty()) {
                            for (String KEYWORD : voiceEngine.getKeywords()) {
                                if (wordList.contains(KEYWORD)) {
                                    this.setKeywordSpotted(true);
                                }
                            }
                        }
                    }
                }
                if (this.stemVoiceSocket != null) {
                    this.setStemVoiceSocket(null);
                    STEMSystemApp.LOGGER.INFO("Clearing and reset recognizer!");
                    this.recognizer.reset();
                }
                Thread.sleep(500);
            } catch (IOException | InterruptedException e) {
                STEMSystemApp.LOGGER.ERROR(e);
            }
        }

        STEMSystemApp.LOGGER.INFO("Stopping StemBoxClient!");
        if (this.stemVoiceSocket != null) {
            this.setStemVoiceSocket(null);
        }
        recognizer.reset();
        recognizer.close();
    }

    public boolean isValidSocket() {
        try {
            if (this.stemVoiceSocket == null) {
                STEMSystemApp.LOGGER.DEBUG("StemVoiceSocket == NULL");
                return false;
            } else if (this.stemVoiceSocket.getSocket().isClosed()) {
                STEMSystemApp.LOGGER.DEBUG("StemVoiceSocket.getSocket().isClosed()");
                return false;
            } else if (this.stemVoiceSocket.getSocket().getInputStream() == null) {
                STEMSystemApp.LOGGER.DEBUG("StemVoiceSocket.getSocket().getInputStream() == null");
                return false;
            } else if (!this.stemVoiceSocket.getSocket().isConnected()) {
                STEMSystemApp.LOGGER.DEBUG("!StemVoiceSocket.getSocket().isConnected()");
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            STEMSystemApp.LOGGER.DEBUG("IOException in valid socket");
            return false;
        }
        return true;
    }

    public void setStemVoiceSocket(StemVoiceSocket stemVoiceSocket) {
        if (stemVoiceSocket != null) {
            STEMSystemApp.LOGGER.INFO("Enable new stemLink voice connection!");
            if (this.stemVoiceSocket != null) {
                STEMSystemApp.LOGGER.INFO("Disable and closing old stemLink voice connection!");
                this.stemVoiceSocket.closeConnection();
            }
            this.stemVoiceSocket = stemVoiceSocket;
        } else {
            STEMSystemApp.LOGGER.INFO("Closing stemLink voice client and set to NULL!");
            this.stemVoiceSocket.closeConnection();
            this.stemVoiceSocket = null;
        }
    }

    public void close() {
        this.setStemVoiceSocket(null);
        this.running.set(false);
    }

    public int getStemBoxId() {
        return stemBoxId;
    }

    private void setKeywordSpotted(boolean value) {
        if (this.keywordSpotted.get() != value) {
            this.keywordSpotted.set(value);
            STEMSystemApp.LOGGER.CORE("KeywordSpotted set to " + value);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("command", "RECORDING");
            jsonObject.put("status", this.keywordSpotted.get() ? "START" : "STOP");
            MqttMessage mqttMessage = new MqttMessage(jsonObject.toString().getBytes());
            STEMSystemApp.getInstance().getMqttModule().publish("stemBox/device_" + this.stemBoxId + "/callback", mqttMessage);
        }
    }

    private LinkedList<String> split_to_linked_list(String input) {

        String[] words = input.split("\\s+");
        LinkedList<String> linkedList = new LinkedList<>();

        for (int i = 0; i < words.length; i++) {
            words[i] = words[i].replaceAll("[^A-Za-z0-9_äöüÄÖÜß]", "");
            if (!words[i].isEmpty()) {
                linkedList.add(words[i].toLowerCase());
            }
        }
        return linkedList;
    }

}
