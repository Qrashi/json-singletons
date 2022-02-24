package link.ignyte.utils.JsonSingleton;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;

public class FileHandler {

    /**
     * Get the default for an object
     *
     * @param type type of class to get default of
     * @param <T>  Type of class
     * @return Object of T
     */
    private static <T extends JsonSerializable> T getNew(Class<T> type) {
        try {
            return type.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new NoDefaultConstructor("Could not instantiate default for " + type.getName());
        }
    }

    /**
     * Load an object from a path
     *
     * @param path   path to load from
     * @param tClass type of class to load
     * @param <T>    type of class
     * @return object of T
     */
    static <T extends JsonSerializable> T load(String path, Class<T> tClass) {
        File to_read = new File(path);
        //Check if file exist
        if (!to_read.exists()) {
            if (!saveDefault(to_read, tClass)) {
                throw new WriteException("Could not save default for " + to_read.getName());
            }
        }

        try {
            JsonReader reader = new JsonReader(new FileReader(to_read));
            T loaded = JsonSingletons.gson.fromJson(reader, tClass);
            if (loaded == null) {
                if (!saveDefault(to_read, tClass)) {
                    throw new WriteException("Could not save default for " + to_read.getName());
                }
                return load(path, tClass);
            }
            return loaded;
        } catch (FileNotFoundException e) {
            throw new NullPointerException("Could not locate file " + to_read.getName() + " even tough created!");
        } catch (JsonSyntaxException | JsonIOException e) {
            System.out.println("Error while loading JSON, malformed JSON:");
            e.printStackTrace();
            System.out.println("Saving default");
            if (!saveDefault(to_read, tClass)) {
                throw new WriteException("Could not save default for " + to_read.getName());
            } else {
                return load(path, tClass);
            }
        }
    }

    /**
     * Create a file and its parent directories
     *
     * @param file file to create
     * @return state of operation (successful or unsuccessful)
     */
    private static boolean createFile(File file) {
        file.getParentFile().mkdirs(); // Output can be ignored, doesn't matter if file already exists
        try {
            return file.createNewFile();
        } catch (IOException e) {
            System.out.println("Could not create file (" + file.getName() + ")");
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Ensure that the specified file and all of its parent directories exist as well as ensuring that no data is lost (previous files will get backed up)
     *
     * @param file The file to check
     * @return State of operation (successful or unsuccessful)
     */
    private static boolean ensureWriteable(File file) {
        if (!file.exists()) {
            // Just create a new file
            return createFile(file);
        } else {
            // Create backup
            try {
                Files.copy(Paths.get(file.getPath()), Paths.get(file.getParentFile().getPath() + removeExtension(file.getName()) + "-backup_at-" + Instant.now().getEpochSecond() + ".json"));
            } catch (IOException e) {
                System.out.println("Could not back up json (" + file.getName() + ")!");
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    /**
     * Remove extension from file (name)
     *
     * @param fileName the file name to remove from.
     * @return file name without extension
     */
    private static String removeExtension(String fileName) {
        final int lastPeriodPos = fileName.lastIndexOf('.');
        if (lastPeriodPos <= 0) return fileName; // No period at all
        else return fileName.substring(0, lastPeriodPos); // Remove everything after the last.
    }

    /**
     * Save the default for a class
     *
     * @param file path to save to
     * @param type type of file to save
     * @return State of operation (success, unsuccessfully)
     */
    private static boolean saveDefault(File file, Class<? extends JsonSerializable> type) {
        if (!ensureWriteable(file)) return false;
        try (Writer writer = new FileWriter(file)) {
            JsonSingletons.gson.toJson(getNew(type), writer);
        } catch (IOException e) {
            System.out.println("Could not write " + file.getName() + "!");
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Save an object to storage
     *
     * @param path   path to save to
     * @param object object to save
     */
    static void save(String path, Object object) {
        File file = new File(path);
        if (!file.exists()) {
            if (!createFile(file)) {
                throw new WriteException("Could not create writable file to save to!");
            }
        }
        try (Writer writer = new FileWriter(path)) {
            JsonSingletons.gson.toJson(object, writer);
        } catch (IOException | JsonIOException e) {
            System.out.println("Could not save " + file.getName());
            e.printStackTrace();
        }
    }

}
