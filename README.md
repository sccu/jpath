jpath
=====

jpath is a node selector for JSON and tree-like data structures.

* You can use jpath for JSON, YAML and built-in Map and List classes. (jpath works with XML, but not much useful.)
* Do not parse data again. jpath use already parsed objects.
* Especially useful for unit testing of JSON or REST APIs. 

##Usage
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
        .parse("{\"entries\": [{ \"name.sccu\":\"selector\", \"name\":\"Steve\" }, {\"name\":\"Bill\", \"age\":26}]}");
selector = Selector.builderOf(elem).create();

assertEquals("Bill", selector.find(".entries[1].name").getAsString());
assertEquals(26, selector.find(".entries[1].age").getAsInt());

assertEquals(1, selector.findAll(".entries[*].age").size());
assertEquals(2, selector.findAll(".entries[1].*").size());
assertEquals(4, selector.findAll(".entries[*].*").size());
```

Using with Gson:
```java
Gson gson = new GsonBuilder().create();
JsonElement elem = gson.fromJson("{entries: [{ name.sccu:\"selector\", name:\"Steve\" }, {name:\"Bill\", age:26}]}",
        JsonElement.class);
selector = Selector
        .builderOf(elem)
        .withVisitor(new DefaultVisitor<JsonElement>() {
            @Override
            public JsonElement getByName(JsonElement element, String name) {
                return element.getAsJsonObject().get(name);
            }
        
            @Override
            public Collection<Map.Entry<String, JsonElement>> getAllMembers(JsonElement element) {
                return element.getAsJsonObject().entrySet();
            }
        })
        .create();

assertEquals("Bill", selector.find(".entries[1].name").getAsString());
assertEquals(26, selector.find(".entries[1].age").getAsInt());

assertEquals(1, selector.findAll(".entries[*].age").size());
assertEquals(2, selector.findAll(".entries[1].*").size());
assertEquals(4, selector.findAll(".entries[*].*").size());
```

##Maven Dependency
jpath is not currently deployed to the maven central. But you can configure in-project repo in the pom.xml.
Refer to [this](https://devcenter.heroku.com/articles/local-maven-dependencies)
