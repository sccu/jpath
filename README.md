jPath
=====

* Java JSON path finder like XPath in XML.
* You can use it with Gson or org.json.simple library.
* You can get it supported new json library by extending JsonTree class and overriding two methods. 

Usage:
```java
JsonElement jsonElement = new GsonBuilder().create().toJsonTree(
    "{entries: [{ pe.sccu:\"jpath\" }, {name:\"Bill\", age:26}]}",
    jsonElement.class);

JsonTree<JsonElement> tree = new JsonTreeBuilder().nullWhenNotFound().create(jsonElement);
JsonElement element = tree.find(".entries[1].name");
Assert.assertNotNull(element);
JsonElement element2 = tree.find(".entries[0].pe\\.sccu");
Assert.assertNotNull(element);
Assert.assertNull(tree.find(".entries[1].gender"));
```
