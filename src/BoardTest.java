import org.junit.Test;

import java.util.Optional;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class BoardTest {

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
    public void testSwitchToMove() {
        Board board = Board.STARTING_BOARD;
        assertEquals(board.getToMove(), PieceColor.White);
        board.switchToMove();
        assertEquals(board.getToMove(), PieceColor.Black);
        board.switchToMove();
        assertEquals(board.getToMove(), PieceColor.White);
    }
    @Test
    public void testIncrements() {
        Board board = Board.STARTING_BOARD;
        assertEquals(0, board.halfMoveClock);
        assertEquals(1, board.fullMoveNumber);
        board.incrementHalfMoveClock();
        assertEquals(1, board.halfMoveClock);
        board.incrementFullMoveNumber();
        assertEquals(2, board.fullMoveNumber);
        board.resetHalfMoveClock();
        assertEquals(0, board.halfMoveClock);
        board.incrementFullMoveNumber();
        assertEquals(3, board.fullMoveNumber);
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
        board.makeMove("g4", "g5");
        assertEquals("8/3k3p/1p6/1Np3P1/2P5/4p3/3bK3/8 b - - 0 54", board.getFEN());

        //Test position 2
        board = new Board("r1b1qrkb/pp1n1p1p/4p1pP/2pnP1B1/3p2N1/3P1NP1/PPP2PB1/R2QR1K1 b - - 6 16");
        board.makeMove("d7", "b6");
        assertEquals("r1b1qrkb/pp3p1p/1n2p1pP/2pnP1B1/3p2N1/3P1NP1/PPP2PB1/R2QR1K1 w - - 7 17", board.getFEN());

        //Test position 3
        board = new Board("3N1R2/P7/5k2/5bp1/r7/r4BK1/6P1/8 b - - 10 53");
        board.makeMove("f6", "e5");
        assertEquals("3N1R2/P7/8/4kbp1/r7/r4BK1/6P1/8 w - - 11 54", board.getFEN());

    }
    @Test
    public void testGetSquareAtPosition() {
        Board board = Board.STARTING_BOARD;
        Position at = new Position("e1");
        assertEquals(new Square(at, PieceType.King, PieceColor.White), board.getSquareAtPosition(at));
        at = new Position("f7");
        assertEquals(new Square(at, PieceType.Pawn, PieceColor.Black), board.getSquareAtPosition(at));
        at = new Position("b1");
        assertEquals(new Square(at, PieceType.Knight, PieceColor.White), board.getSquareAtPosition(at));
    }
    @Test
    public void testGetPieceColorFromChar() {
        assertEquals(PieceColor.Black, Board.getPieceColorFromChar('q'));
        assertEquals(PieceColor.Black, Board.getPieceColorFromChar('p'));
        assertEquals(PieceColor.White, Board.getPieceColorFromChar('R'));
        assertEquals(PieceColor.White, Board.getPieceColorFromChar('B'));
        assertThrows(IllegalArgumentException.class, () -> Board.getPieceColorFromChar('&'));
        assertThrows(IllegalArgumentException.class, () -> Board.getPieceColorFromChar('a'));
    }
    @Test
    public void testGetPieceTypeFromChar() {
        assertEquals(PieceType.Bishop, Board.getPieceTypeFromChar('b'));
        assertEquals(PieceType.Bishop, Board.getPieceTypeFromChar('B'));
        assertEquals(PieceType.Queen, Board.getPieceTypeFromChar('q'));
        assertEquals(PieceType.Queen, Board.getPieceTypeFromChar('Q'));
        assertThrows(IllegalArgumentException.class, () -> Board.getPieceTypeFromChar('&'));
        assertThrows(IllegalArgumentException.class, () -> Board.getPieceTypeFromChar('a'));
    }
    @Test
    public void testGetCharFromPiece() {
        assertEquals('n', Board.getCharFromPiece(PieceType.Knight, PieceColor.Black));
        assertEquals('k', Board.getCharFromPiece(PieceType.King, PieceColor.Black));
        assertEquals('P', Board.getCharFromPiece(PieceType.Pawn, PieceColor.White));
        assertEquals('B', Board.getCharFromPiece(PieceType.Bishop, PieceColor.White));
        assertThrows(IllegalStateException.class, () -> Board.getCharFromPiece(PieceType.Empty, PieceColor.White));
    }
    @Test
    public void testPieceTypeToString() {
        assertEquals("King", Board.pieceTypeToString(PieceType.King));
        assertEquals("Queen", Board.pieceTypeToString(PieceType.Queen));
        assertEquals("Rook", Board.pieceTypeToString(PieceType.Rook));
        assertEquals("Bishop", Board.pieceTypeToString(PieceType.Bishop));
        assertEquals("Knight", Board.pieceTypeToString(PieceType.Knight));
        assertEquals("Pawn", Board.pieceTypeToString(PieceType.Pawn));
        assertEquals("Empty", Board.pieceTypeToString(PieceType.Empty));
    }
    @Test
    public void testCharIsInBetween() {
        assertTrue(Board.charIsBetween('5', 3, 8));
        assertTrue(Board.charIsBetween('7', 6, 7));
        assertTrue(Board.charIsBetween('1', 0, 7));
        assertTrue(Board.charIsBetween0And8('5'));
        assertFalse(Board.charIsBetween('1', 4, 6));
        assertFalse(Board.charIsBetween('9', 3, 4));
        assertFalse(Board.charIsBetween0And8('9'));
    }
    @Test
    public void testLocateKing() {
        Board board = Board.STARTING_BOARD;
        assertEquals(new Position("e1"), board.locateKing(PieceColor.White));
        assertEquals(new Position("e8"), board.locateKing(PieceColor.Black));
        // Board with same layout as starting position, except white king is replaced with a pawn
        Board board1 = new Board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQPBNR w KQkq - 0 1");
        assertThrows(IllegalStateException.class, () -> board1.locateKing(PieceColor.White));
    }
    @Test
    public void testIsCapture() {
        Board board = new Board("rnbqkbnr/ppp1pppp/8/3p4/4P3/8/PPPP1PPP/RNBQKBNR w KQkq d6 0 2");
        assertTrue(Board.isCapture(board.getSquareAtPosition(new Position("e4")), board.getSquareAtPosition(new Position("d5"))));
        assertFalse(Board.isCapture(board.getSquareAtPosition(new Position("e4")), board.getSquareAtPosition(new Position("e5"))));

        board = new Board("rnbqkbnr/ppp1pppp/8/3p4/4P1Q1/8/PPPP1PPP/RNB1KBNR b KQkq - 1 2");
        assertTrue(Board.isCapture(board.getSquareAtPosition(new Position("c8")), board.getSquareAtPosition(new Position("g4"))));
        assertFalse(Board.isCapture(board.getSquareAtPosition(new Position("c8")), board.getSquareAtPosition(new Position("f5"))));
    }
    @Test
    public void testFindMoves() {

        Board board = Board.STARTING_BOARD;
        // Test Pawns
        ArrayList<Move> sampleMoves0 = new ArrayList<>();
        String[] strings0 = {"e3", "e4"};
        for (String str : strings0) {
            sampleMoves0.add(new Move(new Position("e2"), new Position(str)));
        }
        assertEquals(sampleMoves0, board.findMoves("e2"));

        ArrayList<Move> sampleMoves1 = new ArrayList<>();
        String[] strings1 = {"e6", "e5"};
        for (String str : strings1) {
            sampleMoves1.add(new Move(new Position("e7"), new Position(str)));
        }
        assertEquals(sampleMoves1, board.findMoves("e7"));
        // Test Knights
        board = new Board("rnbqkb1r/pppppppp/8/4N2n/8/8/PPPPPPPP/RNBQKB1R w KQkq - 4 3");
        ArrayList<Move> sampleMoves2 = new ArrayList<>();
        String[] strings2 = {"d3", "f3", "c4", "g4", "c6", "g6", "d7", "f7"};
        for (String str : strings2) {
            sampleMoves2.add(new Move(new Position("e5"), new Position(str)));
        }
        assertTrue(equalsIgnoringOrder(sampleMoves2, board.findMoves("e5")));

        // Test Bishops

        board = new Board("rnbqkbnr/pppp1pp1/4p2p/8/2B5/4P3/PPPP1PPP/RNBQK1NR w KQkq - 0 3");
        ArrayList<Move> sampleMoves3 = new ArrayList<>();
        String[] strings3 = {"b3", "d3", "e2", "f1", "b5", "d5", "a6", "e6"};
        for (String str : strings3) {
            sampleMoves3.add(new Move(new Position("c4"), new Position(str)));
        }
        assertTrue(equalsIgnoringOrder(sampleMoves3, board.findMoves("c4")));

        // Test Rooks
        board = new Board("rnbqkbnr/pppp4/4pppp/8/2R4P/8/PPPPPPP1/RNBQKBN1 w Qkq - 0 5");
        ArrayList<Move> sampleMoves4 = new ArrayList<>();
        String[] strings4 = {"c3", "a4", "b4", "d4", "e4", "f4", "g4", "c5", "c6", "c7"};
        for (String str : strings4) {
            sampleMoves4.add(new Move(new Position("c4"), new Position(str)));
        }
        assertTrue(equalsIgnoringOrder(sampleMoves4, board.findMoves("c4")));

        // Test Queens

        board = new Board("rnbqkbnr/pppppp2/6pp/8/6Q1/4P3/PPPP1PPP/RNB1KBNR w KQkq - 0 3");
        ArrayList<Move> sampleMoves5 = new ArrayList<>();
        String[] strings5 = {"d1", "e2", "f3", "g3", "h3", "a4", "b4", "c4", "d4", "e4", "f4", "h4", "f5", "g5", "h5", "e6", "g6", "d7"};
        for (String str : strings5) {
            sampleMoves5.add(new Move(new Position("g4"), new Position(str)));
        }
        assertTrue(equalsIgnoringOrder(sampleMoves5, board.findMoves("g4")));

        //Test Kings

        board = new Board("rnbqk1nr/ppppp1b1/5ppp/8/2K5/4P3/PPPP1PPP/RNBQ1BNR w kq - 2 7");
        ArrayList<Move> sampleMoves6 = new ArrayList<>();
        String[] strings6 = {"b3", "c3", "d3", "b4", "d4", "b5", "c5", "d5"};
        for (String str : strings6) {
            sampleMoves6.add(new Move(new Position("c4"), new Position(str)));
        }
        assertTrue(equalsIgnoringOrder(sampleMoves6, board.findMoves("c4")));

        // Test En Passant

        board = new Board("rnbqkbnr/1pppp1pp/p7/5pP1/8/8/PPPPPP1P/RNBQKBNR w KQkq f6 0 3");
        ArrayList<Move> sampleMoves7 = new ArrayList<>();
        String[] strings7 = {"f6", "g6"};
        for (String str : strings7) {
            sampleMoves7.add(new Move(new Position("g5"), new Position(str)));
        }
        assertTrue(equalsIgnoringOrder(sampleMoves7, board.findMoves("g5")));

        board = new Board("rnbqkbnr/p1pppppp/8/8/1pP4P/8/PP1PPPP1/RNBQKBNR b KQkq c3 0 3");
        ArrayList<Move> sampleMoves8 = new ArrayList<>();
        String[] strings8 = {"b3", "c3"};
        for (String str : strings8) {
            sampleMoves8.add(new Move(new Position("b4"), new Position(str)));
        }
        assertTrue(equalsIgnoringOrder(sampleMoves8, board.findMoves("b4")));
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
        assertFalse(board.resultsInCheck("c2", "c3", PieceColor.White));
    }
    public static boolean equalsIgnoringOrder(ArrayList<Move> list0, ArrayList<Move> list1) {
        return list0.containsAll(list1) && list0.size() == list1.size();
    }
    @Test
    public void testEqualsIgnoringOrder() {
        Move move0 = new Move("e2", "e4");
        Move move1 = new Move("e7", "e5");
        ArrayList<Move> list0 = new ArrayList<>();
        ArrayList<Move> list1 = new ArrayList<>();
        list0.add(move0);
        list0.add(move1);
        list1.add(move1);
        list1.add(move0);
        assertTrue(equalsIgnoringOrder(list0, list1));

    }

}
