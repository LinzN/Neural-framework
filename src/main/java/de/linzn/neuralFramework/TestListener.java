package de.linzn.neuralFramework;

import de.linzn.neuralFramework.voiceEngine.VoiceInputEvent;
import de.stem.stemSystem.STEMSystemApp;
import de.stem.stemSystem.modules.eventModule.handler.StemEventHandler;

import java.util.Arrays;

public class TestListener {

    @StemEventHandler
    public void onVoiceEvent(VoiceInputEvent voiceInputEvent) {
        STEMSystemApp.LOGGER.CONFIG("#########WORDS##########");
        STEMSystemApp.LOGGER.CONFIG(Arrays.toString(voiceInputEvent.getWordList().toArray()));
        STEMSystemApp.LOGGER.CONFIG("Size: " + voiceInputEvent.getWordList().size());
    }
}
