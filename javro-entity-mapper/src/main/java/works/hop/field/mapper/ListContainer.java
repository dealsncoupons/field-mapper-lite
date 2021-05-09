package works.hop.field.mapper;

import java.lang.reflect.Type;
import java.util.LinkedList;

public class ListContainer<T> extends LinkedList<T> {

    public Type genericType;

    public ListContainer(Type genericType) {
        this.genericType = genericType;
    }
}
