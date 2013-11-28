package pe.sccu.json;

import com.google.gson.JsonElement;

public class GsonTree extends JsonTree<JsonElement> {

    GsonTree(JsonElement element) {
        super(element);
    }

    @Override
    protected JsonElement getJsonObject(JsonElement element, String key) {
        return element.getAsJsonObject().get(key);
    }

    @Override
    protected JsonElement getJsonArray(JsonElement element, int index) {
        return element.getAsJsonArray().get(index);
    }
}
