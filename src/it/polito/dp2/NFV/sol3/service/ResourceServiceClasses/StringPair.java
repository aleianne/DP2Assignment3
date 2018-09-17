package it.polito.dp2.NFV.sol3.service.ResourceServiceClasses;


import java.util.Objects;

// this class is used to create an object with two keys for the hashmap lookup
public class StringPair {

    private String key1;
    private String key2;

    public StringPair(String key1, String key2) {
        this.key1 = key1;
        this.key2 = key2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StringPair that = (StringPair) o;
        return Objects.equals(key1, that.key1) &&
                Objects.equals(key2, that.key2);
    }

    @Override
    public int hashCode() {

        return Objects.hash(key1, key2);
    }
}
