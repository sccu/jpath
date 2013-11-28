package pe.sccu.json;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.google.gson.JsonElement;

public class JsonTreeBuilder {

    private boolean nullWhenNotFound = false;

    public JsonTree<JsonElement> create(JsonElement element) {
        return new GsonTree(element, nullWhenNotFound);
    }

    public JsonTree<Object> create(Object element) {
        if ((element instanceof JSONValue) || (element instanceof JSONObject) || (element instanceof JSONArray)) {
            return new SimpleJsonTree(element, nullWhenNotFound);
        }
        throw new IllegalArgumentException("'element' must be one of JSONValue, JSONObject or JSONArray.");
    }

    public JsonTreeBuilder nullWhenNotFound() {
        this.nullWhenNotFound = true;
        return this;
    }
}
