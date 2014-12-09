package name.sccu.jpath;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

public class GsonSelectorTest {

    private Selector<JsonElement> selector;
    private JsonElement elem;

    @Before
    public void before() {
        Gson gson = new GsonBuilder().create();
        elem = gson.fromJson(
                "{entries: [{ name.sccu:\"selector\", name:\"Steve\", \"full_name\":\"Steve Jobs\"}," +
                        "{name:\"Bill\", age:26, \"[elevated.forced_prop]\":true}]}",
                JsonElement.class);
        selector = Selector
                .builderOf(elem)
                .withVisitor(new GsonVisitor())
                .create();
    }

    @Test
    public void testFind() {
        assertEquals(elem.getAsJsonObject().getAsJsonArray("entries").get(1).getAsJsonObject().get("name"),
                selector.findFirst(".entries[1].name"));
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
    public void testFindWithKeysIncludingSpecialCharacters() {
        assertEquals("selector", selector.findFirst(".entries[0].name\\.sccu").getAsString());
        assertEquals("Steve Jobs", selector.findFirst(".entries[0].full_name").getAsString());
        assertTrue(selector.findFirst(".entries[1].\\[elevated\\.forced_prop\\]").getAsBoolean());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWhenNotFound() {
        Gson gson = new GsonBuilder().create();
        JsonElement elem = gson.fromJson("{entries: [{ name.sccu:\"selector\" }, {name:\"Bill\", age:26}]}",
                JsonElement.class);
        Selector<JsonElement> aSelector = Selector
                .builderOf(elem)
                .suppressExceptions()
                .withVisitor(new GsonVisitor())
                .create();
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
        assertEquals(3, selector.findAll(".entries[1].*").size());
        assertEquals(6, selector.findAll(".entries[*].*").size());
    }

    @Test
    public void testNodesNotFoundException() {
        String eMessage = null;
        try {
            selector.findFirst(".entries[1].gender");
            selector.findFirst(".entries[1].gender");
        } catch (NodesNotFoundException e) {
            eMessage = e.getMessage();
        }

        assertNotNull(eMessage);
    }

}
