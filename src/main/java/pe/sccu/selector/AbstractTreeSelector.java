package pe.sccu.selector;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public abstract class AbstractTreeSelector<T> {

    protected final T element;
    private final boolean throwExceptionWhenNotFound;

    protected AbstractTreeSelector(T element) {
        this(element, false);
    }

    protected AbstractTreeSelector(T element, boolean throwExceptionWhenNotFound) {
        this.element = element;
        this.throwExceptionWhenNotFound = throwExceptionWhenNotFound;
    }

    public T findFirst(String jpath) {
        try {
            List<T> result = findElements(ImmutableList.of(element), jpath, 0);
            if (result == null || result.isEmpty()) {
                throw new ElementNotFoundException(jpath);
            }
            return result.get(0);
        } catch (IndexOutOfBoundsException e) {
            if (throwExceptionWhenNotFound) {
                throw new ElementNotFoundException(jpath, e);
            } else {
                return null;
            }
        } catch (NullPointerException e) {
            if (throwExceptionWhenNotFound) {
                throw new ElementNotFoundException(jpath);
            } else {
                return null;
            }
        }
    }

    public List<T> findAll(String jpath) {
        try {
            List<T> result = findElements(ImmutableList.of(element), jpath, 0);
            if (result == null) {
                throw new ElementNotFoundException(jpath);
            }
            return result;
        } catch (IndexOutOfBoundsException e) {
            if (throwExceptionWhenNotFound) {
                throw e;
            } else {
                return null;
            }
        } catch (ElementNotFoundException e) {
            if (throwExceptionWhenNotFound) {
                throw e;
            } else {
                return null;
            }
        }
    }

    private List<T> findElements(List<T> elements, String jpath, int endIndex) {
        SelectorToken t = SelectorToken.getNextToken(jpath, endIndex);
        switch (t.getType()) {
        case ARRAY: {
            List<T> candidates = Lists.newArrayList();
            for (T element : elements) {
                T child = getByIndex(element, Integer.parseInt(t.getData()));
                if (child != null) {
                    candidates.add(child);
                }
            }
            return findElements(candidates, jpath, t.getEndIndex());
        }
        case ARRAY_PATTERN: {
            List<T> candidates = Lists.newArrayList();
            for (T element : elements) {
                List<T> children = getAllByIndexPattern(element, t.getData());
                for (T child : children) {
                    if (child != null) {
                        candidates.add(child);
                    }
                }
            }
            return findElements(candidates, jpath, t.getEndIndex());
        }
        case OBJECT: {
            List<T> candidates = Lists.newArrayList();
            for (T element : elements) {
                T child = getByName(element, t.getData());
                if (child != null) {
                    candidates.add(child);
                }
            }
            return findElements(candidates, jpath, t.getEndIndex());
        }
        case OBJECT_PATTERN: {
            List<T> candidates = Lists.newArrayList();
            for (T element : elements) {
                List<T> children = getAllByNamePattern(element, t.getData());
                for (T child : children) {
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

    protected T getByName(T element, String name) {
        throw new UnsupportedOperationException();
    }

    protected T getByIndex(T element, int index) {
        throw new UnsupportedOperationException();
    }

    protected List<T> getAllByIndexPattern(T element, String indexPattern) {
        throw new UnsupportedOperationException();
    }

    protected List<T> getAllByNamePattern(T element, String namePattern) {
        throw new UnsupportedOperationException();
    }

    public static class ElementNotFoundException extends RuntimeException {
        private final String path;

        public ElementNotFoundException(String jpath) {
            this(jpath, null);
        }

        public ElementNotFoundException(String jpath, Throwable e) {
            super(e);
            this.path = jpath;
        }
    }
}
