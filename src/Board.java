import java.util.*;
import java.util.HashMap;

public class Board {
    public static final Board startingBoard = new Board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
    private Square[][] board;
    private int toMove; // 0 for white, 1 for black
    private String castlingRights;
    private Optional<Position> enPassantTargetSquare;
    private int halfMoveClock;
    private int fullMoveNumber;
    // Construct a board with a given FEN code, which is the standard for a chess position
    /*
    IMPLEMENT -> Method Tested in PositionTest.java
    Description: This method takes in a String, the Forsyth–Edwards Notation (or FEN for short) of a position, and creates a board that contains all of the information given by this notation.
    Parameters: chessPosition(String) -> the position to check
    Examples: refer to test method.
     */
    public Board(String FEN) {
        String[] partsOfFEN = FEN.split(" ");
        this.board = readFen(partsOfFEN[0]);
        if (partsOfFEN[1].equals("w")) {
            this.toMove = 0;
        }
        else {
            this.toMove = 1;
        }
        this.castlingRights = partsOfFEN[2];
        if (partsOfFEN[3].equals("-")) {
            this.enPassantTargetSquare = Optional.empty();
        }
        else {
            this.enPassantTargetSquare = Optional.of(Position.chessPositionToPosition(partsOfFEN[3]));
        }
        this.halfMoveClock = Integer.parseInt(partsOfFEN[4]);
        this.fullMoveNumber = Integer.parseInt(partsOfFEN[5]);
    }
    public String getExceptionMessageForFENValidityIfExists(String FEN) {
        return null;
        // Implement later
    }
    //If we already have the board info, such as if we have to simulate moves, then we can construct the class with given information usually extracted from FEN
    public Board(Square[][] board, int toMove, String castlingRights, Position enPassantTargetSquare, int halfMoveClock, int fullMove) {
        this.board = board;
        this.toMove = toMove;
        this.castlingRights = castlingRights;
        this.enPassantTargetSquare = Optional.of(enPassantTargetSquare);
        this.halfMoveClock = halfMoveClock;
        this.fullMoveNumber = fullMove;
    }
    public Square[][] readFen(String boardAsFENNotation) {
        String[] ranks = boardAsFENNotation.split("/");
        Square[][] board = new Square[8][8];
        for (int rankIndex = 7; rankIndex >= 0; rankIndex--) {
            String rank = ranks[7 - rankIndex];
            int fileIndex = 0;
            for (int j = 0; j < rank.length(); j++) {
                char c = rank.charAt(j);
                //In FEN format, a number in the board representation indicates the number of empty squares, so basically we just iterate through it here
                if (charIsBetween0And8(c)) {
                    for (int k = 0; k < Integer.parseInt("" + c); k++) {
                        //type: 0 because the piece is empty
                        board[rankIndex][fileIndex] = new Square(new Position(rankIndex, fileIndex));
                        fileIndex++;
                    }
                }
                // if it is not a number, so a piece, then we just put it in
                else {
                    // Get the piece type from the pre-made map
                    board[rankIndex][fileIndex] = new Square(new Position(rankIndex, fileIndex), getPieceTypeFromChar(c), getPieceColorFromChar(c));
                    fileIndex++;
                }

            }
        }
        return board;
    }

    public Square[][] getBoard() {
        return this.board;
    }

    public PieceColor getPieceColorFromChar(char c) {
        return switch (c) {
            case 'K', 'Q', 'R', 'B', 'N', 'P' -> PieceColor.White;
            case 'k', 'q', 'r', 'b', 'n', 'p' -> PieceColor.Black;
            default -> throw new IllegalArgumentException("Method getPieceColorFromChar expects a proper chess character, but is given " + c + ".");
        };
    }
    public PieceType getPieceTypeFromChar(char c) {
        return switch(c) {
            case 'K', 'k' -> PieceType.King;
            case 'Q', 'q' -> PieceType.Queen;
            case 'R', 'r' -> PieceType.Rook;
            case 'B', 'b' -> PieceType.Bishop;
            case 'N', 'n' -> PieceType.Knight;
            case 'P', 'p' -> PieceType.Pawn;
            default -> throw new IllegalArgumentException("Method getPieceTypeFromChar expects a proper chess character, but is given " + c + ".");
        };
    }
    public static boolean charIsBetween(char c, int val1, int val2) {
        return val1 <= c - '1' && c - '1' < val2;
    }
    public static boolean charIsBetween0And8(char c) {
        return charIsBetween(c, 0, 8);
    }
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int rank = 7; rank >= 0; rank--)  {
            for (int file = 0; file < 8; file++) {
                if (file == 0) {
                    builder.append(Position.positionToChessPosition(new Position(rank, file)).charAt(0)).append(" - ");
                }
                builder.append(board[rank][file].toString()).append(" ");
            }
            builder.append("\n");
        }
        builder.append("    ");
        for (int i = 1; i <= 8; i++) {
            builder.append(i).append(" ");
        }
        return builder.toString();
    }
}
