package pe.sccu.selector;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;

public class GsonTreeSelector extends AbstractTreeSelector<JsonElement> {

    GsonTreeSelector(JsonElement element) {
        super(element);
    }

    GsonTreeSelector(JsonElement element, boolean throwExceptionWhenNotFound) {
        super(element, throwExceptionWhenNotFound);
    }

    @Override
    protected JsonElement getByName(JsonElement element, String name) {
        return element.getAsJsonObject().get(name);
    }

    @Override
    protected JsonElement getByIndex(JsonElement element, int index) {
        return element.getAsJsonArray().get(index);
    }

    @Override
    protected List<JsonElement> getAllByIndexPattern(JsonElement element, String indexPattern) {
        if (indexPattern.equals("*")) {
            return ImmutableList.copyOf(element.getAsJsonArray());
        } else {
            return ImmutableList.of();
        }
    }

    @Override
    protected List<JsonElement> getAllByNamePattern(JsonElement element, String namePattern) {
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
