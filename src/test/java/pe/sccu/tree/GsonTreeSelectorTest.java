package pe.sccu.tree;

import static org.junit.Assert.*;

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
        JsonElement elem = gson.fromJson("{entries: [{ pe.sccu:\"jujang\" }, {name:\"Bill\", age:26}]}",
                JsonElement.class);
        selector = new GsonTreeSelector(elem, true);
    }

    @Test
    public void testNotNull() {
        assertNotNull(selector.element);
    }

    @Test
    public void testFind() {
        assertEquals("Bill", selector.find(".entries[1].name").getAsString());
        assertEquals(26, selector.find(".entries[1].age").getAsInt());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testIOBException() {
        selector.find(".entries[2]");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidPath() {
        assertNull(selector.find(".entries[1].gender"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMalformedPath() {
        selector.find(".entries[a].gender");
    }

    @Test
    public void testFindWithKeyIncludingDot() {
        assertEquals("jujang", selector.find(".entries[0].pe\\.sccu").getAsString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWhenNotFound() {
        Gson gson = new GsonBuilder().create();
        JsonElement elem = gson.fromJson("{entries: [{ pe.sccu:\"jujang\" }, {name:\"Bill\", age:26}]}",
                JsonElement.class);
        GsonTreeSelector aSelector = new GsonTreeSelector(elem);
        assertNull(aSelector.find(".entry"));
        assertNull(aSelector.find(".entries[2]"));
        assertNull(aSelector.find(".entries[1].gender"));

        aSelector.find(".entries[a].gender");
    }

}
