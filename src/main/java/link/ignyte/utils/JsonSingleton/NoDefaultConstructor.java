package link.ignyte.utils.JsonSingleton;

public class NoDefaultConstructor extends RuntimeException {

    NoDefaultConstructor(String s) {
        super(s);
    }

}
