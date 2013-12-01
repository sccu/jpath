package pe.sccu.selector;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;

public class DefaultNodeGetter<E> implements NodeGetter<E> {
    @Override
    public E getByName(E element, String name) {
        if (element instanceof Map) {
            return ((Map<String, E>) element).get(name);
        }

        throw new IllegalArgumentException("Unsupported type:" + element.getClass().getCanonicalName());
    }

    @Override
    public E getByIndex(E element, int index) {
        if (element instanceof List) {
            return ((List<E>) element).get(index);
        }

        if (element instanceof Iterable) {
            int count = 0;
            for (E child : (Iterable<E>) element) {
                if (count++ == index) {
                    return child;
                }
            }
            throw new IndexOutOfBoundsException("size:" + count + ", index:" + index);
        }

        throw new IllegalArgumentException("Unsupported type:" + element.getClass().getCanonicalName());
    }

    @Override
    public List<E> getAllByIndexPattern(E element, String indexPattern) {
        if (indexPattern.equals("*")) {
            if (element instanceof Iterable) {
                return ImmutableList.copyOf(((Iterable) element).iterator());
            }
        } else {
            throw new IllegalArgumentException("Unsupported index pattern:" + indexPattern);
        }

        throw new IllegalArgumentException("Unsupported type:" + element.getClass().getCanonicalName());
    }

    @Override
    public List<E> getAllByNamePattern(E element, String namePattern) {
        if (namePattern.equals("*")) {
            if (element instanceof Map) {
                return ImmutableList.copyOf(((Map<Object, E>) element).values());
            }
        } else {
            throw new IllegalArgumentException("Unsupported name pattern:" + namePattern);
        }

        throw new IllegalArgumentException("Unsupported type:" + element.getClass().getCanonicalName());
    }
}
