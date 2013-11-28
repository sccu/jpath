package pe.sccu.json;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.json.simple.JSONValue;
import org.junit.Before;
import org.junit.Test;

public class SimpleJsonTreeTest {

    private JsonTree<Object> tree;

    @Before
    public void before() {
        Object elem = JSONValue.parse("{\"entries\": [{ \"pe.sccu\":\"jujang\" }, {\"name\":\"Bill\", \"age\":26}]}");
        tree = JsonTree.create(elem);
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

    @Test
    public void testWrongKey() {
        assertNull(tree.find(".entries[1].gender"));
    }

    @Test
    public void testFindWithKeyIncludingDot() {
        assertEquals("jujang", tree.find(".entries[0].pe\\.sccu").toString());
    }
}
