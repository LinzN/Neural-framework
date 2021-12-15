package de.linzn.neuralFramework.voiceServer;

import de.linzn.neuralFramework.NeuralFrameworkPlugin;
import de.stem.stemSystem.STEMSystemApp;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
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


    public synchronized void setEnable() {
        this.voiceServer.voiceClients.put(this.uuid, this);
        STEMSystemApp.getInstance().getScheduler().runTask(NeuralFrameworkPlugin.neuralFrameworkPlugin, this);
    }

    public synchronized void setDisable() {
        this.closeConnection();
    }


    protected void readInput() throws IOException {
        byte[] bytes = new byte[1024];
        int bytesRead = this.socket.getInputStream().read(bytes);
        this.voiceClientPipelineStream.pipedOutputStream.write(bytes,0,bytesRead);
    }

    public void writeData(byte[] b, int off, int len) {
        if (this.isValidConnection()) {
            try {
                BufferedOutputStream bOutStream = new BufferedOutputStream(this.socket.getOutputStream());
                DataOutputStream dataOut = new DataOutputStream(bOutStream);
                dataOut.write(b, off, len);
                bOutStream.flush();
            } catch (IOException e) {
                STEMSystemApp.LOGGER.ERROR("Is already closed!");
                closeConnection();
            }

        } else {
            STEMSystemApp.LOGGER.ERROR("no connection");
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
