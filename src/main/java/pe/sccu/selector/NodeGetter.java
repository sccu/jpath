package pe.sccu.selector;

import java.util.List;

public interface NodeGetter<T> {
    T getByName(T element, String name);

    T getByIndex(T element, int index);

    List<T> getAllByIndexPattern(T element, String indexPattern);

    List<T> getAllByNamePattern(T element, String namePattern);
}
