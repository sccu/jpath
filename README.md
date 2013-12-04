jPath
=====

jPath is a node selector in json object.

* You can use it on JSON, YAML or built-in Map and List classes. (With XML, it works but not much useful.)
* Do not parse data again. It works with parsed tree objects.

```java
// BEFORE
((JSONObject)((JSONArray)((JSONObject) elem).get("entries")).get(1)).get("name");   // for org.json.simple
elem.getAsJsonObject().getAsJsonArray("entries").get(1).getAsJsonObject().get("name");  // for gson

// AFTER
selector.findFirst(".entries[1].name");
```

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
