package fr.univlyon1.tiw1.framework.loader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {

    private static Properties propertiesLoaded;

    public static Properties load() {

        if (propertiesLoaded == null){
            propertiesLoaded = new Properties();
            InputStream inputStream = PropertiesLoader.class.getResourceAsStream("/application.properties");
            try {
                propertiesLoaded.load(inputStream);
            } catch (IOException e) {
            }
        }

        return propertiesLoaded;
    }

}
