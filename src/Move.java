public class Move {
    public Position origin;
    public Position target;
    public Move(Position origin, Position target) {
        this.origin = origin;
        this.target = target;
    }
    public Move(String origin, String target) {
        this.origin = Position.chessPositionToPosition(origin);
        this.target = Position.chessPositionToPosition(target);
    }
    public String toString() {
        return Position.positionToChessPosition(this.origin) + Position.positionToChessPosition(this.target);
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Move other)) return false;
        return this.origin.equals(other.origin) && this.target.equals(other.target);
    }
}
