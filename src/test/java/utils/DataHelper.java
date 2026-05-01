package utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Reads test data from a nested JSON file (testdata.json).
 * Supports dot-notation keys like "login.validEmail" to traverse nested objects.
 */
public final class DataHelper {

    private static final String DATA_FILE = "testdata.json";
    private static final JsonObject DATA;

    static {
        try (Reader reader = new InputStreamReader(
                Objects.requireNonNull(
                        DataHelper.class.getClassLoader().getResourceAsStream(DATA_FILE),
                        DATA_FILE + " not found on classpath"),
                StandardCharsets.UTF_8)) {
            DATA = JsonParser.parseReader(reader).getAsJsonObject();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load test data: " + DATA_FILE, e);
        }
    }

    private DataHelper() {
        // Utility class — prevent instantiation
    }

    /** Returns a string value for the given dot-notation key. */
    public static String get(String key) {
        return getElement(key).getAsString();
    }

    /** Returns an int value for the given dot-notation key. */
    public static int getInt(String key) {
        return getElement(key).getAsInt();
    }

    /** Returns a boolean value for the given dot-notation key. */
    public static boolean getBoolean(String key) {
        return getElement(key).getAsBoolean();
    }

    private static JsonElement getElement(String key) {
        String[] segments = key.split("\\.");
        JsonObject current = DATA;

        for (int i = 0; i < segments.length - 1; i++) {
            JsonElement next = current.get(segments[i]);
            if (next == null || !next.isJsonObject()) {
                throw new RuntimeException("Key segment '" + segments[i]
                        + "' not found or not an object in path: " + key);
            }
            current = next.getAsJsonObject();
        }

        String lastKey = segments[segments.length - 1];
        JsonElement result = current.get(lastKey);
        if (result == null) {
            throw new RuntimeException("Key '" + lastKey + "' not found in path: " + key);
        }
        return result;
    }
}
