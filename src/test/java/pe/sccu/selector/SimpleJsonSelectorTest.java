package pe.sccu.selector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.json.simple.JSONValue;
import org.junit.Before;
import org.junit.Test;

public class SimpleJsonSelectorTest {

    private TreeNodeSelector selector;

    @Before
    public void before() {
        Object elem = JSONValue
                .parse("{\"entries\": [{ \"pe.sccu\":\"selector\", \"name\":\"Steve\" }, {\"name\":\"Bill\", \"age\":26}]}");
        selector = TreeNodeSelector.create(elem, true);
    }

    @Test
    public void testFind() {
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
        assertEquals("selector", selector.findFirst(".entries[0].pe\\.sccu").toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWhenNotFound() {
        Object elem = JSONValue.parse("{\"entries\": [{ \"pe.sccu\":\"selector\" }, {\"name\":\"Bill\", \"age\":26}]}");
        TreeNodeSelector aTree = TreeNodeSelector.create(elem);
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
