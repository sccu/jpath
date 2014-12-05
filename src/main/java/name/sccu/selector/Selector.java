package name.sccu.selector;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

public class Selector<E> {

    private final E element;
    private final Visitor<E> visitor;
    private final boolean suppressExceptions;

    private Selector(E element, boolean suppressExceptions, Visitor<? super E> visitor) {
        Preconditions.checkNotNull(visitor, "visitor is null.");
        this.element = Preconditions.checkNotNull(element, "element is null.");
        this.suppressExceptions = suppressExceptions;
        this.visitor = suppressExceptions ? new ExceptionProofVisitor(visitor) : new ThrowableVisitor(visitor);
    }

    private static <E> Selector<E> create(E element, boolean suppressExceptions, Visitor<? super E> visitor) {
        // noinspection unchecked
        return new Selector(element, suppressExceptions, visitor);
    }

    public E findFirst(String path) {
        @SuppressWarnings("unchecked")
        List<? extends E> candidates = findElements(Lists.newArrayList(element), path, 0);
        if (candidates.isEmpty()) {
            if (suppressExceptions) {
                return null;
            } else {
                throw new NodesNotFoundException(path);
            }
        } else {
            return candidates.get(0);
        }
    }

    public List<? extends E> findAll(String path) {
        @SuppressWarnings("unchecked")
        List<? extends E> candidates = findElements(Lists.newArrayList(element), path, 0);
        if (candidates.isEmpty() && !suppressExceptions) {
            throw new NodesNotFoundException(path);
        }
        return candidates;
    }

    private List<? extends E> findElements(List<? extends E> elements, String path, int endIndex) {
        SelectorToken t = SelectorToken.getNextToken(path, endIndex);
        switch (t.getType()) {
        case ARRAY: {
            List<E> candidates = Lists.newArrayList();
            for (E element : elements) {
                E child = visitor.getByIndex(element, Integer.parseInt(t.getData()));
                if (child != null) {
                    candidates.add(child);
                }
            }
            return findElements(candidates, path, t.getEndIndex());
        }
        case ARRAY_PATTERN: {
            List<? extends E> candidates = getMatchedArrayElements(elements, t.getData());
            return findElements(candidates, path, t.getEndIndex());
        }
        case OBJECT: {
            List<E> candidates = Lists.newArrayList();
            for (E element : elements) {
                E child = visitor.getByName(element, t.getData());
                if (child != null) {
                    candidates.add(child);
                }
            }
            return findElements(candidates, path, t.getEndIndex());
        }
        case OBJECT_PATTERN: {
            List<E> candidates = getMatchedMembers(elements, t.getData());
            return findElements(candidates, path, t.getEndIndex());
        }
        case EOP:
            return elements;
        default:
            throw new IllegalArgumentException("path:" + path);
        }
    }

    private List<? extends E> getMatchedArrayElements(List<? extends E> elements, String indexPattern) {
        List<E> candidates = Lists.newArrayList();
        for (E element : elements) {
            Collection<E> children = visitor.getAllArrayElements(element);
            for (E child : children) {
                if (child != null) {
                    if ("*".equals(indexPattern)) {
                        candidates.add(child);
                    }
                }
            }
        }
        return candidates;
    }

    private List<E> getMatchedMembers(List<? extends E> elements, String data) {
        List<E> candidates = Lists.newArrayList();
        for (E element : elements) {
            Collection<Map.Entry<String, E>> members = visitor.getAllMembers(element);
            for (Map.Entry<String, E> member : members) {
                if (member.getValue() != null) {
                    if ("*".equals(data)) {
                        candidates.add(member.getValue());
                    }
                }
            }
        }
        return candidates;
    }

    public static <E> SelectorBuilder<E> builderOf(E elem) {
        // noinspection unchecked
        return new SelectorBuilder(elem);
    }

    public static class SelectorBuilder<E> {
        private boolean suppressExceptions;
        private Visitor<? super E> visitor;
        private E element;

        public SelectorBuilder(E elem) {
            this.element = elem;
        }

        public SelectorBuilder<E> suppressExceptions() {
            this.suppressExceptions = true;
            return this;
        }

        public SelectorBuilder<E> withVisitor(Visitor<? super E> visitor) {
            this.visitor = visitor;
            return this;
        }

        public Selector<E> create() {
            // noinspection unchecked
            return Selector.create(element, suppressExceptions, visitor == null ? new DefaultVisitor() : visitor);
        }
    }
}
