package pe.sccu.selector;

import java.util.Collection;
import java.util.Map;

/**
 * Created with IntelliJ IDEA. User: sccu Date: 2013. 12. 7. Time: 7:32 To change this template use File | Settings |
 * File Templates.
 */
public class ExceptionSafeNodeAccessor<E> implements NodeAccessor<E> {
    private final NodeAccessor<E> nodeAccessor;

    public ExceptionSafeNodeAccessor(NodeAccessor<E> nodeAccessor) {
        this.nodeAccessor = nodeAccessor;
    }

    @Override
    public E getByName(E element, String name) {
        try {
            return nodeAccessor.getByName(element, name);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public E getByIndex(E element, int index) {
        try {
            return nodeAccessor.getByIndex(element, index);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Collection<Map.Entry<String, E>> getAllMembers(E element) {
        try {
            return nodeAccessor.getAllMembers(element);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Collection<E> getAllArrayElements(E element) {
        try {
            return nodeAccessor.getAllArrayElements(element);
        } catch (Exception e) {
            return null;
        }
    }
}
