package de.linzn.neuralFramework.voiceEngine.stemBox;

import de.linzn.neuralFramework.voiceEngine.stemLinkLite.VoiceServer;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class StemVoiceSocket {
    private final Socket socket;
    private final VoiceServer voiceServer;


    public StemVoiceSocket(Socket socket, VoiceServer voiceServer) {
        this.socket = socket;
        this.voiceServer = voiceServer;
    }

    public int read_stemBox_data() {
        int stemBoxId = -1;
        try {
            BufferedInputStream bInStream = new BufferedInputStream(this.socket.getInputStream());
            DataInputStream dataInput = new DataInputStream(bInStream);

            String channel = dataInput.readUTF();

            if (channel.equalsIgnoreCase("stemBoxId")) {
                stemBoxId = dataInput.readInt();
            }

            bInStream.close();
            dataInput.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return stemBoxId;
    }

    public synchronized InputStream getInStream() throws IOException {
        return this.socket.getInputStream();
    }

    public synchronized Socket getSocket() {
        return this.socket;
    }

    public synchronized void closeConnection() {
        if (!this.socket.isClosed()) {
            try {
                this.socket.close();
            } catch (IOException ignored) {
            }
        }
    }

    public synchronized VoiceServer getVoiceServer() {
        return voiceServer;
    }
}
