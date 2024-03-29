import java.util.*;

public class Board {
    public static final Board STARTING_BOARD = new Board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
    public static final int BOARD_DIMENSION = 8;
    private Square[][] board;
    private PieceColor toMove; // 0 for white, 1 for black
    private Optional<String> castlingRights;
    private Optional<Position> enPassantTargetSquare;
    private int halfMoveClock;
    private int fullMoveNumber;

    // Construct a board with a given FEN code, which is the standard for a chess position
    /*
    Method Tested in PositionTest.java
    Description: This method takes in a String, the Forsyth–Edwards Notation (or FEN for short) of a position, and creates a board that contains all of the information given by this notation.
    Parameters: chessPosition(String) -> the position to check
    Examples: refer to test method.
     */
    public Board(String FEN) {
        String[] partsOfFEN = FEN.split(" ");
        String exceptionMsg = getExceptionMessageForFENValidityIfExists(FEN);
        if (exceptionMsg != null) throw new IllegalArgumentException(exceptionMsg);
        this.board = readFEN(partsOfFEN[0]);
        if (partsOfFEN[1].equals("w")) {
            this.toMove = PieceColor.White;
        } else {
            this.toMove = PieceColor.Black;
        }
        if (partsOfFEN[2].equals("-")) {
            this.castlingRights = Optional.empty();
        }
        else {
            this.castlingRights = Optional.of(partsOfFEN[2]);
        }
        if (partsOfFEN[3].equals("-")) {
            this.enPassantTargetSquare = Optional.empty();
        } else {
            this.enPassantTargetSquare = Optional.of(Position.chessPositionToPosition(partsOfFEN[3]));
        }
        this.halfMoveClock = Integer.parseInt(partsOfFEN[4]);
        this.fullMoveNumber = Integer.parseInt(partsOfFEN[5]);
    }
    public Square[][] readFEN(String boardAsFENNotation) {
        String[] ranks = boardAsFENNotation.split("/");
        Square[][] board = new Square[8][8];
        for (int rankIndex = BOARD_DIMENSION - 1; rankIndex >= 0; rankIndex--) {
            String rank = ranks[BOARD_DIMENSION - 1 - rankIndex];
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

    public String getExceptionMessageForFENValidityIfExists(String FEN) {
        String[] partsOfFEN = FEN.split(" ");
        if (partsOfFEN.length != 6)
            return "FEN code has too many arguments, expected 6 but given " + partsOfFEN.length + ".";
        // Check part 1 - position
        String[] board = partsOfFEN[0].split("/");
        if (board.length != BOARD_DIMENSION) {
            return "Argument 1, which is the board position, has an incorrect number of ranks supplied, expected " + BOARD_DIMENSION + ", but given " + board.length + ".";
        }
        for (int rank = 0; rank < BOARD_DIMENSION; rank++) {
            int count = 0;
            for (int file = 0; file < board[rank].length(); file++) {
                char c = board[rank].charAt(file);
                if (charIsBetween(c, 0, 9)) {
                    count += (c - '0');
                } else {
                    count += 1;
                }
            }
            if (count != 8) {
                return "Argument 1, which is the board position, has too many (" + count + ") files on rank " + ((char) ((7 - rank) + 'a')) + ", should be " + BOARD_DIMENSION + ".";
            }
        }
        // Check part 2 - whose move it is
        String toMove = partsOfFEN[1];
        if (toMove.length() != 1) {
            return "Argument 2, which is supposed to be the player to move, is \"" + toMove + "\", which is more than one character long.";
        }
        char toMoveAsChar = toMove.charAt(0);
        if (toMoveAsChar != 'b' && toMoveAsChar != 'w') {
            return "Argument 2, which is supposed to be the player to move, is \"" + toMove + "\", which is not \"b\" or \"w\".";
        }
        // Check part 3 - castling rights
        String castlingRights = (partsOfFEN[2].equals("-")) ? "" : partsOfFEN[2];
        if (castlingRights.length() > 4) {
            return "Argument 3, which is supposed to be castling rights, is \"" + castlingRights + "\", which is not between 1 and 4 characters long."; // KQkq
        }
        if (!castlingRights.isEmpty()) {
            int crCount = 0;
            char[] crArr = castlingRights.toCharArray();
            char[] arr = "KQkq".toCharArray();
            for (int i = 0; i < 4; i++) {
                if (arr[i] == crArr[crCount]) {
                    crCount++;
                }
            }
            if (crCount != crArr.length) {
                return "Argument 3, which is supposed to be castling rights, is \"" + castlingRights + "\", which is not in the proper format.";
            }
        }
        // Check part 4 - en passant target square
        String enPassantTargetSquare = partsOfFEN[3];

        if (!enPassantTargetSquare.equals("-")) {
            if (enPassantTargetSquare.length() != 2)
                throw new IllegalArgumentException("Argument 4, which is supposed to be en passant target square, must be a string of length 2.");
            int properRow = enPassantTargetSquare.charAt(0) - 'a';
            int properCol = enPassantTargetSquare.charAt(1) - '1';
            String exceptionMsg = Position.getExceptionMessageIfExists(properRow, properCol);
            if (exceptionMsg != null) {
                return "Argument 4, which is supposed to be en passant target square, has invalid bounds. " + exceptionMsg;
            }
        }
        // Check part 5 - half move clock
        if (!correctNumberFormat(partsOfFEN[4])) {
            return "Argument 5, which is supposed to be half move clock, is \"" + partsOfFEN[4] + "\", which is not an integer.";
        }
        int halfMoveClock = Integer.parseInt(partsOfFEN[4]);
        if (halfMoveClock < 0 || halfMoveClock >= 50) {
            return "Half move clock is \"" + halfMoveClock + "\", which is not between 0 and 49.";
        }
        // Check part 6 - full move number
        if (!correctNumberFormat(partsOfFEN[5])) {
            return "Argument 6, which is supposed to be full move number, is \"" + partsOfFEN[5] + "\", which is not an integer.";
        }
        int fullMoveNumber = Integer.parseInt(partsOfFEN[5]);
        if (fullMoveNumber < 1) {
            return "Full move number is \"" + fullMoveNumber + "\", which is not greater than or equal to 1.";
        }
        return null;
    }
    public String getFEN() {
        StringBuilder builder = new StringBuilder();
        // Part 1 - position
        for (int rank = BOARD_DIMENSION - 1; rank >= 0; rank--) {
            int emptySquareCount = 0;
            for (int file = 0; file < BOARD_DIMENSION; file++) {
                if (this.board[rank][file].getPieceType() == PieceType.Empty) {
                    emptySquareCount++;
                }
                else {
                    if (emptySquareCount > 0) {
                        builder.append(emptySquareCount);
                        emptySquareCount = 0;
                    }
                    PieceType type = this.board[rank][file].getPieceType();
                    PieceColor color = this.board[rank][file].getPieceColor().orElseThrow();
                    builder.append(getCharFromPiece(type, color));
                }
            }
            if (emptySquareCount > 0) {
                builder.append(emptySquareCount);
            }
            if (rank != 0) {
                builder.append("/");
            }
        }
        builder.append(" ");

        // Part 2 - whose move it is
        builder.append(switch (this.toMove) {
            case White -> "w";
            case Black -> "b";
        }).append(" ");

        // Part 3 - castling rights
        builder.append(this.castlingRights.orElse("-")).append(" ");

        // Part 4 - en passant target square
        builder.append(this.enPassantTargetSquare.map(Position::positionToChessPosition).orElse("-")).append(" ");

        // Part 5 - half move clock
        builder.append(halfMoveClock).append(" ");

        // Part 6 - full move number
        builder.append(fullMoveNumber);


        return builder.toString();
    }

    public boolean correctNumberFormat(String num) {
        try {
            Integer.parseInt(num);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    //If we already have the board info, such as if we have to simulate moves, then we can construct the class with given information usually extracted from FEN
    public Board(Square[][] board, PieceColor toMove, String castlingRights, Position enPassantTargetSquare, int halfMoveClock, int fullMove) {
        this.board = board;
        this.toMove = toMove;
        this.castlingRights = (castlingRights.isEmpty()) ? Optional.empty() : Optional.of(castlingRights);
        this.enPassantTargetSquare = Optional.of(enPassantTargetSquare);
        this.halfMoveClock = halfMoveClock;
        this.fullMoveNumber = fullMove;
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
        return switch (c) {
            case 'K', 'k' -> PieceType.King;
            case 'Q', 'q' -> PieceType.Queen;
            case 'R', 'r' -> PieceType.Rook;
            case 'B', 'b' -> PieceType.Bishop;
            case 'N', 'n' -> PieceType.Knight;
            case 'P', 'p' -> PieceType.Pawn;
            default -> throw new IllegalArgumentException("Method getPieceTypeFromChar expects a proper chess character, but is given " + c + ".");
        };
    }
    public char getCharFromPiece(PieceType pieceType, PieceColor pieceColor) {
        return switch (pieceType) {
            case King -> switch (pieceColor) {
                case White -> 'K';
                case Black -> 'k';
            };
            case Queen -> switch (pieceColor) {
                case White -> 'Q';
                case Black -> 'q';
            };
            case Rook -> switch (pieceColor) {
                case White -> 'R';
                case Black -> 'r';
            };
            case Bishop -> switch (pieceColor) {
                case White -> 'B';
                case Black -> 'b';
            };
            case Knight -> switch (pieceColor) {
                case White -> 'N';
                case Black -> 'n';
            };
            case Pawn -> switch (pieceColor) {
                case White -> 'P';
                case Black -> 'p';
            };
            case Empty -> throw new IllegalStateException("Empty piece got through handling in loop in getFEN().");
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
        for (int rank = BOARD_DIMENSION - 1; rank >= 0; rank--) {
            for (int file = 0; file < BOARD_DIMENSION; file++) {
                if (file == 0) {
                    builder.append(rank + 1).append(" - ");
                }
                builder.append(board[rank][file].toString()).append(" ");
            }
            builder.append("\n");
        }
        builder.append("    ");
        for (int i = 0; i < BOARD_DIMENSION; i++) {
            builder.append((char)('a' + i)).append(" ");
        }
        return builder.toString();
    }
}
