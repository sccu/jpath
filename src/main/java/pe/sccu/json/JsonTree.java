package pe.sccu.json;

import java.util.regex.Pattern;

import com.google.gson.JsonElement;

public class JsonTree {

    private final Pattern ARRAY_PATTERN = Pattern.compile("^\\[(\\d)\\]");
    private final JsonElement element;

    public JsonTree(JsonElement element) {
        this.element = element;
    }

    public static JsonTree create(JsonElement element) {
        return new JsonTree(element);
    }

    public JsonElement find(String jpath) {
        return find(element, jpath, 0);
    }

    public static JsonElement find(JsonElement element, String jpath, int endIndex) {
        Token t = getNextToken(jpath, endIndex);
        switch (t.getType()) {
            case ARRAY:
                return find(element.getAsJsonArray().get(Integer.parseInt(t.getData())), jpath, t.getEndIndex());
            case OBJECT:
                return find(element.getAsJsonObject().get(t.getData()), jpath, t.getEndIndex());
            case EOP:
                return element;
            default:
                throw new IllegalArgumentException("jpath:" + jpath);
        }
    }

    /**
    private Token getNextToken(String jpath) {
        if ("".equals(jpath)) {
            return new Token(Token.Type.EOP, "", 0);
        } else if (jpath.startsWith(".")) {
            int end = jpath.indexOf(".", 1) == -1 ? jpath.length() : jpath.indexOf(".", 1);
            int bracketIndex = jpath.indexOf("[");
            if (bracketIndex != -1 && bracketIndex < end) {
                end = bracketIndex;
            }
            return new Token(Token.Type.OBJECT, jpath.substring(1, end), end);
        }
        Matcher m = ARRAY_PATTERN.matcher(jpath);
        if (m.find()) {
            return new Token(Token.Type.ARRAY, m.group(1), m.group(0).length());
        } else {
            throw new IllegalArgumentException("jpath:" + jpath);
        }
    }
     */

    private static Token getNextToken(String jpath, int start) {
        int next = start;
        int state = 0;
        while (next < jpath.length()) {
            char lookahead = jpath.charAt(next);
            switch(state) {
                case 0:
                    if (lookahead == '.') {
                        state = 1;
                    } else if (lookahead == '[') {
                        state = 4;
                    } else {
                        throw new IllegalArgumentException("Path starts with '.' or '['.");
                    }
                    break;
                case 1:
                    if (Character.isAlphabetic(lookahead)) {
                        state = 2;
                    } else {
                        throw new IllegalArgumentException("Invalid key character:" + lookahead);
                    }
                    break;
                case 2:
                    if (Character.isAlphabetic(lookahead)) {
                        state = 2;
                    } else if (lookahead == '\\') {
                        state = 3;
                    } else if (lookahead == '.' || lookahead == '[') {
                        return new Token(Token.Type.OBJECT, jpath.substring(start+1, next).replace("\\", ""), next);
                    } else {
                        throw new IllegalArgumentException("Invalid key character:" + lookahead);
                    }
                    break;
                case 3:
                    if (lookahead == '.') {
                        state = 2;
                    } else {
                        throw new IllegalArgumentException("Invalid key character:" + lookahead);
                    }
                    break;
                case 4:
                    if (lookahead == '0') {
                        state = 5;
                    } else if (Character.isDigit(lookahead)) {
                        state = 7;
                    } else {
                        throw new IllegalArgumentException("Invalid key character:" + lookahead);
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
                        throw new IllegalArgumentException("Invalid key character:" + lookahead);
                    }
                    break;
            }

            next++;
        }

        if (state == 2) {
            return new Token(Token.Type.OBJECT, jpath.substring(start+1, next).replace("\\", ""), next);
        } else if (state == 6) {
            return new Token(Token.Type.ARRAY, jpath.substring(start+1, next-1), next);
        } else {
        return new Token(Token.Type.EOP, jpath, 0);
        }
    }

    private static class Token {
        private final Type type;
        private final String data;
        private final int usedBytes;

        public Token(Type type, String data, int usedBytes) {
            this.type = type;
            this.data = data;
            this.usedBytes = usedBytes;
        }

        public String getData() {
            return data;
        }

        public Type getType() {
            return type;
        }

        public int getEndIndex() {
            return usedBytes;
        }

        public enum Type {
            OBJECT, EOP, ARRAY
        };
    }
}
