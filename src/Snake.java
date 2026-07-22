import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.LinkedList;
//this file contain the imp code.

public class Snake {

    private final LinkedList<Point> body;
    private Direction direction;
    private Direction pendingDirection; // buffers key input until next tick
    private boolean growPending;        // true when the snake should grow on next move

    public Snake(int startX, int startY) {
        body = new LinkedList<>();
        // Start with a snake of length 3, laid out horizontally
        body.add(new Point(startX, startY));
        body.add(new Point(startX - 1, startY));
        body.add(new Point(startX - 2, startY));

        direction = Direction.RIGHT;
        pendingDirection = Direction.RIGHT;
        growPending = false;
    }

    public void setDirection(Direction newDirection) {
        if (!newDirection.isOpposite(this.direction)) {
            pendingDirection = newDirection;
        }
    }

    /** Marks that the snake should grow by one segment on the next move. */
    public void grow() {
        growPending = true;
    }

    public void move() {
        direction = pendingDirection;
        Point head = body.getFirst();
        Point newHead = new Point(head.x + direction.dx, head.y + direction.dy);
        body.addFirst(newHead);

        if (growPending) {
            growPending = false; // consumed the growth for this move
        } else {
            body.removeLast();
        }
    }

    /** Returns true if the snake's head has collided with the arena walls. */
    public boolean hasHitWall(int gridWidth, int gridHeight) {
        Point head = body.getFirst();
        return head.x < 0 || head.x >= gridWidth || head.y < 0 || head.y >= gridHeight;
    }


    public boolean hasHitSelf() {
        Point head = body.getFirst();
        for (int i = 1; i < body.size(); i++) {
            if (body.get(i).equals(head)) {
                return true;
            }
        }
        return false;
    }

    /** Returns true if the given grid cell is currently occupied by the snake. */
    public boolean occupies(int x, int y) {
        for (Point p : body) {
            if (p.x == x && p.y == y) return true;
        }
        return false;
    }

    public Point getHead() {
        return body.getFirst();
    }

    public LinkedList<Point> getBody() {
        return body;
    }

    public int length() {
        return body.size();
    }

    public void draw(Graphics2D g2d, int cellSize) {
        int index = 0;
        for (Point p : body) {
            int px = p.x * cellSize;
            int py = p.y * cellSize;
            int arc = cellSize / 3;

            if (index == 0) {
                g2d.setColor(new Color(80, 250, 123)); // bright head
            } else {
                // Slight gradient down the body for visual polish
                int shade = Math.max(60, 200 - index * 2);
                g2d.setColor(new Color(46, shade > 200 ? 200 : shade, 90));
            }

            RoundRectangle2D segment = new RoundRectangle2D.Float(
                    px + 1, py + 1, cellSize - 2, cellSize - 2, arc, arc
            );
            g2d.fill(segment);
            index++;
        }
    }
}
