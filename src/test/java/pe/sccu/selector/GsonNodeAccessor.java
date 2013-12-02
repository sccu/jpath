package pe.sccu.selector;

import java.util.Collection;
import java.util.Map;

import com.google.gson.JsonElement;

public class GsonNodeAccessor extends DefaultNodeAccessor<JsonElement> {

    @Override
    public JsonElement getByName(JsonElement element, String name) {
        return element.getAsJsonObject().get(name);
    }

    @Override
    public Collection<Map.Entry<String, JsonElement>> getAllMembers(JsonElement element) {
        return element.getAsJsonObject().entrySet();
    }
}
