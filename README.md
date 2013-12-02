AbstractTreeSelector
=====

Jpath is - like jQuery - a node selector in tree-like structure.

* Finding elements with a path string.
* You can use it with JSON, XML, YAML or built-in Map and List classes.
* Simply overriding two methods - getByName() and getByIndex(), you can use it with any tree-like data object.
* To get all matched elements, override two more methods - getAllMembers() and getAllArrayElements().
* Including test cases for org.json.simple, gson, snake YAML.


Using with org.json.simple:
```java
Object elem = JSONValue
        .parse("{\"entries\": [{ \"pe.sccu\":\"selector\", \"name\":\"Steve\" }, {\"name\":\"Bill\", \"age\":26}]}");
selector = TreeNodeSelector.create(elem, true);

assertEquals("Bill", selector.find(".entries[1].name").getAsString());
assertEquals(26, selector.find(".entries[1].age").getAsInt());

assertEquals(1, selector.findAll(".entries[*].age").size());
assertEquals(2, selector.findAll(".entries[1].*").size());
assertEquals(4, selector.findAll(".entries[*].*").size());
```

Using with Gson:
```java
Gson gson = new GsonBuilder().create();
JsonElement elem = gson.fromJson("{entries: [{ pe.sccu:\"selector\", name:\"Steve\" }, {name:\"Bill\", age:26}]}",
        JsonElement.class);
selector = TreeNodeSelector.create(elem, true, new DefaultNodeAccessor<JsonElement>() {
    @Override
    public JsonElement getByName(JsonElement element, String name) {
        return element.getAsJsonObject().get(name);
    }

    @Override
    public Collection<Map.Entry<String, JsonElement>> getAllMembers(JsonElement element) {
        return element.getAsJsonObject().entrySet();
    }
});

assertEquals("Bill", selector.find(".entries[1].name").getAsString());
assertEquals(26, selector.find(".entries[1].age").getAsInt());

assertEquals(1, selector.findAll(".entries[*].age").size());
assertEquals(2, selector.findAll(".entries[1].*").size());
assertEquals(4, selector.findAll(".entries[*].*").size());
```
