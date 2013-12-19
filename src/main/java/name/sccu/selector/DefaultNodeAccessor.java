package name.sccu.selector;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

public class DefaultNodeAccessor<E> implements NodeAccessor<E> {
    @Override
    public E getByName(E element, String name) {
        if (element instanceof Map) {
            return ((Map<?, E>) element).get(name);
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
    public List<E> getAllArrayElements(E element) {
        if (element instanceof Iterable) {
            return Lists.newArrayList(((Iterable) element).iterator());
        }

        throw new IllegalArgumentException("Unsupported type:" + element.getClass().getCanonicalName());
    }

    @Override
    public Collection<Map.Entry<String, E>> getAllMembers(E element) {
        if (element instanceof Map) {
            return ((Map<String, E>) element).entrySet();
        }

        throw new IllegalArgumentException("Unsupported type:" + element.getClass().getCanonicalName());
    }
}
