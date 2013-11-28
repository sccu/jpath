package pe.sccu.json;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

public class GsonTreeTest {

    private JsonTree<JsonElement> tree;

    @Before
    public void before() {
        Gson gson = new GsonBuilder().create();
        JsonElement elem = gson.fromJson("{entries: [{ pe.sccu:\"jujang\" }, {name:\"Bill\", age:26}]}",
                JsonElement.class);
        tree = new JsonTreeBuilder().create(elem);
    }

    @Test
    public void testNotNull() {
        assertNotNull(tree.element);
    }

    @Test
    public void testFind() {
        assertEquals("Bill", tree.find(".entries[1].name").getAsString());
        assertEquals(26, tree.find(".entries[1].age").getAsInt());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testIOBException() {
        tree.find(".entries[2]");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidPath() {
        assertNull(tree.find(".entries[1].gender"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMalformedPath() {
        tree.find(".entries[a].gender");
    }

    @Test
    public void testFindWithKeyIncludingDot() {
        assertEquals("jujang", tree.find(".entries[0].pe\\.sccu").getAsString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWhenNotFound() {
        Gson gson = new GsonBuilder().create();
        JsonElement elem = gson.fromJson("{entries: [{ pe.sccu:\"jujang\" }, {name:\"Bill\", age:26}]}",
                JsonElement.class);
        JsonTree<JsonElement> aTree = new JsonTreeBuilder().nullWhenNotFound().create(elem);
        assertNull(aTree.find(".entry"));
        assertNull(aTree.find(".entries[2]"));
        assertNull(aTree.find(".entries[1].gender"));

        aTree.find(".entries[a].gender");
    }

}
