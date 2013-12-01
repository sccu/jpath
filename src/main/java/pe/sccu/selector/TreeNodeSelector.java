package pe.sccu.selector;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class TreeNodeSelector<E> {

    protected final E element;
    private final boolean throwExceptionWhenNotFound;
    private final NodeGetter<E> nodeGetter;

    public TreeNodeSelector(E element) {
        this(element, false);
    }

    public TreeNodeSelector(E element, boolean throwExceptionWhenNotFound) {
        this(element, throwExceptionWhenNotFound, new DefaultNodeGetter<E>());
    }

    public TreeNodeSelector(E element, NodeGetter<E> nodeGetter) {
        this(element, false, nodeGetter);
    }

    public TreeNodeSelector(E element, boolean throwExceptionWhenNotFound, NodeGetter<E> nodeGetter) {
        this.element = element;
        this.nodeGetter = nodeGetter;
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
                E child = nodeGetter.getByIndex(element, Integer.parseInt(t.getData()));
                if (child != null) {
                    candidates.add(child);
                }
            }
            return findElements(candidates, jpath, t.getEndIndex());
        }
        case ARRAY_PATTERN: {
            List<E> candidates = Lists.newArrayList();
            for (E element : elements) {
                List<E> children = nodeGetter.getAllByIndexPattern(element, t.getData());
                for (E child : children) {
                    if (child != null) {
                        candidates.add(child);
                    }
                }
            }
            return findElements(candidates, jpath, t.getEndIndex());
        }
        case OBJECT: {
            List<E> candidates = Lists.newArrayList();
            for (E element : elements) {
                E child = nodeGetter.getByName(element, t.getData());
                if (child != null) {
                    candidates.add(child);
                }
            }
            return findElements(candidates, jpath, t.getEndIndex());
        }
        case OBJECT_PATTERN: {
            List<E> candidates = Lists.newArrayList();
            for (E element : elements) {
                List<E> children = nodeGetter.getAllByNamePattern(element, t.getData());
                for (E child : children) {
                    if (child != null) {
                        candidates.add(child);
                    }
                }
            }
            return findElements(candidates, jpath, t.getEndIndex());
        }
        case EOP:
            return elements;
        default:
            throw new IllegalArgumentException("jpath:" + jpath);
        }
    }

}
