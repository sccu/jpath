package pe.sccu.selector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

public class SnakeYamlSelectorTest {

    private TreeNodeSelector selector;

    @Before
    public void before() {
        Yaml yaml = new Yaml();
        Map<String, Object> doc = (Map<String, Object>) yaml.load(
                "# yaml 형식\n" +
                        "cosmos:\n" +
                        "    service:\n" +
                        "        name: tmap\n" +
                        "        version: 11\n" +
                        "    role.dev:\n" +
                        "        hq:\n" +
                        "            #host: 1.234.567.89\n" +
                        "            host: localhost"
                );
        selector = TreeNodeSelector.create(doc, true);
    }

    @Test
    public void testFind() {
        assertEquals("tmap", selector.findFirst(".cosmos.service.name").toString());
    }

    @Test(expected = NodesNotFoundException.class)
    public void testNotFound() {
        selector.findFirst(".cosmos.services");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMalformedPath() {
        selector.findFirst(".cosmos..role");
    }

    @Test
    public void testFindWithKeyIncludingDot() {
        assertEquals("localhost", selector.findFirst(".cosmos.role\\.dev.hq.host").toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWhenNotFound() {
        Yaml yaml = new Yaml();
        Map<String, Object> doc = (Map<String, Object>) yaml.load(
                "# yaml 형식\n" +
                        "cosmos:\n" +
                        "    service:\n" +
                        "        name: tmap\n" +
                        "        version: 11\n" +
                        "    role.dev:\n" +
                        "        hq:\n" +
                        "            #host: 1.234.567.89\n" +
                        "            host: localhost"
                );
        TreeNodeSelector aTree = TreeNodeSelector.create(doc);
        assertNull(aTree.findFirst(".entry"));
        assertNull(aTree.findFirst(".entries[2]"));
        assertNull(aTree.findFirst(".entries[1].gender"));

        aTree.findFirst(".entries[a].gender");
    }

    @Test
    public void testFindAll() {
        assertEquals(2, selector.findAll(".cosmos.service.*").size());
        assertEquals(2, selector.findAll(".cosmos.*").size());
        assertEquals(1, selector.findAll(".*").size());
        assertEquals(1, selector.findAll(".cosmos.*.hq").size());
    }
}
