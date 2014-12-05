package name.sccu.selector;

import java.util.Collection;
import java.util.Map;

public class ExceptionProofVisitor<E> implements Visitor<E> {
    private final Visitor<E> visitor;

    public ExceptionProofVisitor(Visitor<E> visitor) {
        this.visitor = visitor;
    }

    @Override
    public E getByName(E element, String name) {
        try {
            return visitor.getByName(element, name);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public E getByIndex(E element, int index) {
        try {
            return visitor.getByIndex(element, index);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Collection<Map.Entry<String, E>> getAllMembers(E element) {
        try {
            return visitor.getAllMembers(element);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Collection<E> getAllArrayElements(E element) {
        try {
            return visitor.getAllArrayElements(element);
        } catch (Exception e) {
            return null;
        }
    }
}
