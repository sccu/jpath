package pe.sccu.selector;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

public class GsonTreeSelectorTest {

    private TreeNodeSelector<JsonElement> selector;

    @Before
    public void before() {
        Gson gson = new GsonBuilder().create();
        JsonElement elem = gson.fromJson(
                "{entries: [{ pe.sccu:\"selector\", name:\"Steve\" }, {name:\"Bill\", age:26}]}",
                JsonElement.class);
        selector = TreeNodeSelector.create(elem, true, new GsonNodeAccessor());
    }

    @Test
    public void testFind() {
        assertEquals("Bill", selector.findFirst(".entries[1].name").getAsString());
        assertEquals(26, selector.findFirst(".entries[1].age").getAsInt());
    }

    @Test(expected = NodesNotFoundException.class)
    public void testIOBException() {
        selector.findFirst(".entries[2]");
    }

    @Test(expected = NodesNotFoundException.class)
    public void testInvalidPath() {
        selector.findFirst(".entries[1].gender");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMalformedPath() {
        selector.findFirst(".entries[a].gender");
    }

    @Test
    public void testFindWithKeyIncludingDot() {
        assertEquals("selector", selector.findFirst(".entries[0].pe\\.sccu").getAsString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWhenNotFound() {
        Gson gson = new GsonBuilder().create();
        JsonElement elem = gson.fromJson("{entries: [{ pe.sccu:\"selector\" }, {name:\"Bill\", age:26}]}",
                JsonElement.class);
        TreeNodeSelector<JsonElement> aSelector = TreeNodeSelector.create(elem, new GsonNodeAccessor());
        assertNull(aSelector.findFirst(".entry"));
        assertNull(aSelector.findFirst(".entries[2]"));
        assertNull(aSelector.findFirst(".entries[1].gender"));

        aSelector.findFirst(".entries[a].gender");
        aSelector.findFirst(".entries[*].gender");
    }

    @Test
    public void testFindAll() {
        assertEquals(2, selector.findAll(".entries[*].name").size());
        assertEquals(1, selector.findAll(".entries[*].age").size());
        assertEquals(2, selector.findAll(".entries[1].*").size());
        assertEquals(4, selector.findAll(".entries[*].*").size());
    }

    @Test
    public void testNodesNotFoundException() {
        String eMessage = null;
        try {
            selector.findFirst(".entries[1].gender");
        } catch (NodesNotFoundException e) {
            eMessage = e.getMessage();
        }

        assertNotNull(eMessage);
    }

}
