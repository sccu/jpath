AbstractTreeSelector
=====

* Finding elements with a path string.
* You can use it with JSON, XML, YAML or built-in Map and List classes.
* Simply overriding two methods - getByName() and getByIndex().
* To get all matched elements, override two more methods - getAllByNamePattern() and getAllByIndexPattern().

Usage:
```java
Gson gson = new GsonBuilder().create();
JsonElement elem = gson.fromJson("{entries: [{ pe.sccu:\"package\" }, {name:\"Bill\", age:26}]}",
        JsonElement.class);
AbstractTreeSelector<JsonElement> aSelector = new AbstractTreeSelector<JsonElement>() {
    @Override
    protected JsonElement getByName(JsonElement element, String key) {
        return element.getAsJsonObject().get(key);
    }

    @Override
    protected JsonElement getByIndex(JsonElement element, int index) {
        return element.getAsJsonArray().get(index);
    }
}
assertEquals("Bill", selector.find(".entries[1].name").getAsString());
assertEquals(26, selector.find(".entries[1].age").getAsInt());

assertNull(aSelector.find(".entry"));
assertNull(aSelector.find(".entries[2]"));
assertNull(aSelector.find(".entries[1].gender"));
```

```java
...
AbstractTreeSelector<JsonElement> aSelector = new AbstractTreeSelector<JsonElement>() {
    ...
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

assertEquals(2, aSelector.findAll(".entries[*].name").size());
assertEquals(1, aSelector.findAll(".entries[*].age").size());
assertEquals(2, aSelector.findAll(".entries[1].*").size());
assertEquals(4, aSelector.findAll(".entries[*].*").size());
```
