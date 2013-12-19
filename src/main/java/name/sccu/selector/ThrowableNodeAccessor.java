package name.sccu.selector;

import java.util.Collection;
import java.util.Map;

public class ThrowableNodeAccessor<E> implements NodeAccessor<E> {
    private final NodeAccessor<E> nodeAccessor;

    public ThrowableNodeAccessor(NodeAccessor<E> nodeAccessor) {
        this.nodeAccessor = nodeAccessor;
    }

    @Override
    public E getByName(E element, String name) {
        try {
            return nodeAccessor.getByName(element, name);
        } catch (Exception e) {
            throw new NodesNotFoundException("No such member name:" + name);
        }
    }

    @Override
    public E getByIndex(E element, int index) {
        try {
            return nodeAccessor.getByIndex(element, index);
        } catch (Exception e) {
            throw new NodesNotFoundException("Invalid index:" + index);
        }
    }

    @Override
    public Collection<Map.Entry<String, E>> getAllMembers(E element) {
        try {
            return nodeAccessor.getAllMembers(element);
        } catch (Exception e) {
            throw new NodesNotFoundException("Failded to get members of " + element);
        }
    }

    @Override
    public Collection<E> getAllArrayElements(E element) {
        try {
            return nodeAccessor.getAllArrayElements(element);
        } catch (Exception e) {
            throw new NodesNotFoundException("Failded to get members of " + element);
        }
    }
}
