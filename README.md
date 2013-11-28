jpath
=====

Java JSON path finder like XPath in XML

Usage:
```java
Assert.assertEquals("{entries: [{ pe.sccu:\"jpath\" }, {name:\"Bill\", age:26}]}", jsonElement.toString());

JsonTree tree = JsonTree.create(jsonElement);
JsonElement element = tree.find(".entries[1].name");
JsonElement element2 = tree.find(".entries[0].pe\\.sccu");
```
