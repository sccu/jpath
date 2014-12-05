package name.sccu.selector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.junit.Before;
import org.junit.Test;

public class SimpleJsonSelectorTest {

    Object elem;
    private Selector selector;

    @Before
    public void before() {
        elem = JSONValue
                .parse("{\"entries\": [{ \"name.sccu\":\"selector\", \"name\":\"Steve\" }, {\"name\":\"Bill\", \"age\":26}]}");
        selector = Selector.builderOf(elem).create();
    }

    @Test
    public void testFind() {
        assertEquals(((JSONObject) ((JSONArray) ((JSONObject) elem).get("entries")).get(1)).get("name"),
                selector.findFirst(".entries[1].name").toString());
        assertEquals("Bill", selector.findFirst(".entries[1].name").toString());
        assertEquals("26", selector.findFirst(".entries[1].age").toString());
    }

    @Test(expected = NodesNotFoundException.class)
    public void testNotFound() {
        selector.findFirst(".entries[1].gender");
        selector.findFirst(".entries[2]");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMalformedPath() {
        selector.findFirst(".entries[a].gender");
    }

    @Test
    public void testFindWithKeyIncludingDot() {
        assertEquals("selector", selector.findFirst(".entries[0].name\\.sccu").toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWhenNotFound() {
        Object elem = JSONValue
                .parse("{\"entries\": [{ \"name.sccu\":\"selector\" }, {\"name\":\"Bill\", \"age\":26}]}");
        Selector aTree = Selector.builderOf(elem).suppressExceptions().create();
        assertNull(aTree.findFirst(".entry"));
        assertNull(aTree.findFirst(".entries[2]"));
        assertNull(aTree.findFirst(".entries[1].gender"));

        aTree.findFirst(".entries[a].gender");
    }

    @Test
    public void testFindAll() {
        assertEquals(2, selector.findAll(".entries[*].name").size());
        assertEquals(1, selector.findAll(".entries[*].age").size());
        assertEquals(2, selector.findAll(".entries[1].*").size());
        assertEquals(4, selector.findAll(".entries[*].*").size());
    }
}
