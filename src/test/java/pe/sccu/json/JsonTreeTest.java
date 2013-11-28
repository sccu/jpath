package pe.sccu.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import org.junit.rules.ExpectedException;

public class JsonTreeTest {

    private JsonTree tree;

    @Before
    public void before() {
        Gson gson = new GsonBuilder().create();
        JsonElement elem = gson.fromJson("{entries: [{ pe.sccu:\"jujang\" }, {name:\"Bill\", age:26}]}", JsonElement.class);
        tree = JsonTree.create(elem);
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

    @Test
    public void testWrongKey() {
        assertNull(tree.find(".entries[1].gender"));
    }

    @Test
    public void testFindWithKeyIncludingDot() {
        assertEquals("jujang", tree.find(".entries[0].pe\\.sccu").getAsString());
    }
}
