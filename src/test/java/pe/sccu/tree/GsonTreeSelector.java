package pe.sccu.tree;

import com.google.gson.JsonElement;

public class GsonTreeSelector extends AbstractTreeSelector<JsonElement> {

    GsonTreeSelector(JsonElement element) {
        super(element);
    }

    GsonTreeSelector(JsonElement element, boolean throwExceptionWhenNotFound) {
        super(element, throwExceptionWhenNotFound);
    }

    @Override
    protected JsonElement getByName(JsonElement element, String key) {
        return element.getAsJsonObject().get(key);
    }

    @Override
    protected JsonElement getByIndex(JsonElement element, int index) {
        return element.getAsJsonArray().get(index);
    }
}
