package name.sccu.jpath.decorator;

import java.util.Collection;
import java.util.Map;

import name.sccu.jpath.NodesNotFoundException;
import name.sccu.jpath.Visitor;

public class ThrowableVisitor<E> implements Visitor<E> {
    private final Visitor<E> visitor;

    public ThrowableVisitor(Visitor<E> visitor) {
        this.visitor = visitor;
    }

    @Override
    public E getByName(E element, String name) {
        try {
            return visitor.getByName(element, name);
        } catch (Exception e) {
            throw new NodesNotFoundException("No such member name:" + name);
        }
    }

    @Override
    public E getByIndex(E element, int index) {
        try {
            return visitor.getByIndex(element, index);
        } catch (Exception e) {
            throw new NodesNotFoundException("Invalid index:" + index);
        }
    }

    @Override
    public Collection<Map.Entry<String, E>> getAllMembers(E element) {
        try {
            return visitor.getAllMembers(element);
        } catch (Exception e) {
            throw new NodesNotFoundException("Failded to get members of " + element);
        }
    }

    @Override
    public Collection<E> getAllArrayElements(E element) {
        try {
            return visitor.getAllArrayElements(element);
        } catch (Exception e) {
            throw new NodesNotFoundException("Failded to get members of " + element);
        }
    }
}
