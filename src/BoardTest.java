import org.junit.Test;
import static org.junit.Assert.*;

public class BoardTest {
    @Test
    public void testGetPieceAttributesFromChar() {
        Board board = Board.STARTING_BOARD;
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
        Board board = Board.STARTING_BOARD;
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
    @Test
    public void testInvalidFENs() {
        String[] FENsToTest = {
                // Test first FEN argument - position
                "rnbqkbknr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
                "rnbqkbnr/pppppppp/8/8/9/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
                "rnbqkbnr/pppppppp/8/8/4p4/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",

                // Test second FEN argument - whose move it is
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR k KQkq - 0 1",
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR hello KQkq - 0 1",
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR 136 KQkq - 0 1",

                // Test third FEN argument - castling rights
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w kqKQ - 0 1",
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w kK - 0 1",
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w awesome - 0 1",

                // Test fourth FEN argument - en passant target square
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq hello 0 1",
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq z5 0 1",
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq q11 0 1",

                // Test fifth FEN argument - half move clock
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 120 1",
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - -20 1",
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 2390984 1",

                // Test sixth FEN argument - full move number
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 0",
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 -120",
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 -39489"
        };
        for (String FEN : FENsToTest) {
            assertThrows(IllegalArgumentException.class, () -> { Board testBoard = new Board(FEN); });
        }
    }
}