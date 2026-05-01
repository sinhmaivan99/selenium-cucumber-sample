package utils;

import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = ConfigManager.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("Sorry, unable to find config.properties");
            }
            properties.load(input);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to load config file", ex);
        }
    }

    public static String get(String key) {
        // Priority 1: System Property (e.g., -Dbrowser=edge)
        String value = System.getProperty(key);
        if (value != null && !value.isEmpty()) {
            return value;
        }
        // Priority 2: config.properties file
        return properties.getProperty(key);
    }

    public static int getInt(String key) {
        String value = get(key);
        return value != null ? Integer.parseInt(value) : 0;
    }

    public static boolean getBoolean(String key) {
        String value = get(key);
        return value != null && Boolean.parseBoolean(value);
    }
}
