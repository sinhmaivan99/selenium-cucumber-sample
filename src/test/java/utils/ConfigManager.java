package utils;

import java.io.InputStream;
import java.util.Properties;

/**
 * Reads configuration from config.properties with system property override support.
 * System properties (e.g. -Dbrowser=edge) take priority over file values,
 * enabling CI/CD pipeline customization without code changes.
 */
public final class ConfigManager {

    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream input = ConfigManager.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("config.properties not found on classpath");
            }
            PROPERTIES.load(input);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to load config.properties", ex);
        }
    }

    private ConfigManager() {
        // Utility class — prevent instantiation
    }

    /**
     * Returns a config value. System property takes priority over file property.
     */
    public static String get(String key) {
        String systemValue = System.getProperty(key);
        if (systemValue != null && !systemValue.isEmpty()) {
            return systemValue;
        }
        return PROPERTIES.getProperty(key);
    }

    /**
     * Returns a config value or a default if the key is not found.
     */
    public static String getOrDefault(String key, String defaultValue) {
        String value = get(key);
        return value != null ? value : defaultValue;
    }

    public static int getInt(String key) {
        String value = get(key);
        return value != null ? Integer.parseInt(value.trim()) : 0;
    }

    public static int getInt(String key, int defaultValue) {
        String value = get(key);
        if (value == null || value.isBlank()) return defaultValue;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static boolean getBoolean(String key) {
        String value = get(key);
        return value != null && Boolean.parseBoolean(value.trim());
    }
}
