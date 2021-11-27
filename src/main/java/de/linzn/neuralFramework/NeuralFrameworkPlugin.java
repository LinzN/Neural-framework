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
import de.linzn.neuralFramework.voiceServer.VoiceServer;
import de.stem.stemSystem.STEMSystemApp;
import de.stem.stemSystem.modules.pluginModule.STEMPlugin;

public class NeuralFrameworkPlugin extends STEMPlugin {

    public static NeuralFrameworkPlugin neuralFrameworkPlugin;
    private NeuralEngine neuralEngine;
    private VoiceServer voiceServer;

    @Override
    public void onEnable() {
        neuralFrameworkPlugin = this;
        this.neuralEngine = new NeuralEngine();
        setupVoiceServer();
        STEMSystemApp.getInstance().getEventModule().getStemEventBus().register(new TestListener());
    }

    @Override
    public void onDisable() {
        this.voiceServer.closeServer();
        STEMSystemApp.getInstance().getCallBackService().unregisterCallbackListeners(this);
    }

    private void setupVoiceServer() {
        String host = this.getDefaultConfig().getString("voiceServer.host", "10.50.0.10");
        int port = this.getDefaultConfig().getInt("voiceServer.port", 11105);
        this.getDefaultConfig().save();

        this.voiceServer = new VoiceServer(host, port);
        this.voiceServer.openServer();
    }

    public NeuralEngine getNeuralEngine() {
        return neuralEngine;
    }
}
