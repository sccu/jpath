package pe.sccu.json;

import com.google.common.base.Preconditions;

public abstract class JsonTree<T> {

    private final boolean nullWhenNotFound;
    protected final T element;

    protected JsonTree(T element, boolean nullWhenNotFound) {
        this.element = element;
        this.nullWhenNotFound = nullWhenNotFound;
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
            }

            next++;
        }

        if (state == 2) {
            return new Token(Token.Type.OBJECT, jpath.substring(start + 1, next).replace("\\", ""), next);
        } else if (state == 6) {
            return new Token(Token.Type.ARRAY, jpath.substring(start + 1, next - 1), next);
        } else {
            return new Token(Token.Type.EOP, jpath, 0);
        }
    }

    public T find(String jpath) {
        try {
            return Preconditions.checkNotNull(find(element, jpath, 0));
        } catch (IndexOutOfBoundsException e) {
            if (nullWhenNotFound) {
                return null;
            } else {
                throw e;
            }
        } catch (NullPointerException e) {
            if (nullWhenNotFound) {
                return null;
            } else {
                throw new IllegalArgumentException("Invalid path:" + jpath);
            }
        }
    }

    private T find(T element, String jpath, int endIndex) {
        Token t = getNextToken(jpath, endIndex);
        switch (t.getType()) {
        case ARRAY:
            return find(getJsonArray(element, Integer.parseInt(t.getData())), jpath, t.getEndIndex());
        case OBJECT:
            return find(getJsonObject(element, t.getData()), jpath, t.getEndIndex());
        case EOP:
            return element;
        default:
            throw new IllegalArgumentException("jpath:" + jpath);
        }
    }

    protected abstract T getJsonObject(T element, String key);

    protected abstract T getJsonArray(T element, int index);

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
            OBJECT, EOP, ARRAY
        }
    }
}
