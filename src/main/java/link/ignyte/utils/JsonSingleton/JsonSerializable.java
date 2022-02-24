package link.ignyte.utils.JsonSingleton;

public abstract class JsonSerializable {

    public static <T extends JsonSerializable> T get(Class<T> type) {
        return JsonSingletons.get(type);
    }

    public static void save(Class<? extends JsonSerializable> type) {
        JsonSingletons.save(type);
    }

    public static void reload(Class<? extends JsonSerializable> type) {JsonSingletons.reload(type);}
}
