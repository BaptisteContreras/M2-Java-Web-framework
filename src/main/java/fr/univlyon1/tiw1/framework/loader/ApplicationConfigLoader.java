package fr.univlyon1.tiw1.framework.loader;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

public class ApplicationConfigLoader {

    private ApplicationConfigLoader() {

    }

    public static ApplicationConfig load(InputStream inputStream) {

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(
                    inputStream,
                    ApplicationConfig.class
            );
        } catch (IOException e) {
            return new ApplicationConfig();
        }

    }

    public static ApplicationConfig load() {
        return load(ApplicationConfigLoader.class.getResourceAsStream("/config-framework.json"));
    }
}
