package pe.sccu.tree;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.json.simple.JSONValue;
import org.junit.Before;
import org.junit.Test;

public class SimpleJsonTreeSelectorTest {

    private SimpleJsonTreeSelector tree;

    @Before
    public void before() {
        Object elem = JSONValue.parse("{\"entries\": [{ \"pe.sccu\":\"jujang\" }, {\"name\":\"Bill\", \"age\":26}]}");
        tree = new SimpleJsonTreeSelector(elem, true);
    }

    @Test
    public void testNotNull() {
        assertNotNull(tree.element);
    }

    @Test
    public void testFind() {
        assertNotNull(tree.element);
        assertEquals("Bill", tree.find(".entries[1].name").toString());
        assertEquals("26", tree.find(".entries[1].age").toString());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testIOBException() {
        tree.find(".entries[2]");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWrongKey() {
        tree.find(".entries[1].gender");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMalformedPath() {
        tree.find(".entries[a].gender");
    }

    @Test
    public void testFindWithKeyIncludingDot() {
        assertEquals("jujang", tree.find(".entries[0].pe\\.sccu").toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWhenNotFound() {
        Object elem = JSONValue.parse("{\"entries\": [{ \"pe.sccu\":\"jujang\" }, {\"name\":\"Bill\", \"age\":26}]}");
        SimpleJsonTreeSelector aTree = new SimpleJsonTreeSelector(elem);
        assertNull(aTree.find(".entry"));
        assertNull(aTree.find(".entries[2]"));
        assertNull(aTree.find(".entries[1].gender"));

        aTree.find(".entries[a].gender");
    }
}
