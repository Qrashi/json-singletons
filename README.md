# Project moved to [GitLab](https://gitlab.com/Qrashi/json-singletons)
This project has been moved to [GitLab](https://gitlab.com/Qrashi/json-singletons)

# The version hosted here is an old and deprecated one
<br><br><br><br><br><br><br><br>
# JsonSingletons

JsonSingletons is a small library that makes it easy to automatically deserialize and serialize various classes **using gson**.
The concept is the following:
* Any class that extends JsonSerializable can be serialized and deserialized.
* There is always only ONE single Object of your type in storage, so you don't have to worry about using wrong (old) data.

Here is how it works:

This is the object that I would like to serialize/deserialize

```java
import link.ignyte.utils.JsonSingleton.JsonPath;
import link.ignyte.utils.JsonSingleton.JsonSerializable;

class Person extends JsonSerializable {
    int age;
    String name;

    public Person() {
        // Initialize default person, REQUIRED
        age = 21;
        name = "Arda";
    }

    public String id() {
        return ("name: " + name + "; age: " + age);
    }
}
```

This is the main class:

```java
import link.ignyte.utils.JsonSingleton.GsonMode;
import link.ignyte.utils.JsonSingleton.JsonSingletons;

class Main {
    public static void main(String[] args) {
        JsonSingletons.initialize(GsonMode.ALL, "data");
        
        Person person = Person.get(Person.class);
        System.out.println(person.id());
    }
}
```

Once ``JsonSerializable.get`` is executed, the required class will get loaded and put into memory.
<br> If you execute ``JsonSerializable.get`` again with the same parameters, the one in memory will get returned.
<br> To save back to the filesystem use ``JsonSerializable.save``.

When using 
```java
JsonSingletons.initialize(GsonMode mode, String path);
```
 the avialible ``GsonModes`` are
```
* ALL (Instructs gson to serialize ALL properties)
* ALL_EXCEPT_TRANSIENT (Instructs gson to serialize ALL properties except for the properties marked with transient)
* EXPOSE_ONLY (Save all properties that have been anotated with @Expose)
```

