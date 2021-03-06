package name.sccu.jpath;

class SelectorToken {
    private final Type type;
    private final String data;
    private final int endIndex;
    public static final String SPECIAL_CHARACTERS = "\\.[]*";

    private SelectorToken(Type type, String data, int endIndex) {
        this.type = type;
        this.data = data;
        this.endIndex = endIndex;
    }

    static SelectorToken getNextToken(String path, int start) {
        int next = start;
        State state = State.S0;
        while (next < path.length()) {
            char lookahead = path.charAt(next);
            switch (state) {
            case S0:
                if (lookahead == '.') {
                    state = State.S1;
                } else if (lookahead == '[') {
                    state = State.S4;
                } else {
                    throw new IllegalArgumentException("A path starts with '.' or '['.");
                }
                break;
            case S1:
                if (SPECIAL_CHARACTERS.indexOf(lookahead) == -1) {
                    state = State.S2;
                } else if (lookahead == '\\') {
                    state = State.S3;
                } else if (lookahead == '*') {
                    state = State.S10;
                } else {
                    throw new IllegalArgumentException("Invalid character " + lookahead + " at position " + next);
                }
                break;
            case S2:
                if (SPECIAL_CHARACTERS.indexOf(lookahead) == -1) {
                    state = State.S2;
                } else if (lookahead == '\\') {
                    state = State.S3;
                } else if (lookahead == '.' || lookahead == '[') {
                    return new SelectorToken(Type.OBJECT, path.substring(start + 1, next).replace("\\", ""), next);
                } else {
                    throw new IllegalArgumentException("Invalid character " + lookahead + " at position " + next);
                }
                break;
            case S3:
                state = State.S2;
                break;
            case S4:
                if (lookahead == '0') {
                    state = State.S5;
                } else if (Character.isDigit(lookahead)) {
                    state = State.S7;
                } else if (lookahead == '*') {
                    state = State.S8;
                } else {
                    throw new IllegalArgumentException("Invalid character " + lookahead + " at position " + next);
                }
                break;
            case S5:
                if (lookahead == ']') {
                    state = State.S6;
                } else {
                    throw new IllegalArgumentException("Invalid character in index:" + lookahead);
                }
                break;
            case S6:
                if (lookahead == '[' || lookahead == '.') {
                    return new SelectorToken(Type.ARRAY, path.substring(start + 1, next - 1), next);
                } else {
                    throw new IllegalArgumentException("Invalid character " + lookahead + " at position " + next);
                }
            case S7:
                if (lookahead == ']') {
                    state = State.S6;
                } else if (Character.isDigit(lookahead)) {
                    state = State.S7;
                } else {
                    throw new IllegalArgumentException("Invalid character " + lookahead + " at position " + next);
                }
                break;
            case S8:
                if (lookahead == ']') {
                    state = State.S9;
                } else {
                    throw new IllegalArgumentException("Invalid character " + lookahead + " at position " + next);
                }
                break;
            case S9:
                if (lookahead == '[' || lookahead == '.') {
                    return new SelectorToken(Type.ARRAY_PATTERN, path.substring(start + 1, next - 1), next);
                } else {
                    throw new IllegalArgumentException("Invalid character " + lookahead + " at position " + next);
                }
            case S10:
                if (lookahead == '[' || lookahead == '.') {
                    return new SelectorToken(Type.OBJECT_PATTERN, path.substring(start + 1, next), next);
                } else {
                    throw new IllegalArgumentException("Invalid character " + lookahead + " at position " + next);
                }
            }

            next++;
        }

        if (state == State.S2) {
            String key = path.substring(start + 1, next)
                    .replace("\\\\", "\\")
                    .replace("\\.", ".")
                    .replace("\\*", "*")
                    .replace("\\[", "[")
                    .replace("\\]", "]");
            return new SelectorToken(Type.OBJECT, key, next);
        } else if (state == State.S6) {
            return new SelectorToken(Type.ARRAY, path.substring(start + 1, next - 1), next);
        } else if (state == State.S9) {
            return new SelectorToken(Type.ARRAY_PATTERN, path.substring(start + 1, next - 1), next);
        } else if (state == State.S10) {
            return new SelectorToken(Type.OBJECT_PATTERN, path.substring(start + 1, next), next);
        } else {
            return new SelectorToken(Type.EOP, path, 0);
        }
    }

    String getData() {
        return data;
    }

    Type getType() {
        return type;
    }

    int getEndIndex() {
        return endIndex;
    }

    enum Type {
        ARRAY,
        ARRAY_PATTERN,
        OBJECT,
        OBJECT_PATTERN,
        EOP,
    }

    private enum State {
        S0,
        S1,
        S2,
        S3,
        S4,
        S5,
        S6,
        S7,
        S8,
        S9,
        S10,
    }
}
