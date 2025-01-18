package dev.dubsky.anitogether.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ConfigManager {

    private static final String CONFIG_FILE_PATH = Params.CONFIG;

    private ConfigData config;
    private final ObjectMapper objectMapper;

    public ConfigManager() {
        this.config = new ConfigData();
        this.objectMapper = new ObjectMapper();
    }

    public void loadConfig() throws IOException {
        File configFile = new File(CONFIG_FILE_PATH);
        if (configFile.exists()) {
            config = objectMapper.readValue(configFile, ConfigData.class);
        } else {
            config.setAUTOPLAY(false);
            config.setPORT(Params.BASE_PORT);
            saveConfig();
        }
    }

    public void saveConfig() throws IOException {
        File configFile = new File(CONFIG_FILE_PATH);
        configFile.getParentFile().mkdirs();
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(configFile, config);
    }

    public boolean configFileExists() {
        return Files.exists(Paths.get(CONFIG_FILE_PATH));
    }

    public ConfigData getConfig() {
        return config;
    }

}