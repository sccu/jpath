package pe.sccu.tree;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

public class GsonTreeSelectorTest {

    private GsonTreeSelector selector;

    @Before
    public void before() {
        Gson gson = new GsonBuilder().create();
        JsonElement elem = gson.fromJson(
                "{entries: [{ pe.sccu:\"jujang\", name:\"Steve\" }, {name:\"Bill\", age:26}]}",
                JsonElement.class);
        selector = new GsonTreeSelector(elem, true);
    }

    @Test
    public void testNotNull() {
        assertNotNull(selector.element);
    }

    @Test
    public void testFind() {
        assertEquals("Bill", selector.findFirst(".entries[1].name").getAsString());
        assertEquals(26, selector.findFirst(".entries[1].age").getAsInt());
    }

    @Test(expected = AbstractTreeSelector.ElementNotFoundException.class)
    public void testIOBException() {
        selector.findFirst(".entries[2]");
    }

    @Test(expected = AbstractTreeSelector.ElementNotFoundException.class)
    public void testInvalidPath() {
        assertNull(selector.findFirst(".entries[1].gender"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMalformedPath() {
        selector.findFirst(".entries[a].gender");
    }

    @Test
    public void testFindWithKeyIncludingDot() {
        assertEquals("jujang", selector.findFirst(".entries[0].pe\\.sccu").getAsString());
    }

    @Test(expected = AbstractTreeSelector.ElementNotFoundException.class)
    public void testWhenNotFound() {
        Gson gson = new GsonBuilder().create();
        JsonElement elem = gson.fromJson("{entries: [{ pe.sccu:\"jujang\" }, {name:\"Bill\", age:26}]}",
                JsonElement.class);
        GsonTreeSelector aSelector = new GsonTreeSelector(elem);
        assertNull(aSelector.findFirst(".entry"));
        assertNull(aSelector.findFirst(".entries[2]"));
        assertNull(aSelector.findFirst(".entries[1].gender"));

        aSelector.findFirst(".entries[a].gender");
    }

    @Test
    public void testFindAll() {
        List<JsonElement> elems = selector.findAll(".entries[*].name");
        assertEquals(2, elems.size());

        elems = selector.findAll(".entries[*].age");
        assertEquals(1, elems.size());
    }

}
