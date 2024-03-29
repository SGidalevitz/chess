public enum PieceColor {
    White, Black;
    public static PieceColor getOpposite(PieceColor pieceColor) {
        return switch (pieceColor) {
            case White -> PieceColor.Black;
            case Black -> PieceColor.White;
        };
    }
}
