package de.linzn.neuralFramework.voiceServer;

import de.stem.stemSystem.modules.eventModule.StemEvent;
import org.json.JSONObject;

import java.util.LinkedList;

public class VoiceInputEvent implements StemEvent {
    private boolean isCanceled;
    private final JSONObject eventData;
    private final LinkedList<String> wordList;

    public VoiceInputEvent(JSONObject eventData, LinkedList<String> wordList){
        this.isCanceled = false;
        this.eventData = eventData;
        this.wordList = wordList;
    }

    @Override
    public boolean isCanceled() {
        return isCanceled;
    }

    @Override
    public void setCanceled(boolean canceled) {
        isCanceled = canceled;
    }

    public JSONObject getEventData() {
        return eventData;
    }

    public String getSpeechToTextData(){
        return this.eventData.getString("text");
    }

    public LinkedList<String> getWordList() {
        return wordList;
    }
}
