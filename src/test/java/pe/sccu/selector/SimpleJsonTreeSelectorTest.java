package pe.sccu.selector;

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
        Object elem = JSONValue.parse("{\"entries\": [{ \"pe.sccu\":\"selector\" }, {\"name\":\"Bill\", \"age\":26}]}");
        tree = new SimpleJsonTreeSelector(elem, true);
    }

    @Test
    public void testNotNull() {
        assertNotNull(tree.element);
    }

    @Test
    public void testFind() {
        assertNotNull(tree.element);
        assertEquals("Bill", tree.findFirst(".entries[1].name").toString());
        assertEquals("26", tree.findFirst(".entries[1].age").toString());
    }

    @Test(expected = ElementsNotFoundException.class)
    public void testNotFound() {
        tree.findFirst(".entries[1].gender");
        tree.findFirst(".entries[2]");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMalformedPath() {
        tree.findFirst(".entries[a].gender");
    }

    @Test
    public void testFindWithKeyIncludingDot() {
        assertEquals("selector", tree.findFirst(".entries[0].pe\\.sccu").toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWhenNotFound() {
        Object elem = JSONValue.parse("{\"entries\": [{ \"pe.sccu\":\"selector\" }, {\"name\":\"Bill\", \"age\":26}]}");
        SimpleJsonTreeSelector aTree = new SimpleJsonTreeSelector(elem);
        assertNull(aTree.findFirst(".entry"));
        assertNull(aTree.findFirst(".entries[2]"));
        assertNull(aTree.findFirst(".entries[1].gender"));

        aTree.findFirst(".entries[a].gender");
    }
}
