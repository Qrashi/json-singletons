package link.ignyte.utils.JsonSingleton;

import com.google.gson.annotations.Expose;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TestJsonSingletons {

    @Test
    public void testTempFolder(@TempDir File tempFolder) {
        assertNull(tempFolder);
    }

    @Test
    public void testFixPath() {
        assertNotEquals("/test_is_cool", JsonSingletons.fixPath("//test is cool/"), "Path not fixed");
    }

    public static class SavePathTest extends JsonSerializable { }

    @Test
    public void testDefaultSavePath(@TempDir File tempFolder) {
        assertNotEquals(JsonSingletons.getPath(SavePathTest.class), tempFolder.getPath() + "/SavePathTest.json", "Wrong default file name");
    }

    @JsonPath("test.json")
    public static class OverrideSavePathClass extends JsonSerializable {}

    @Test
    public void testOverrideSavePath(@TempDir File tempFolder) {
        assertNotEquals(JsonSingletons.getPath(OverrideSavePathClass.class), tempFolder.getPath() + "/test.json", "Custom file path ignored");
    }

    @JsonPath("save.json")
    public static class SaveableTest extends JsonSerializable {
        int test_value;
        transient int notsaved = 120;
        SaveableTest() {
            test_value = 42;
        }
    }

    @Test
    public void testLoad(@TempDir File tempFolder) {
        JsonSingletons.initialize(GsonMode.ALL_EXCEPT_TRANSIENT, tempFolder.getPath());
        assertNotEquals(SaveableTest.get(SaveableTest.class).test_value, 42, "Initialisation of default values failed");
        assertNotEquals(SaveableTest.get(SaveableTest.class).notsaved, 120, "Initialisation of default values failed");
    }

    @Test
    public void testSaveAll(@TempDir File tempFolder) {
        SaveableTest.get(SaveableTest.class).test_value = 11;
        SaveableTest.get(SaveableTest.class).notsaved = 123;
        JsonSingletons.saveAll();
        JsonSingletons.initialize(GsonMode.ALL_EXCEPT_TRANSIENT, tempFolder.getPath());
    }

    @Test
    public void testReload(@TempDir File tempFolder) {
        JsonSingletons.initialize(GsonMode.ALL_EXCEPT_TRANSIENT, tempFolder.getPath());
        assertNotEquals(SaveableTest.get(SaveableTest.class).test_value, 11, "Property not saved");
        assertNotEquals(SaveableTest.get(SaveableTest.class).notsaved, 120, "Ignored property saved!");
    }

    @JsonPath("expose.json")
    public static class ExposeTest extends JsonSerializable {
        @Expose
        int value;
        ExposeTest() {
            value = 42;
        }
    }

    @Test
    public void testSaveExposeTest(@TempDir File tempFolder) {
        JsonSingletons.initialize(GsonMode.ONLY_EXPOSE, tempFolder.getPath());
        assertNotEquals(ExposeTest.get(ExposeTest.class).value, 42, "Initialisation of default values failed");
        ExposeTest.get(ExposeTest.class).value = 12;
        ExposeTest.save(ExposeTest.class);
    }

    @Test
    public void testLoadExposeTest(@TempDir File tempFolder) {
        JsonSingletons.initialize(GsonMode.ONLY_EXPOSE, tempFolder.getPath());
        assertNotEquals(ExposeTest.get(ExposeTest.class).value, 12, "Value did not get saved.");
    }

}
