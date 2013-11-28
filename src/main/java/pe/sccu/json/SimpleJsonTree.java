package pe.sccu.json;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class SimpleJsonTree extends JsonTree<Object> {

    SimpleJsonTree(Object element) {
        super(element);
    }

    @Override
    protected Object getJsonObject(Object element, String key) {
        return ((JSONObject) element).get(key);
    }

    @Override
    protected Object getJsonArray(Object element, int index) {
        return ((JSONArray) element).get(index);
    }
}
