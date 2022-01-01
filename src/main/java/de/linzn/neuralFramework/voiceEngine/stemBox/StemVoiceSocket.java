package de.linzn.neuralFramework.voiceEngine.stemBox;

import de.linzn.neuralFramework.voiceEngine.stemLinkVoice.VoiceServer;
import de.stem.stemSystem.STEMSystemApp;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.UUID;

public class StemVoiceSocket {
    private final Socket socket;
    private final VoiceServer voiceServer;


    public StemVoiceSocket(Socket socket, VoiceServer voiceServer) {
        this.socket = socket;
        this.voiceServer = voiceServer;
    }

    public UUID read_stemBox_data() {
        UUID stemBoxUUID = null;
        try {
            BufferedInputStream bInStream = new BufferedInputStream(this.socket.getInputStream());
            DataInputStream dataInput = new DataInputStream(bInStream);

            String channel = dataInput.readUTF();

            if (channel.equalsIgnoreCase("stemBox_uuid")) {
                stemBoxUUID = UUID.fromString(dataInput.readUTF());
                STEMSystemApp.LOGGER.CONFIG("Getting stemBoxUUID " + stemBoxUUID);
            } else {
                STEMSystemApp.LOGGER.ERROR("Unknown channel header");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return stemBoxUUID;
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
