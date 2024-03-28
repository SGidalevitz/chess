import org.junit.Test;
import static org.junit.Assert.*;

public class SquareTest {
    @Test
    public void testToString() {
        // Test that the toString for Square works properly.
        assertEquals("n", new Square(new Position(0, 0), PieceType.Knight, PieceColor.Black).toString());
        assertEquals("Q", new Square(new Position(0, 0), PieceType.Queen, PieceColor.White).toString());
        assertEquals("p", new Square(new Position(0, 0), PieceType.Pawn, PieceColor.Black).toString());
    }
}