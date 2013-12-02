package pe.sccu.selector;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class TreeNodeSelector<E> {

    protected final E element;
    private final boolean throwExceptionWhenNotFound;
    private final NodeAccessor<E> nodeAccessor;

    public TreeNodeSelector(E element) {
        this(element, false);
    }

    public TreeNodeSelector(E element, boolean throwExceptionWhenNotFound) {
        this(element, throwExceptionWhenNotFound, new DefaultNodeAccessor<E>());
    }

    public TreeNodeSelector(E element, NodeAccessor<E> nodeAccessor) {
        this(element, false, nodeAccessor);
    }

    public TreeNodeSelector(E element, boolean throwExceptionWhenNotFound, NodeAccessor<E> nodeAccessor) {
        this.element = element;
        this.nodeAccessor = nodeAccessor;
        this.throwExceptionWhenNotFound = throwExceptionWhenNotFound;
    }

    public E findFirst(String jpath) {
        try {
            List<E> result = findElements(ImmutableList.of(element), jpath, 0);
            if (result == null || result.isEmpty()) {
                throw new ElementsNotFoundException(jpath);
            }
            return result.get(0);
        } catch (IndexOutOfBoundsException e) {
            if (throwExceptionWhenNotFound) {
                throw new ElementsNotFoundException(jpath, e);
            } else {
                return null;
            }
            // } catch (NullPointerException e) {
        } catch (ElementsNotFoundException e) {
            if (throwExceptionWhenNotFound) {
                throw new ElementsNotFoundException(jpath);
            } else {
                return null;
            }
        }
    }

    public List<E> findAll(String jpath) {
        try {
            List<E> result = findElements(ImmutableList.of(element), jpath, 0);
            if (result == null || result.isEmpty()) {
                throw new ElementsNotFoundException(jpath);
            }
            return result;
        } catch (IndexOutOfBoundsException e) {
            if (throwExceptionWhenNotFound) {
                throw e;
            } else {
                return null;
            }
        } catch (ElementsNotFoundException e) {
            if (throwExceptionWhenNotFound) {
                throw e;
            } else {
                return null;
            }
        }
    }

    private List<E> findElements(List<E> elements, String jpath, int endIndex) {
        SelectorToken t = SelectorToken.getNextToken(jpath, endIndex);
        switch (t.getType()) {
        case ARRAY: {
            List<E> candidates = Lists.newArrayList();
            for (E element : elements) {
                E child = nodeAccessor.getByIndex(element, Integer.parseInt(t.getData()));
                if (child != null) {
                    candidates.add(child);
                }
            }
            return findElements(candidates, jpath, t.getEndIndex());
        }
        case ARRAY_PATTERN: {
            List<E> candidates = getMatchedArrayElements(elements, t.getData());
            return findElements(candidates, jpath, t.getEndIndex());
        }
        case OBJECT: {
            List<E> candidates = Lists.newArrayList();
            for (E element : elements) {
                E child = nodeAccessor.getByName(element, t.getData());
                if (child != null) {
                    candidates.add(child);
                }
            }
            return findElements(candidates, jpath, t.getEndIndex());
        }
        case OBJECT_PATTERN: {
            List<E> candidates = getMatchedMembers(elements, t.getData());
            return findElements(candidates, jpath, t.getEndIndex());
        }
        case EOP:
            return elements;
        default:
            throw new IllegalArgumentException("jpath:" + jpath);
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
