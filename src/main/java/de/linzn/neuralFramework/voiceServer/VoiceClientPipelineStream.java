package de.linzn.neuralFramework.voiceServer;

import de.stem.stemSystem.STEMSystemApp;
import org.json.JSONObject;
import org.vosk.Model;
import org.vosk.Recognizer;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.LinkedList;

public class VoiceClientPipelineStream implements Runnable {
    private final VoiceServerClient voiceServerClient;

    public PipedOutputStream pipedOutputStream;
    private PipedInputStream pipedInputStream;
    private Model model;

    private static final String KEYWORD = "alexa";

    private boolean keywordSpotted = false;

    public VoiceClientPipelineStream(VoiceServerClient voiceServerClient, Model model) {
        STEMSystemApp.LOGGER.CORE("Setup PipelineStream for new voice connection!");
        this.model = model;
        this.voiceServerClient = voiceServerClient;
        this.pipedOutputStream = new PipedOutputStream();
        try {
            this.pipedInputStream = new PipedInputStream(this.pipedOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        STEMSystemApp.LOGGER.CORE("Enable Pipeline for new voice connection");
        InputStream ais = this.pipedInputStream;
        STEMSystemApp.LOGGER.CORE("Loading VOSK-API Recognizer for new voice connection");
        Recognizer recognizer = new Recognizer(model, 120000);
        STEMSystemApp.LOGGER.CORE("VOSK-API enabled for new voice connection");
        while (voiceServerClient.isValidConnection()) {
            try {
                int nbytes;
                byte[] b = new byte[8192];
                while ((nbytes = ais.read(b)) >= 0) {
                    if (recognizer.acceptWaveForm(b, nbytes)) {
                        JSONObject result = new JSONObject(recognizer.getResult());

                        LinkedList<String> wordList = this.splitIntoWords(result.getString("text"));

                        if (!wordList.isEmpty()) {
                            if (wordList.contains(KEYWORD)) {
                                wordList.remove(KEYWORD);
                                this.setKeywordSpotted(true);
                            }

                            if (this.keywordSpotted && !wordList.isEmpty()) {
                                VoiceInputEvent voiceInputEvent = new VoiceInputEvent(result, wordList);
                                STEMSystemApp.getInstance().getEventModule().getStemEventBus().fireEvent(voiceInputEvent);

                                if (!voiceInputEvent.isCanceled()) {
                                    //do something
                                }
                                this.setKeywordSpotted(false);
                            }
                        } else {
                            this.setKeywordSpotted(false);
                        }
                    } else {
                        JSONObject result = new JSONObject(recognizer.getPartialResult());
                        LinkedList<String> wordList = this.splitIntoWords(result.getString("partial"));
                        if (!wordList.isEmpty()) {
                            if (wordList.contains(KEYWORD)) {
                                this.setKeywordSpotted(true);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setKeywordSpotted(boolean value) {
        if(this.keywordSpotted != value){
            this.keywordSpotted = value;
            STEMSystemApp.LOGGER.CORE("KeywordSpotted set to " + value);
            //publish mqtt that keyword is spotted
        }
    }

    private LinkedList<String> splitIntoWords(String input){

        String[] words = input.split("\\s+");
        LinkedList<String> linkedList = new LinkedList<>();

        for (int i = 0; i < words.length; i++) {
            words[i] = words[i].replaceAll("[^A-Za-z0-9_äöüÄÖÜß]", "");
            if(!words[i].isEmpty()){
                linkedList.add(words[i].toLowerCase());
            }
        }
        return linkedList;
    }
}
