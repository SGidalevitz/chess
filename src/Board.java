import java.util.*;
import java.util.stream.Collectors;

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
    Description: This method takes in a String, the Forsyth–Edwards Notation (or FEN for short) of a position, and creates a board that contains all the information given by this notation.
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
                    if (crCount == crArr.length) break;
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

    public Board(Board other) {
        this.board = new Square[BOARD_DIMENSION][BOARD_DIMENSION];
        for (int i = 0; i < BOARD_DIMENSION; i++) {
            for (int j = 0; j < BOARD_DIMENSION; j++) {
                this.board[i][j] = new Square(other.board[i][j]);
            }
        }
        this.toMove = other.toMove;
        this.castlingRights = other.castlingRights;
        this.enPassantTargetSquare = other.enPassantTargetSquare;
        this.halfMoveClock = other.halfMoveClock;
        this.fullMoveNumber = other.fullMoveNumber;
    }

    public void switchToMove() {
        toMove = switch(toMove) {
            case Black -> PieceColor.White;
            case White -> PieceColor.Black;
        };
    }
    public void incrementFullMoveNumber() {
        fullMoveNumber++;
    }
    public void incrementHalfMoveClock() {
        halfMoveClock++;
    }
    public void resetHalfMoveClock() {
        halfMoveClock = 0;
    }
    public Square getSquareAtPosition(Position pos) {
        return this.board[pos.row][pos.col];
    }
    public PieceColor getToMove() {
        return this.toMove;
    }




    public Square[][] getBoard() {
        return this.board;
    }

    public static PieceColor getPieceColorFromChar(char c) {
        return switch (c) {
            case 'K', 'Q', 'R', 'B', 'N', 'P' -> PieceColor.White;
            case 'k', 'q', 'r', 'b', 'n', 'p' -> PieceColor.Black;
            default -> throw new IllegalArgumentException("Method getPieceColorFromChar expects a proper chess character, but is given " + c + ".");
        };
    }

    public static PieceType getPieceTypeFromChar(char c) {
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
    public static char getCharFromPiece(PieceType pieceType, PieceColor pieceColor) {
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
    public static String pieceTypeToString(PieceType pieceType) {
        return switch (pieceType) {
            case King -> "King";
            case Queen -> "Queen";
            case Rook -> "Rook";
            case Bishop -> "Bishop";
            case Knight -> "Knight";
            case Pawn -> "Pawn";
            case Empty -> "Empty";
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
    public String boardWithPossibleMovesFor(Position initialPosition) {
        ArrayList<Move> possibleMoves = findMoves(initialPosition);
        StringBuilder builder = new StringBuilder();
        for (int rank = BOARD_DIMENSION - 1; rank >= 0; rank--) {
            for (int file = 0; file < BOARD_DIMENSION; file++) {
                if (file == 0) {
                    builder.append(rank + 1).append(" - ");
                }
                Move current = new Move(initialPosition, new Position(rank, file));
                if (possibleMoves.contains(current)) {
                    builder.append("\u001B[31m").append(board[rank][file].toString()).append("\u001B[0m ");
                }
                else {
                    builder.append(board[rank][file].toString()).append(" ");
                }

            }
            builder.append("\n");
        }
        return getLastPartOfToString(builder);
    }
    public String boardWithLatestMove(Move move) {
        Position initialPosition = move.origin;
        Position targetPosition = move.target;
        StringBuilder builder = new StringBuilder();
        for (int rank = BOARD_DIMENSION - 1; rank >= 0; rank--) {
            for (int file = 0; file < BOARD_DIMENSION; file++) {
                if (file == 0) {
                    builder.append(rank + 1).append(" - ");
                }
                //Move current = new Move(initialPosition, new Position(rank, file));
                Position positionOn = new Position(rank, file);
                if (initialPosition.equals(positionOn) || targetPosition.equals(positionOn)) {
                    builder.append("\u001B[31m").append(board[rank][file].toString()).append("\u001B[0m ");
                }
                else {
                    builder.append(board[rank][file].toString()).append(" ");
                }

            }
            builder.append("\n");
        }
        return getLastPartOfToString(builder);
    }

    private String getLastPartOfToString(StringBuilder builder) {
        builder.append("    ");
        builder.append("| ".repeat(BOARD_DIMENSION));
        builder.append("\n    ");
        for (int i = 0; i < BOARD_DIMENSION; i++) {
            builder.append((char)('a' + i)).append(" ");
        }
        return builder.toString();
    }
    // TODO: Track the king's coordinates separately as this operation doesn't need to run each time
    public Position locateKing(PieceColor pieceColor) {
        for (Square[] rank : board) {
            for (Square square : rank) {
                if (square.getPieceType() == PieceType.King && square.getPieceColor().orElseThrow() == pieceColor) {
                    return square.getPosition();
                }
            }
        }
        throw new IllegalStateException("No king found on board.");
    }
    public boolean resultsInCheck(Move move, PieceColor sideColor) {
        Board testBoard = new Board(this);
        Position originPosition = move.origin;
        Position targetPosition = move.target;
        testBoard.makeMove(originPosition, targetPosition);
        Position kingLocation = testBoard.locateKing(sideColor);
        HashSet<Move> movesOfAllPieces = testBoard.getMovesOfAllPieces(PieceColor.getOpposite(sideColor));
        HashSet<Position> finalPositionsOfAllMoves = movesOfAllPieces.stream()
                .map(o -> o.target)
                .collect(Collectors.toCollection(HashSet::new));
        return contains(finalPositionsOfAllMoves,kingLocation);
    }
    public boolean resultsInCheck(Position piecePosition, Position targetPosition, PieceColor sideColor) {
        return resultsInCheck(new Move(piecePosition, targetPosition), sideColor);
    }
    public boolean resultsInCheck(String piecePosition, String targetPosition, PieceColor sideColor) {
        return resultsInCheck(Position.chessPositionToPosition(piecePosition), Position.chessPositionToPosition(targetPosition), sideColor);
    }
    public void makeMove(Move move) {
        Position originPosition = move.origin;
        Position targetPosition = move.target;
        Square originSquare = this.getSquareAtPosition(originPosition);
        Square targetSquare = this.getSquareAtPosition(targetPosition);
        boolean isCapture = isCapture(originSquare, targetSquare);
        boolean isPawnMove = originSquare.pieceType == PieceType.Pawn;
        boolean isDoublePawnMove = isPawnMove && Math.abs(originPosition.row - targetPosition.row) == 2;
        if (!(isCapture || isPawnMove)) {
            this.incrementHalfMoveClock();
        }
        else {
            this.resetHalfMoveClock();
        }
        if (isDoublePawnMove) {
            if (originPosition.col != targetPosition.col) throw new IllegalStateException("Invalid move: Pawn move switches columns.");
            this.enPassantTargetSquare = Optional.of(new Position((originPosition.row + targetPosition.row) / 2, originPosition.col));
        }
        boolean isEnPassant = enPassantTargetSquare.isPresent() && targetPosition.equals(enPassantTargetSquare.get());
        this.getSquareAtPosition(targetPosition).pieceType = this.getSquareAtPosition(originPosition).pieceType;
        this.getSquareAtPosition(targetPosition).pieceColor = this.getSquareAtPosition(originPosition).pieceColor;
        this.getSquareAtPosition(originPosition).pieceType = PieceType.Empty;
        this.getSquareAtPosition(originPosition).pieceColor = Optional.empty();
        if (isEnPassant) {
            this.getSquareAtPosition(new Position(originPosition.row, targetPosition.col)).pieceType = PieceType.Empty;
            this.getSquareAtPosition(new Position(originPosition.row, targetPosition.col)).pieceColor = Optional.empty();
        }
        if (enPassantTargetSquare.isPresent() && !isDoublePawnMove) {
            enPassantTargetSquare = Optional.empty();
        }
        this.switchToMove();
        if (toMove == PieceColor.White) {
            this.incrementFullMoveNumber();
        }
    }

    private static boolean isCapture(Square pieceSquare, Square targetSquare) {
        Optional<PieceColor> pieceColor = pieceSquare.getPieceColor();
        if (pieceColor.isEmpty()) throw new IllegalStateException("Piece given to makeMove method has no color.");
        Optional<PieceColor> targetColor = targetSquare.getPieceColor();
        if (targetColor.isPresent() && targetColor.get() == pieceColor.get()) throw new IllegalStateException("Move given to makeMove method is invalid, as it involves a side capturing its own piece.");
        return targetColor.isPresent() && targetColor.get() == PieceColor.getOpposite(pieceColor.get());
    }

    public void makeMove(Position piecePosition, Position targetPosition) {
        makeMove(new Move(piecePosition, targetPosition));
    }
    public void makeMove(String piecePosition, String targetPosition) {
        makeMove(Position.chessPositionToPosition(piecePosition), Position.chessPositionToPosition(targetPosition));
    }

    public ArrayList<Position> getPositionsOfAllPieces(PieceColor sideColor) {
        ArrayList<Position> positionsOfAllPieces = new ArrayList<>();
        for (Square[] rank : board) {
            for (Square square : rank) {
                if (square.getPieceColor().isPresent() && square.getPieceColor().get() == sideColor) {
                    positionsOfAllPieces.add(square.getPosition());
                }
            }
        }
        return positionsOfAllPieces;
    }
    public HashSet<Move> getMovesOfAllPieces(PieceColor sideColor) {
        HashSet<Move> allPossibleMoves = new HashSet<>();
        for (Position position : getPositionsOfAllPieces(sideColor)) {
            allPossibleMoves.addAll(findMoves(position));
        }
        return allPossibleMoves;
    }
    private ArrayList<Move> getMovesForKingKnightAndPawn(Position pos) {
        Square initialPos = this.board[pos.row][pos.col];
        PieceType pieceType = initialPos.getPieceType();
        int[][] vectors = getPieceVectors(pieceType, pos);
        PieceColor thisPieceColor = initialPos.getPieceColor().orElseThrow();
        PieceColor oppositePieceColor = PieceColor.getOpposite(thisPieceColor);
        ArrayList<Move> possibleMoves = new ArrayList<>();
        for (int[] vec : vectors) {
            try {
                possibleMoves.add(new Move(pos, new Position(pos.row + vec[0], pos.col + vec[1])));
            }
            catch (IllegalArgumentException ignored) { }
        }
        for (int i = possibleMoves.size() - 1; i >= 0; i--) {
            Move move = possibleMoves.get(i);
            Square targetSquare = board[move.target.row][move.target.col];
            // Removes the move if it is either: out of bounds, or if the target square is occupied by a piece of its own kind.
            if (!Position.isInBounds(move.target) || (targetSquare.getPieceColor().orElse(oppositePieceColor) == thisPieceColor)) {
                possibleMoves.remove(i);
            }
        }
        return possibleMoves;
    }


    private ArrayList<Move> getMovesForQueenRookAndBishop(Position originPosition) {
        ArrayList<Move> possibleMoves = new ArrayList<>();
        Square originPositionSquare = this.board[originPosition.row][originPosition.col];
        PieceType originType = originPositionSquare.getPieceType();
        PieceColor thisPieceColor = originPositionSquare.getPieceColor().orElseThrow();
        PieceColor oppositePieceColor = PieceColor.getOpposite(thisPieceColor);
        int[][] vectors = getPieceVectors(originType, originPosition);
        for (int[] vec : vectors) {
            int row = originPosition.row;
            int col = originPosition.col;
            boolean squareIsValid = true;
            while (squareIsValid) {
                row += vec[0];
                col += vec[1];
                Position targetPosition;
                try {
                    targetPosition = new Position(row, col);
                }
                catch (IllegalArgumentException e) {
                    break;
                }
                Square targetSquare = board[row][col];
                // If the target square is empty, it is a valid place to move
                if (targetSquare.getPieceColor().isEmpty()) {
                    possibleMoves.add(new Move(originPosition, targetPosition));
                }
                // If the target square is the opposite side's piece, then it is valid but search needs to be stopped after it.
                else if (targetSquare.getPieceColor().get() == oppositePieceColor) {
                    possibleMoves.add(new Move(originPosition, targetPosition));
                    squareIsValid = false;
                }
                // If the target square is the current side's piece, then it is invalid and search needs to be stopped.
                else {
                    squareIsValid = false;
                }
            }
        }
        return possibleMoves;
    }
    public ArrayList<Move> findMoves(Position pos) {
        return switch (getSquareAtPosition(pos).getPieceType()) {
            case King, Knight, Pawn -> getMovesForKingKnightAndPawn(pos);
            case Queen, Rook, Bishop -> getMovesForQueenRookAndBishop(pos);
            case Empty -> throw new IllegalArgumentException("Empty position given to findMoves method, which requires an existent piece.");
        };
    }
    public ArrayList<Move> findMoves(String chessPosition) {
        return findMoves(Position.chessPositionToPosition(chessPosition));
    }

    public String boardWithPossibleMovesFor(String chessPosition) {
        return boardWithPossibleMovesFor(Position.chessPositionToPosition(chessPosition));
    }

    private int[][] getPieceVectors(PieceType pieceType, Position pos) {
        return switch (pieceType) {
            case King   ->  new int[][]{{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};      // All positions exactly 1 square away
            case Queen  ->  new int[][]{{-1, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, -1}, {-1, 1}, {1, -1}, {1, 1}};     // ITERATION Combination of both Rook and Bishop moves
            case Rook   ->  new int[][]{{-1, 0}, {1, 0}, {0, -1}, {0, 1}};                                         // ITERATION all positions in each four cardinal directions
            case Bishop ->  new int[][]{{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};                                        // ITERATION all positions in each four diagonal directions
            case Knight ->  new int[][]{{-1, -2}, {-2, -1}, {1, -2}, {2, -1}, {-1, 2}, {-2, 1}, {1, 2}, {2, 1}};    // All positions in the L-shape that the knight moves in
            case Pawn   ->  getPawnVectors(pos);
            case Empty  -> throw new IllegalStateException("Empty piece got through errors into getPieceVectors() function, should have been stopped earlier");
        };
    }

    public int[][] getPawnVectors(Position pos) {
        Square initialPos = this.board[pos.row][pos.col];
        PieceColor thisPieceColor = initialPos.getPieceColor().orElseThrow();
        ArrayList<Integer[]> vectors = new ArrayList<>();
        if (enPassantTargetSquare.isPresent()) {
            if (initialPos.getPieceColor().get() == PieceColor.White) {
                if (pos.row == enPassantTargetSquare.get().row - 1 && pos.col == enPassantTargetSquare.get().col - 1) {
                    vectors.add(new Integer[]{1, 1});
                }
                else if (pos.row == enPassantTargetSquare.get().row - 1 && pos.col == enPassantTargetSquare.get().col + 1) {
                    vectors.add(new Integer[]{1, -1});
                }
            }
            else {
                if (pos.row == enPassantTargetSquare.get().row + 1 && pos.col == enPassantTargetSquare.get().col - 1) {
                    vectors.add(new Integer[]{-1, 1});
                }
                else if (pos.row == enPassantTargetSquare.get().row + 1 && pos.col == enPassantTargetSquare.get().col + 1) {
                    vectors.add(new Integer[]{-1, -1});
                }
            }
        }
        Integer[][][] possibleCases =
               { {{1, 0},  {2, 0}},
                {{-1, 0}, {-2, 0}},
                { {1, 0}},
                {{-1, 0}},
               };
        if (thisPieceColor == PieceColor.White && pos.row == 1) {
            vectors.addAll(Arrays.asList(possibleCases[0]));
        }
        else if (thisPieceColor == PieceColor.Black && pos.row == 6) {
            vectors.addAll(Arrays.asList(possibleCases[1]));
        }
        else if (thisPieceColor == PieceColor.White) {
            vectors.addAll(Arrays.asList(possibleCases[2]));
        }
        else {
            vectors.addAll(Arrays.asList(possibleCases[3]));
        }
        int[][] returnVectors = new int[vectors.size()][2];
        for (int i = 0; i < vectors.size(); i++) {
            returnVectors[i][0] = vectors.get(i)[0];
            returnVectors[i][1] = vectors.get(i)[1];
        }
        return returnVectors;
    }
    public static boolean contains(HashSet<Position> list, Position value) {
        for (Position pos : list) {
            if (pos.equals(value)) {
                return true;
            }
        }
        return false;
    }

}
