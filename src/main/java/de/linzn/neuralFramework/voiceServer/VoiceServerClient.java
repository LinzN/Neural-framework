package de.linzn.neuralFramework.voiceServer;

import de.linzn.neuralFramework.NeuralFrameworkPlugin;
import de.stem.stemSystem.STEMSystemApp;

import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

public class VoiceServerClient implements Runnable {

    private final Socket socket;
    private final VoiceServer voiceServer;
    private final VoiceClientPipelineStream voiceClientPipelineStream;
    private final UUID uuid;

    public VoiceServerClient(Socket socket, VoiceServer voiceServer) {
        this.uuid = UUID.randomUUID();
        this.socket = socket;
        this.voiceServer = voiceServer;
        STEMSystemApp.LOGGER.CORE("New voice connection!");
        this.voiceClientPipelineStream = new VoiceClientPipelineStream(this, voiceServer.getModel());
    }

    public synchronized void setEnable() {
        this.voiceServer.voiceClients.put(this.uuid, this);
        STEMSystemApp.getInstance().getScheduler().runTask(NeuralFrameworkPlugin.neuralFrameworkPlugin, this);
    }

    public synchronized void setDisable() {
        this.closeConnection();
    }


    protected void readInput() throws IOException {
        byte[] bytes = new byte[512];
        this.socket.getInputStream().read(bytes, 0, 512);
        this.voiceClientPipelineStream.pipedOutputStream.write(bytes);
    }


    @Override
    public void run() {
        STEMSystemApp.getInstance().getScheduler().runTask(NeuralFrameworkPlugin.neuralFrameworkPlugin, this.voiceClientPipelineStream);
        try {
            while (!this.voiceServer.server.isClosed() && this.isValidConnection()) {
                this.readInput();
            }
        } catch (IOException e2) {
            this.closeConnection();
        }
    }

    public synchronized void closeConnection() {
        if (!this.socket.isClosed()) {
            try {
                this.socket.close();
            } catch (IOException ignored) {
            }
        }
        STEMSystemApp.LOGGER.CORE("Close voice connection: " + this.uuid);
        this.voiceServer.voiceClients.remove(this.uuid);
    }

    public boolean isValidConnection() {
        return this.socket.isConnected() && !this.socket.isClosed();
    }


}
