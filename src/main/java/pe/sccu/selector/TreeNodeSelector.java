package pe.sccu.selector;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

public class TreeNodeSelector<E> {

    private final E element;
    private final boolean throwExceptionWhenNotFound;
    private final NodeAccessor<E> nodeAccessor;

    private TreeNodeSelector(E element, boolean throwExceptionWhenNotFound, NodeAccessor<E> nodeAccessor) {
        this.element = element;
        this.nodeAccessor = nodeAccessor;
        this.throwExceptionWhenNotFound = throwExceptionWhenNotFound;
    }

    public static <E> TreeNodeSelector<E> create(E element) {
        return create(element, false);
    }

    public static <E> TreeNodeSelector<E> create(E element, boolean throwExceptionWhenNotFound) {
        return create(element, throwExceptionWhenNotFound, new DefaultNodeAccessor<E>());
    }

    public static <E> TreeNodeSelector<E> create(E element, NodeAccessor<E> nodeAccessor) {
        return create(element, false, nodeAccessor);
    }

    public static <E> TreeNodeSelector<E> create(E element, boolean throwExceptionWhenNotFound,
            NodeAccessor<E> nodeAccessor) {
        return new TreeNodeSelector<E>(element, throwExceptionWhenNotFound, nodeAccessor);
    }

    public E findFirst(String path) {
        try {
            List<E> result = findElements(Lists.newArrayList(element), path, 0);
            if (result == null || result.isEmpty()) {
                throw new NodesNotFoundException(path);
            }
            return result.get(0);
        } catch (IndexOutOfBoundsException e) {
            if (throwExceptionWhenNotFound) {
                throw new NodesNotFoundException(path, e);
            } else {
                return null;
            }
        } catch (NodesNotFoundException e) {
            if (throwExceptionWhenNotFound) {
                throw new NodesNotFoundException(path);
            } else {
                return null;
            }
        }
    }

    public List<E> findAll(String path) {
        try {
            List<E> result = findElements(Lists.newArrayList(element), path, 0);
            if (result == null || result.isEmpty()) {
                throw new NodesNotFoundException(path);
            }
            return result;
        } catch (IndexOutOfBoundsException e) {
            if (throwExceptionWhenNotFound) {
                throw e;
            } else {
                return null;
            }
        } catch (NodesNotFoundException e) {
            if (throwExceptionWhenNotFound) {
                throw e;
            } else {
                return null;
            }
        }
    }

    private List<E> findElements(List<E> elements, String path, int endIndex) {
        SelectorToken t = SelectorToken.getNextToken(path, endIndex);
        switch (t.getType()) {
        case ARRAY: {
            List<E> candidates = Lists.newArrayList();
            for (E element : elements) {
                E child = nodeAccessor.getByIndex(element, Integer.parseInt(t.getData()));
                if (child != null) {
                    candidates.add(child);
                }
            }
            return findElements(candidates, path, t.getEndIndex());
        }
        case ARRAY_PATTERN: {
            List<E> candidates = getMatchedArrayElements(elements, t.getData());
            return findElements(candidates, path, t.getEndIndex());
        }
        case OBJECT: {
            List<E> candidates = Lists.newArrayList();
            for (E element : elements) {
                E child = nodeAccessor.getByName(element, t.getData());
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

    private List<E> getMatchedArrayElements(List<E> elements, String indexPattern) {
        List<E> candidates = Lists.newArrayList();
        for (E element : elements) {
            List<E> children = nodeAccessor.getAllArrayElements(element);
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

    private List<E> getMatchedMembers(List<E> elements, String data) {
        List<E> candidates = Lists.newArrayList();
        for (E element : elements) {
            Collection<Map.Entry<String, E>> members = nodeAccessor.getAllMembers(element);
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

}
