package utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Objects;

public class DataHelper {

    private static final String DATA_FILE = "testdata.json";
    private static JsonObject data;

    static {
        try (Reader reader = new InputStreamReader(
                Objects.requireNonNull(DataHelper.class.getClassLoader().getResourceAsStream(DATA_FILE)))) {
            data = JsonParser.parseReader(reader).getAsJsonObject();
        } catch (Exception e) {
            throw new RuntimeException("Cannot read test data file from classpath: " + DATA_FILE, e);
        }
    }

    public static String get(String key) {
        return getElement(key).getAsString();
    }

    public static int getInt(String key) {
        return getElement(key).getAsInt();
    }

    public static boolean getBoolean(String key) {
        return getElement(key).getAsBoolean();
    }

    private static com.google.gson.JsonElement getElement(String key) {
        String[] keys = key.split("\\.");
        JsonObject current = data;
        for (int i = 0; i < keys.length - 1; i++) {
            current = current.getAsJsonObject(keys[i]);
            if (current == null) {
                throw new RuntimeException("Key not found in json: " + key);
            }
        }
        return current.get(keys[keys.length - 1]);
    }
}
