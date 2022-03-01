package ke.co.skyworld;

public interface AbstractTest {

    String getName();
    double salary();

    default String setName(String name) {
        return name;
    }

}
