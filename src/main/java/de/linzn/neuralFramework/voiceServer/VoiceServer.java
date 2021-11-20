package de.linzn.neuralFramework.voiceServer;

import de.linzn.neuralFramework.NeuralFrameworkPlugin;
import de.stem.stemSystem.STEMSystemApp;
import org.vosk.Model;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VoiceServer implements Runnable {

    private final String host;
    private final int port;
    private Model model;
    ServerSocket server;
    Map<UUID, VoiceServerClient> voiceClients;

    public VoiceServer(String host, int port) {
        this.host = host;
        this.port = port;
        this.voiceClients = new HashMap<>();
        this.loadModel();
    }


    public void openServer() {
        try {
            this.server = new ServerSocket();
            this.server.bind(new InetSocketAddress(this.host, this.port));
            STEMSystemApp.getInstance().getScheduler().runTask(NeuralFrameworkPlugin.neuralFrameworkPlugin, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeServer() {
        try {
            this.server.close();
            ArrayList<UUID> uuidList = new ArrayList<>(this.voiceClients.keySet());
            for (UUID uuid : uuidList) {
                this.voiceClients.get(uuid).setDisable();
            }
            this.voiceClients.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void loadModel(){
        STEMSystemApp.LOGGER.CORE("Loading VOSK-API model start");
        this.model = new Model(NeuralFrameworkPlugin.neuralFrameworkPlugin.getDataFolder().getAbsolutePath() + "/model");
        STEMSystemApp.LOGGER.CORE("Loading VOSK-API model done");
    }

    public Model getModel() {
        return model;
    }


    @Override
    public void run() {
        Thread.currentThread().setName("VoiceServer");
        STEMSystemApp.LOGGER.CORE("Startup voiceServer done. Waiting for connections");
        do {
            try {
                Socket socket = this.server.accept();
                socket.setTcpNoDelay(true);
                VoiceServerClient voiceServerClient = new VoiceServerClient(socket, this);
                voiceServerClient.setEnable();
            } catch (IOException e) {
                STEMSystemApp.LOGGER.ERROR("VoiceServerClient already closed!");
            }
        } while (!this.server.isClosed());

    }
}
