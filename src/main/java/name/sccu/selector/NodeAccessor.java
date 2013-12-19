package name.sccu.selector;

import java.util.Collection;
import java.util.Map;

public interface NodeAccessor<E> {
    E getByName(E element, String name);

    E getByIndex(E element, int index);

    Collection<Map.Entry<String, E>> getAllMembers(E element);

    Collection<E> getAllArrayElements(E element);
}
