package dev.dubsky.anitogether.player;

import java.io.*;
import java.net.StandardProtocolFamily;
import java.net.UnixDomainSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class MpvController {
    private static MpvController instance;
    private String ipcPath;
    private boolean isWindows;
    private boolean isPlaying;

    private MpvController() {
        this.isWindows = System.getProperty("os.name").toLowerCase().contains("win");
        this.ipcPath = isWindows ? "\\\\.\\pipe\\mpv-anitogether" : "/tmp/mpv-anitogether";
    }

    public static synchronized MpvController getInstance() {
        if (instance == null) {
            instance = new MpvController();
        }
        return instance;
    }

    public void startMpv(String streamUrl, String subtitleUrl) throws IOException {
        String command = isWindows
            ? "cmd /c start mpv --quiet --profile=low-latency --cache=no --input-ipc-server=" + ipcPath + " --sub-file=" + subtitleUrl + " " + streamUrl
            : "mpv --quiet --profile=low-latency --cache=no --input-ipc-server=" + ipcPath + " --sub-file=" + subtitleUrl + " " + streamUrl;

        ProcessBuilder processBuilder = new ProcessBuilder(isWindows ? "cmd.exe" : "sh", "-c", command);
        processBuilder.start();
        isPlaying = true;
    }

    private void sendCommand(String command) throws IOException {
        if (isWindows) {
            try (RandomAccessFile pipe = new RandomAccessFile(ipcPath, "rw")) {
                pipe.write((command + "\n").getBytes());
            }
        } else {
            UnixDomainSocketAddress address = UnixDomainSocketAddress.of(ipcPath);
            try (SocketChannel channel = SocketChannel.open(StandardProtocolFamily.UNIX)) {
                channel.connect(address);
                channel.write(ByteBuffer.wrap((command + "\n").getBytes()));
            }
        }
    }

    private String sendCommandWithResponse(String command) throws IOException {
        StringBuilder response = new StringBuilder();
        if (isWindows) {
            try (RandomAccessFile pipe = new RandomAccessFile(ipcPath, "rw")) {
                pipe.write((command + "\n").getBytes());
                String line;
                while ((line = pipe.readLine()) != null && !line.contains("\"error\":\"success\"")) {
                    response.append(line);
                }
            }
        } else {
            UnixDomainSocketAddress address = UnixDomainSocketAddress.of(ipcPath);
            try (SocketChannel channel = SocketChannel.open(StandardProtocolFamily.UNIX)) {
                channel.connect(address);
                channel.write(ByteBuffer.wrap((command + "\n").getBytes()));

                ByteBuffer buffer = ByteBuffer.allocate(1024);
                while (channel.read(buffer) > 0) {
                    buffer.flip();
                    response.append(new String(buffer.array(), 0, buffer.limit()));
                    buffer.clear();
                    if (response.toString().contains("\"error\":\"success\""))
                        break;
                }
            }
        }
        return response.toString();
    }

    public void pause() throws IOException {
        sendCommand("{ \"command\": [\"set_property\", \"pause\", true] }");
    }

    public void unpause() throws IOException {
        sendCommand("{ \"command\": [\"set_property\", \"pause\", false] }");
    }

    public void seekTo(int seconds) throws IOException {
        sendCommand("{ \"command\": [\"seek\", " + seconds + ", \"absolute\"] }");
    }

    public void loadStream(String streamUrl) throws IOException {
        sendCommand("{ \"command\": [\"loadfile\", \"" + streamUrl + "\"] }");
    }

    public void quit() throws IOException {
        sendCommand("{ \"command\": [\"quit\"] }");
    }

    public double getCurrentTimestamp() throws IOException {
        String command = "{ \"command\": [\"get_property\", \"time-pos\"] }";
        String response = sendCommandWithResponse(command);
        int dataStart = response.indexOf("\"data\":");
        int dataEnd = response.indexOf(",", dataStart);
        if (dataStart != -1 && dataEnd != -1) {
            String timestampStr = response.substring(dataStart + 7, dataEnd).trim();
            return Double.parseDouble(timestampStr);
        }
        throw new IOException("Failed to get current timestamp");
    }

    public boolean isPlaying() {
        return isPlaying;
    }
}
