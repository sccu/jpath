package name.sccu.jpath;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ GsonSelectorTest.class, SimpleJsonSelectorTest.class, SnakeYamlSelectorTest.class })
public class AllTests {
}
