package de.linzn.neuralFramework;

import de.linzn.neuralFramework.voiceServer.VoiceInputEvent;
import de.stem.stemSystem.STEMSystemApp;
import de.stem.stemSystem.modules.eventModule.handler.StemEventHandler;

import java.util.LinkedList;

public class TestListener {

    @StemEventHandler
    public void onVoiceEvent(VoiceInputEvent voiceInputEvent){
        STEMSystemApp.LOGGER.CONFIG("#########WORDS##########");
        STEMSystemApp.LOGGER.CONFIG(voiceInputEvent.getWordList());
        STEMSystemApp.LOGGER.CONFIG("Size: "+voiceInputEvent.getWordList().size());
    }
}
