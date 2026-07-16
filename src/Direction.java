/**
 * Direction.java
 *
 * Represents the four possible directions the snake can move in.
 * Using an enum keeps direction handling type-safe and readable,
 * and lets us easily check for "opposite" directions to prevent
 * the snake from reversing directly into itself.
 */
public enum Direction {
    UP(0, -1),
    DOWN(0, 1),
    LEFT(-1, 0),
    RIGHT(1, 0);

    // How much this direction moves the snake head on the grid (in cells)
    public final int dx;
    public final int dy;

    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    /**
     * Returns true if this direction is the exact opposite of the given
     * direction. Used to stop the player from making the snake instantly
     * double back on itself (which would count as crashing into its own neck).
     */
    public boolean isOpposite(Direction other) {
        return this.dx == -other.dx && this.dy == -other.dy;
    }
}
