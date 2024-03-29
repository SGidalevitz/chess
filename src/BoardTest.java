import org.junit.Test;

import java.util.Optional;

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
            assertThrows(IllegalArgumentException.class, () -> new Board(FEN));
        }
    }
    @Test
    public void testFENGeneration() {
        // Test position 1
        Board board = new Board("8/3k3p/1p6/1Np5/2P3P1/4p3/3bK3/8 w - - 0 54");
        board.getSquareAtPosition(Position.chessPositionToPosition("g4")).pieceType = PieceType.Empty;
        board.getSquareAtPosition(Position.chessPositionToPosition("g4")).pieceColor = Optional.empty();
        board.getSquareAtPosition(Position.chessPositionToPosition("g5")).pieceType = PieceType.Pawn;
        board.getSquareAtPosition(Position.chessPositionToPosition("g5")).pieceColor = Optional.of(PieceColor.White);
        board.switchToMove();
        assertEquals("8/3k3p/1p6/1Np3P1/2P5/4p3/3bK3/8 b - - 0 54", board.getFEN());

        //Test position 2
        board = new Board("r1b1qrkb/pp1n1p1p/4p1pP/2pnP1B1/3p2N1/3P1NP1/PPP2PB1/R2QR1K1 b - - 6 16");
        board.getSquareAtPosition(Position.chessPositionToPosition("d7")).pieceType = PieceType.Empty;
        board.getSquareAtPosition(Position.chessPositionToPosition("d7")).pieceColor = Optional.empty();
        board.getSquareAtPosition(Position.chessPositionToPosition("b6")).pieceType = PieceType.Knight;
        board.getSquareAtPosition(Position.chessPositionToPosition("b6")).pieceColor = Optional.of(PieceColor.Black);
        board.switchToMove();
        board.incrementFullMoveNumber();
        board.incrementHalfMoveClock();
        assertEquals("r1b1qrkb/pp3p1p/1n2p1pP/2pnP1B1/3p2N1/3P1NP1/PPP2PB1/R2QR1K1 w - - 7 17", board.getFEN());

        //Test position 3
        board = new Board("3N1R2/P7/5k2/5bp1/r7/r4BK1/6P1/8 b - - 10 53");
        board.getSquareAtPosition(Position.chessPositionToPosition("f6")).pieceType = PieceType.Empty;
        board.getSquareAtPosition(Position.chessPositionToPosition("f6")).pieceColor = Optional.empty();
        board.getSquareAtPosition(Position.chessPositionToPosition("e5")).pieceType = PieceType.King;
        board.getSquareAtPosition(Position.chessPositionToPosition("e5")).pieceColor = Optional.of(PieceColor.Black);
        board.switchToMove();
        board.incrementHalfMoveClock();
        board.incrementFullMoveNumber();
        assertEquals("3N1R2/P7/8/4kbp1/r7/r4BK1/6P1/8 w - - 11 54", board.getFEN());

    }

    @Test
    public void testResultsInCheck() {
        Board board = new Board("rnb1k1nr/pppp1p2/4pqpp/8/1b2P1Q1/1P6/P1PPKPPP/RNB2BNR w kq - 3 6");
        assertTrue(board.resultsInCheck("e2", "f3", PieceColor.White));
        board = new Board("r1b1k1nr/1ppp1p2/p1n1p1pp/3N1q2/1bB1P1Q1/1P3N2/P1PP1PPP/R1B1K2R w KQkq - 0 9");
        assertTrue(board.resultsInCheck("d2", "d3", PieceColor.White));
        board = new Board("r1b3nr/1ppp1p2/p1n1pkpp/5q2/1NB1P1QP/1P3N2/P1PP1PP1/R1B1K2R b KQ - 0 11");
        assertTrue(board.resultsInCheck("f6", "g5", PieceColor.Black));
        assertTrue(board.resultsInCheck("f6", "e5", PieceColor.Black));
        assertFalse(board.resultsInCheck("f5", "f3", PieceColor.Black));
        board = new Board("r1b3nr/1ppp1p2/2n1pkp1/p5pP/1NBNP3/1P4q1/P1PP1PP1/R1B1K2R w KQ - 0 15");
        assertTrue(board.resultsInCheck("f2", "f3", PieceColor.White));
        assertTrue(board.resultsInCheck("f2", "f4", PieceColor.White));
        assertFalse(board.resultsInCheck("c3", "c3", PieceColor.White));
    }

}
