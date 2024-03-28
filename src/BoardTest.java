import org.junit.Test;
import static org.junit.Assert.*;

public class BoardTest {
    @Test
    public void testGetPieceAttributesFromChar() {
        Board board = Board.startingBoard;
        // Test the getPieceTypeFromChar() method.
        assertEquals(PieceType.Rook, board.getPieceTypeFromChar('r'));
        assertEquals(PieceType.Queen, board.getPieceTypeFromChar('Q'));
        assertEquals(PieceType.Bishop, board.getPieceTypeFromChar('b'));
        // Test the getPieceColorFromChar() method.
        assertEquals(PieceColor.Black, board.getPieceColorFromChar('r'));
        assertEquals(PieceColor.White, board.getPieceColorFromChar('Q'));
        assertEquals(PieceColor.Black, board.getPieceColorFromChar('b'));

    }

    @Test
    public void testSquareInitialization() {
        Board board = Board.startingBoard;
        // Test that the board reads the FEN correctly by checking the piece types of certain squares on the starting board.
        assertEquals(PieceType.Pawn, board.getBoard()[1][0].getPieceType());
        assertEquals(PieceType.Rook, board.getBoard()[7][0].getPieceType());
        assertEquals(PieceType.Empty, board.getBoard()[5][4].getPieceType());
        // Test that the board reads the FEN correctly by checking the piece colors of certain squares on the starting board.
        assertTrue(board.getBoard()[0][4].getPieceColor().isPresent());
        assertEquals(PieceColor.White, board.getBoard()[0][4].getPieceColor().get());
        assertTrue(board.getBoard()[6][7].getPieceColor().isPresent());
        assertEquals(PieceColor.Black, board.getBoard()[6][7].getPieceColor().get());
        assertTrue(board.getBoard()[3][4].getPieceColor().isEmpty());
    }
}