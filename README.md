jpath
=====

Java JSON path finder like XPath in XML.
You can use it with Gson or org.json.simple libraries.

Usage:
```java
JsonElement jsonElement = new GsonBuilder().create().toJsonTree(
    "{entries: [{ pe.sccu:\"jpath\" }, {name:\"Bill\", age:26}]}",
    jsonElement.class);

JsonTree tree = JsonTree.create(jsonElement);
JsonElement element = tree.find(".entries[1].name");
JsonElement element2 = tree.find(".entries[0].pe\\.sccu");
```
