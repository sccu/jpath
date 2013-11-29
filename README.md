AbstractTreeSelector
=====

* Finding elements - currently only the single element - with the path.
* You can use it with JSON, XML or built-in Map and List classes.
* Simply extending AbstractTreeSelector and overriding two methods - getByName() and getByIndex().

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
