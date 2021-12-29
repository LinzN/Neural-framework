/*
 * Copyright (C) 2020. Niklas Linz - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the LGPLv3 license, which unfortunately won't be
 * written for another century.
 *
 * You should have received a copy of the LGPLv3 license with
 * this file. If not, please write to: niklas.linz@enigmar.de
 *
 */

package de.linzn.neuralFramework;


import de.linzn.neuralFramework.neuralStructure.NeuralEngine;
import de.linzn.neuralFramework.voiceEngine.VoiceEngine;
import de.stem.stemSystem.STEMSystemApp;
import de.stem.stemSystem.modules.pluginModule.STEMPlugin;

public class NeuralFrameworkPlugin extends STEMPlugin {

    public static NeuralFrameworkPlugin neuralFrameworkPlugin;
    private NeuralEngine neuralEngine;
    private VoiceEngine voiceEngine;

    @Override
    public void onEnable() {
        neuralFrameworkPlugin = this;
        this.neuralEngine = new NeuralEngine();
        setupVoiceEngine();
        STEMSystemApp.getInstance().getEventModule().getStemEventBus().register(new TestListener());
    }

    @Override
    public void onDisable() {
        this.voiceEngine.close();
        STEMSystemApp.getInstance().getCallBackService().unregisterCallbackListeners(this);
    }

    private void setupVoiceEngine() {
        this.voiceEngine = new VoiceEngine(this);
        this.voiceEngine.start();
    }

    public NeuralEngine getNeuralEngine() {
        return neuralEngine;
    }

    public VoiceEngine getVoiceEngine() {
        return voiceEngine;
    }
}
