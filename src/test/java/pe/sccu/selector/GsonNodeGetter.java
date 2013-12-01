package pe.sccu.selector;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;

public class GsonNodeGetter extends DefaultNodeGetter<JsonElement> {

    @Override
    public JsonElement getByName(JsonElement element, String name) {
        return element.getAsJsonObject().get(name);
    }

    @Override
    public List<JsonElement> getAllByNamePattern(JsonElement element, String namePattern) {
        if (namePattern.equals("*")) {
            List<JsonElement> elements = Lists.newArrayList();
            for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
                elements.add(entry.getValue());
            }
            return elements;
        } else {
            return ImmutableList.of();
        }
    }
}
