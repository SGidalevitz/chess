import java.util.*;

public class Square {
    public Position position;
    public PieceType pieceType;
    public Optional<PieceColor> pieceColor;


    public Square(Position position, PieceType pieceType, PieceColor pieceColor) {
        this.position = position;
        this.pieceType = pieceType;
        this.pieceColor = Optional.of(pieceColor);
    }

    public Square(Position position) {
        this.position = position;
        this.pieceType = PieceType.Empty;
        this.pieceColor = Optional.empty();
    }
    public Square(Square other) {
        this.position = new Position(other.position);
        this.pieceType = other.pieceType;
        this.pieceColor = other.pieceColor;
    }

    public Position getPosition() {
        return this.position;
    }

    public PieceType getPieceType() {
        return this.pieceType;
    }

    public Optional<PieceColor> getPieceColor() {
        return this.pieceColor;
    }

    public String toString() {
        if (this.pieceColor.isEmpty()) return ".";
        if (this.pieceColor.get() == PieceColor.White) {
            return switch(pieceType) {
                case King -> "K";
                case Queen -> "Q";
                case Rook -> "R";
                case Bishop -> "B";
                case Knight -> "N";
                case Pawn -> "P";
                case Empty -> throw new IllegalStateException("Piece color is assigned, but piece type is empty.");
            };
        }
        else {
            return switch(pieceType) {
                case King -> "k";
                case Queen -> "q";
                case Rook -> "r";
                case Bishop -> "b";
                case Knight -> "n";
                case Pawn -> "p";
                case Empty -> throw new IllegalStateException("Piece color is assigned, but piece type is empty.");
            };
        }
    }

}
