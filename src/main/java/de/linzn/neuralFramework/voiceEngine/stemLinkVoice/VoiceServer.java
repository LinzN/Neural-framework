package de.linzn.neuralFramework.voiceEngine.stemLinkVoice;

import de.linzn.neuralFramework.NeuralFrameworkPlugin;
import de.linzn.neuralFramework.voiceEngine.VoiceEngine;
import de.linzn.neuralFramework.voiceEngine.stemBox.StemBoxClient;
import de.linzn.neuralFramework.voiceEngine.stemBox.StemVoiceSocket;
import de.stem.stemSystem.STEMSystemApp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;

public class VoiceServer implements Runnable {

    private final String host;
    private final int port;
    private ServerSocket server;
    private final VoiceEngine voiceEngine;

    public VoiceServer(String host, int port, VoiceEngine voiceEngine) {
        this.host = host;
        this.port = port;
        this.voiceEngine = voiceEngine;
    }


    public void openServer() {
        try {
            this.server = new ServerSocket();
            this.server.bind(new InetSocketAddress(this.host, this.port));
            STEMSystemApp.getInstance().getScheduler().runTask(NeuralFrameworkPlugin.neuralFrameworkPlugin, this);
        } catch (IOException e) {
            STEMSystemApp.LOGGER.ERROR(e);
        }
    }

    public void closeServer() {
        try {
            this.server.close();
        } catch (IOException e) {
            STEMSystemApp.LOGGER.ERROR(e);
        }

    }


    @Override
    public void run() {
        Thread.currentThread().setName("VoiceServer");
        STEMSystemApp.LOGGER.CORE("Startup voiceServer done. Waiting for connections");
        do {
            try {
                Socket socket = this.server.accept();
                socket.setTcpNoDelay(true);
                socket.setSoTimeout(5000);
                StemVoiceSocket stemVoiceSocket = new StemVoiceSocket(socket, this);
                UUID stemBoxUUID = stemVoiceSocket.read_stemBox_data();

                if (stemBoxUUID != null) {
                    StemBoxClient stemBoxClient = this.voiceEngine.getStemBoxVoiceClient(stemBoxUUID);
                    if (stemBoxClient != null) {
                        stemBoxClient.setStemVoiceSocket(stemVoiceSocket);
                    } else {
                        STEMSystemApp.LOGGER.ERROR("Closing VoiceServerClient because no client exist with this uuid");
                        stemVoiceSocket.closeConnection();
                    }
                } else {
                    STEMSystemApp.LOGGER.ERROR("No UUID transmitted");
                    stemVoiceSocket.closeConnection();
                }
            } catch (IOException e) {
                STEMSystemApp.LOGGER.ERROR("VoiceServerClient already closed!");
            }
        } while (!this.server.isClosed());

    }
}
