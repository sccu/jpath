package name.sccu.jpath;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.common.collect.Lists;

public class XmlSelectorTest {

    private Selector<? extends Node> selector;

    @Before
    public void before() throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new InputSource(new StringReader(
                "<doc>"
                        + "<entries><name.sccu>selector</name.sccu><name>Steve</name></entries>"
                        + "<entries><name>Bill</name><age>26</age></entries>"
                        + "</doc>")));

        DefaultVisitor<Node> visitor = new DefaultVisitor<Node>() {
            @Override
            public Node getByIndex(Node element, int index) {
                return element.getChildNodes().item(index);
            }

            @Override
            public Node getByName(Node element, String name) {
                NodeList nodes = element.getChildNodes();
                for (int i = 0; i < nodes.getLength(); i++) {
                    if (name.equals(nodes.item(i).getNodeName())) {
                        return nodes.item(i);
                    }
                }
                return null;
            }

            @Override
            public Collection<Map.Entry<String, Node>> getAllMembers(Node element) {
                NodeList nodes = element.getChildNodes();
                ArrayList<Map.Entry<String, Node>> list = Lists.newArrayList();
                for (int i = 0; i < nodes.getLength(); i++) {
                    final Node node = nodes.item(i);
                    list.add(new Map.Entry<String, Node>() {

                        @Override
                        public String getKey() {
                            return node.getNodeName();
                        }

                        @Override
                        public Node getValue() {
                            return node;
                        }

                        @Override
                        public Node setValue(Node value) {
                            throw new UnsupportedOperationException();
                        }
                    });
                }
                return list;
            }

            @Override
            public List<Node> getAllArrayElements(Node element) {
                List<Node> list = Lists.newArrayList();
                NodeList nodes = element.getChildNodes();
                for (int i = 0; i < nodes.getLength(); i++) {
                    list.add(nodes.item(i));
                }
                return list;
            }
        };
        selector = Selector.builderOf(document).withVisitor(visitor).create();
    }

    @Test
    public void testFind() {
        assertEquals("Bill", selector.findFirst(".doc[1].name").getTextContent());
        assertEquals("Steve", selector.findFirst(".doc.entries.name").getTextContent());
        assertEquals("26", selector.findFirst(".doc[1].age").getTextContent());
    }

    @Test(expected = NodesNotFoundException.class)
    public void testNotFound() {
        selector.findFirst(".doc[1].gender");
        selector.findFirst(".doc[2]");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMalformedPath() {
        selector.findFirst(".doc[a].gender");
    }

    @Test
    public void testFindWithKeyIncludingDot() {
        assertEquals("selector", selector.findFirst(".doc[0].name\\.sccu").getTextContent());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWhenNotFound() throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new InputSource(new StringReader(
                "<doc>"
                        + "<entries><name.sccu>selector</name.sccu><name>Steve</name></entries>"
                        + "<entries><name>Bill</name><age>26</age></entries>"
                        + "</doc>")));
        Selector aTree = Selector.builderOf(document).suppressExceptions().create();
        assertNull(aTree.findFirst(".docs"));
        assertNull(aTree.findFirst(".doc[2]"));
        assertNull(aTree.findFirst(".doc[1].gender"));

        aTree.findFirst(".entries[a].gender");
    }

    @Test
    public void testFindAll() {
        assertEquals(2, selector.findAll(".doc[1].*").size());
        assertEquals(4, selector.findAll(".doc[*].*").size());
        assertEquals(2, selector.findAll(".doc[*].name").size());
        assertEquals(1, selector.findAll(".doc[*].age").size());
    }
}
