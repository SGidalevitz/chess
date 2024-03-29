import org.junit.Test;
import static org.junit.Assert.*;


public class PositionTest {
    @Test
    public void testPositionConstructor() {
        // Test that the constructor for Position recognizes invalid positions and throws exceptions for them.
        assertThrows(IllegalArgumentException.class, () -> { Position testPos = new Position(9, 10); });
        assertThrows(IllegalArgumentException.class, () -> { Position testPos = new Position(-4, -13); });
        assertThrows(IllegalArgumentException.class, () -> { Position testPos = new Position(Integer.MAX_VALUE, Integer.MAX_VALUE); });
    }
    @Test
    public void testPositionToChessPosition() {
        // Test that positionToChessPosition works for proper method calls
        assertEquals("a1", Position.positionToChessPosition(new Position(0, 0)));
        assertEquals("h8", Position.positionToChessPosition(new Position(7, 7)));
        assertEquals("g2", Position.positionToChessPosition(new Position(1, 6)));
    }
    @Test
    public void testChessPositionToPosition() {
        // Test that chessPositionToPosition works for proper method calls
        assertEquals(new Position(4, 0), Position.chessPositionToPosition("a5"));
        assertEquals(new Position(6, 2), Position.chessPositionToPosition("c7"));
        assertEquals(new Position(0, 6), Position.chessPositionToPosition("g1"));
        // Test that chessPositionToPosition method throws IllegalArgumentException for improper length
        assertThrows(IllegalArgumentException.class, () -> { Position.chessPositionToPosition("invalid"); });
        // Test that chessPositionToPosition method throws IllegalArgumentException for out-of-bounds row and column
        assertThrows(IllegalArgumentException.class, () -> { Position.chessPositionToPosition("q0"); });
        // Test that chessPositionToPosition method throws IllegalArgumentException for out-of-bounds row
        assertThrows(IllegalArgumentException.class, () -> { Position.chessPositionToPosition("z5"); });
        // Test that chessPositionToPosition method throws IllegalArgumentException for out-of-bounds column
        assertThrows(IllegalArgumentException.class, () -> { Position.chessPositionToPosition("a9"); });
    }

}