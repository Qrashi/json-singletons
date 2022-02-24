package link.ignyte.utils.JsonSingleton;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Modifier;
import java.util.HashMap;

public class JsonSingletons {

    private final static HashMap<Class<? extends JsonSerializable>, ? super JsonSerializable> files = new HashMap<>();
    static Gson gson;
    private static boolean initialized = false;
    private static String directory;

    /**
     * Initialize JsonSingletons
     *
     * @param mode how to serialize / deserialize the objects
     */
    public static void initialize(GsonMode mode, String default_directory) {
        files.clear();
        directory = fixPath(default_directory);

        // Instantiate gson
        GsonBuilder builder = new GsonBuilder();
        switch (mode) {
            case ONLY_EXPOSE -> builder.excludeFieldsWithoutExposeAnnotation();
            case ALL_EXCEPT_TRANSIENT -> builder.excludeFieldsWithModifiers(Modifier.TRANSIENT);
        }
        gson = builder.create();

        initialized = true;
    }

    /**
     * Get the object for a class. Should only be called from JsonSerializable
     *
     * @param type Class to load
     * @param <T>  Type of class
     * @return An object for that class
     */
    static <T extends JsonSerializable> T get(Class<T> type) {
        check();
        if (!files.containsKey(type)) {
            load(type);
        }
        return (T) files.get(type);
    }

    /**
     * Load a JsonSerializeable class from storage
     *
     * @param type type of class to load
     */
    private static void load(Class<? extends JsonSerializable> type) {
        String path = getPath(type);
        files.put(type, FileHandler.load(path, type));
    }

    /**
     * Save an Object back to storage
     *
     * @param type type of object to save.
     */
    static void save(Class<? extends JsonSerializable> type) {
        check();
        if (!files.containsKey(type)) {
            return; // Nothing to save :)
        }
        String path = getPath(type);
        FileHandler.save(path, files.get(type));

    }

    /**
     * Save (overwrite) all loaded objects to storage.
     */
    public static void saveAll() {
        check();
        files.keySet().forEach(JsonSingletons::save);
    }

    static void reload(Class<? extends JsonSerializable> type) {
        load(type);
    }

    /**
     * Get the path for a class that extends JsonSerializable
     *
     * @param type Class to get savepath of
     * @return The path to the local json
     */
    protected static String getPath(Class<? extends JsonSerializable> type) {
        if (type.isAnnotationPresent(JsonPath.class)) {
            // Custom path
            return fixPath(directory + "/" + type.getAnnotation(JsonPath.class).value());
        }  // Construct own path
        return directory + "/" + type.getName() + ".json";
    }

    /**
     * Fixes potential double slashes and removes a / from the end of the path
     *
     * @param path The path to fix
     * @return The fixed path
     */
    protected static String fixPath(String path) {
        String finalPath = path;
        finalPath = finalPath.replace(' ', '_');
        finalPath = finalPath.replaceAll("~/+~", "/");
        if (finalPath.charAt(finalPath.length() - 1) == '/') {
            return finalPath.substring(0, finalPath.length() - 1);
        }
        return finalPath;
    }

    /**
     *
     */
    private static void check() {
        if (!initialized) {
            throw new NotInitializedException("JsonSingletons has not been initialized and cannot continue.");
        }
    }

}
