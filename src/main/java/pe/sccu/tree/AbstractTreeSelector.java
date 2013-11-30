package pe.sccu.tree;

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

    private static Token getNextToken(String jpath, int start) {
        int next = start;
        int state = 0;
        while (next < jpath.length()) {
            char lookahead = jpath.charAt(next);
            switch (state) {
            case 0:
                if (lookahead == '.') {
                    state = 1;
                } else if (lookahead == '[') {
                    state = 4;
                } else {
                    throw new IllegalArgumentException("A path starts with '.' or '['.");
                }
                break;
            case 1:
                if (Character.isAlphabetic(lookahead)) {
                    state = 2;
                } else {
                    throw new IllegalArgumentException("Invalid character " + lookahead + " at position " + next);
                }
                break;
            case 2:
                if (Character.isAlphabetic(lookahead)) {
                    state = 2;
                } else if (lookahead == '\\') {
                    state = 3;
                } else if (lookahead == '.' || lookahead == '[') {
                    return new Token(Token.Type.OBJECT, jpath.substring(start + 1, next).replace("\\", ""), next);
                } else {
                    throw new IllegalArgumentException("Invalid character " + lookahead + " at position " + next);
                }
                break;
            case 3:
                if (lookahead == '.') {
                    state = 2;
                } else {
                    throw new IllegalArgumentException("Invalid character " + lookahead + " at position " + next);
                }
                break;
            case 4:
                if (lookahead == '0') {
                    state = 5;
                } else if (Character.isDigit(lookahead)) {
                    state = 7;
                } else if (lookahead == '*') {
                    state = 8;
                } else {
                    throw new IllegalArgumentException("Invalid character " + lookahead + " at position " + next);
                }
                break;
            case 5:
                if (lookahead == ']') {
                    state = 6;
                } else {
                    throw new IllegalArgumentException("Invalid character in index:" + lookahead);
                }
                break;
            case 6:
                if (lookahead == '[' || lookahead == '.') {
                    return new Token(Token.Type.ARRAY, jpath.substring(start + 1, next - 1), next);
                } else {
                    throw new IllegalArgumentException("Invalid character " + lookahead + " at position " + next);
                }
            case 7:
                if (lookahead == ']') {
                    state = 6;
                } else if (Character.isDigit(lookahead)) {
                    state = 7;
                } else {
                    throw new IllegalArgumentException("Invalid character " + lookahead + " at position " + next);
                }
                break;
            case 8:
                if (lookahead == ']') {
                    state = 9;
                } else {
                    throw new IllegalArgumentException("Invalid character " + lookahead + " at position " + next);
                }
                break;
            case 9:
                if (lookahead == '[' || lookahead == '.') {
                    return new Token(Token.Type.ARRAY_PATTERN, jpath.substring(start + 1, next - 1), next);
                } else {
                    throw new IllegalArgumentException("Invalid character " + lookahead + " at position " + next);
                }
            }

            next++;
        }

        if (state == 2) {
            return new Token(Token.Type.OBJECT, jpath.substring(start + 1, next).replace("\\", ""), next);
        } else if (state == 6) {
            return new Token(Token.Type.ARRAY, jpath.substring(start + 1, next - 1), next);
        } else if (state == 9) {
            return new Token(Token.Type.ARRAY_PATTERN, jpath.substring(start + 1, next - 1), next);
        } else {
            return new Token(Token.Type.EOP, jpath, 0);
        }
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
        Token t = getNextToken(jpath, endIndex);
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
                List<T> children = getByIndexPattern(element, t.getData());
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
        case EOP:
            return elements;
        default:
            throw new IllegalArgumentException("jpath:" + jpath);
        }
    }

    protected List<T> getByIndexPattern(T element, String indexPattern) {
        throw new UnsupportedOperationException();
    }

    protected abstract T getByName(T element, String key);

    protected abstract T getByIndex(T element, int index);

    private static class Token {
        private final Type type;
        private final String data;
        private final int endIndex;

        public Token(Type type, String data, int endIndex) {
            this.type = type;
            this.data = data;
            this.endIndex = endIndex;
        }

        public String getData() {
            return data;
        }

        public Type getType() {
            return type;
        }

        public int getEndIndex() {
            return endIndex;
        }

        public enum Type {
            ARRAY,
            ARRAY_PATTERN,
            OBJECT,
            OBJECT_PATTERN,
            EOP,
        }
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
