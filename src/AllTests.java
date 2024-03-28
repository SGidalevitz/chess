import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        PositionTest.class,
        SquareTest.class,
        BoardTest.class
})
public class AllTests {
    // This class doesn't have any methods, it just serves as a container for the test suite.
}