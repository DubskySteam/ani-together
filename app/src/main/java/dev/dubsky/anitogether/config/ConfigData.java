package dev.dubsky.anitogether.config;

public class ConfigData {
    private int PORT;
    private boolean AUTOPLAY;

    public int getPORT() {
        return PORT;
    }

    public void setPORT(int PORT) {
        this.PORT = PORT;
    }

    public boolean isAUTOPLAY() {
        return AUTOPLAY;
    }

    public void setAUTOPLAY(boolean AUTOPLAY) {
        this.AUTOPLAY = AUTOPLAY;
    }

    public String toString() {
        return "ConfigData{" +
                "port=" + PORT +
                ", autoplay=" + AUTOPLAY +
                '}';
    }
}
