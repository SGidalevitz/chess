public class Position {
    public int row;
    public int col;
    public Position(int row, int col) throws IllegalArgumentException {
        String exceptionMsg = getExceptionMessageIfExists(row, col);
        if (exceptionMsg != null) throw new IllegalArgumentException(exceptionMsg);
        this.row = row;
        this.col = col;
    }
    public String toString() {
        return "(" + row + ", " + col + ")";
    }

    /*
    Method Tested in PositionTest.java
    Description: This method takes in a Position with the proper zero-indexed coordinates, and returns a String, which is a standard position in chess notation, e.g. (a-h)(1-8).
    Parameters: chessPosition(String) -> the position to check
    Returns: a Position with the proper coordinates translated into a zero index.
    Examples: refer to test method.
    Notes: Position is checked for proper bounds in the constructor, so the checking of bounds for this method is unnecessary.
     */
    public static String positionToChessPosition(Position position) {
        char rank = (char)('a' + position.row);
        char file = (char)('1' + position.col);
        return "" + rank + file;
    }

    /*
    Method Tested in PositionTest.java
    Description: This method takes in a String, which is a standard position in chess notation, e.g. (a-h)(1-8), and returns a Position with the proper coordinates translated for this program.
    Parameters: chessPosition(String) -> the position to check
    Returns: a Position with the proper coordinates translated into a zero index.
    Examples: refer to test method.
     */
    public static Position chessPositionToPosition(String chessPosition) {
        boolean isProperLength = chessPosition.length() == 2;
        if (!isProperLength) throw new IllegalArgumentException("Invalid argument: chessPosition must be a string of length 2.");
        int properRow = chessPosition.charAt(0) - 'a';
        int properCol = chessPosition.charAt(1) - '1';
        String exceptionMsg = getExceptionMessageIfExists(properRow, properCol);
        if (exceptionMsg != null) throw new IllegalArgumentException(exceptionMsg);
        return new Position(properRow, properCol);
    }

    public static String getExceptionMessageIfExists(int row, int col) {
        // Both properRow and properCol should be in the range [0,8).
        boolean rowOutOfBounds = row < 0 || row >= 8;
        boolean colOutOfBounds = col < 0 || col >= 8;
        String exceptionMsg = null;
        if (rowOutOfBounds && colOutOfBounds) {
            exceptionMsg = "Both row and column are out of bounds.";
        } else if (rowOutOfBounds) {
            exceptionMsg = "Row is out of bounds.";
        } else if (colOutOfBounds) {
            exceptionMsg = "Column is out of bounds.";
        }
        return exceptionMsg;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Position other)) return false;
        return this.row == other.row && this.col == other.col;
    }


}
