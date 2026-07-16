import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.Random;

/**
 * Food.java
 *
 * Represents a piece of food on the grid. Most food is a normal red
 * "apple" worth 1 point. Occasionally a golden apple spawns, worth
 * 5 points, as a bonus/extra feature.
 */
public class Food {

    private int x, y;
    private boolean golden;
    private final Random random = new Random();

    private static final double GOLDEN_CHANCE = 0.12; // 12% chance of golden food

    public Food(int gridWidth, int gridHeight, Snake snake) {
        respawn(gridWidth, gridHeight, snake);
    }

    /**
     * Picks a new random position for the food that does not overlap
     * the snake's body. Also rolls the dice on whether this food is
     * a rare golden apple.
     */
    public void respawn(int gridWidth, int gridHeight, Snake snake) {
        Point newPos;
        do {
            int nx = random.nextInt(gridWidth);
            int ny = random.nextInt(gridHeight);
            newPos = new Point(nx, ny);
        } while (snake.occupies(newPos.x, newPos.y));

        this.x = newPos.x;
        this.y = newPos.y;
        this.golden = random.nextDouble() < GOLDEN_CHANCE;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isGolden() {
        return golden;
    }

    /** Points awarded for eating this food. */
    public int getValue() {
        return golden ? 5 : 1;
    }

    /**
     * Draws the food as a circle (apple). Golden food is drawn in
     * gold with a subtle glow effect to make it stand out.
     */
    public void draw(Graphics2D g2d, int cellSize) {
        int px = x * cellSize;
        int py = y * cellSize;

        if (golden) {
            // Soft glow behind the golden apple
            g2d.setColor(new Color(255, 215, 0, 80));
            g2d.fill(new Ellipse2D.Float(px - 3, py - 3, cellSize + 6, cellSize + 6));
            g2d.setColor(new Color(255, 215, 0));
        } else {
            g2d.setColor(new Color(220, 50, 47));
        }

        g2d.fill(new Ellipse2D.Float(px + 2, py + 2, cellSize - 4, cellSize - 4));

        // Little "stem" highlight for a more apple-like look
        g2d.setColor(new Color(255, 255, 255, 90));
        g2d.fillOval(px + cellSize / 3, py + cellSize / 4, cellSize / 5, cellSize / 5);
    }
}
