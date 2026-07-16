import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;

public class GamePanel extends JPanel implements ActionListener {

    static final int CELL_SIZE = 25;
    static final int GRID_WIDTH = 28;   // cells across
    static final int GRID_HEIGHT = 20;  // cells down
    static final int SCREEN_WIDTH = CELL_SIZE * GRID_WIDTH;
    static final int SCREEN_HEIGHT = CELL_SIZE * GRID_HEIGHT;

    static final int RENDER_FPS = 60;
    static final int RENDER_DELAY_MS = 1000 / RENDER_FPS;

    static final int BASE_MOVE_DELAY_MS = 130; // starting snake speed (lower = faster)
    static final int MIN_MOVE_DELAY_MS = 60;   // fastest the snake is allowed to go
    static final int SPEEDUP_PER_5_POINTS = 8; // ms shaved off per 5 points scored

    private static final String HIGH_SCORE_FILE = "highscore.txt";

    // ----- Game state machine -----
    private enum State { START_MENU, COUNTDOWN, PLAYING, PAUSED, GAME_OVER }
    private State state = State.START_MENU;

    private Snake snake;
    private Food food;
    private int score;
    private int highScore;

    private final Timer renderTimer;      // drives paint/repaint at 60 FPS
    private long lastMoveTime;            // last time the snake actually stepped
    private int currentMoveDelay;         // current ms between snake steps (speeds up over time)

    private long countdownStartTime;
    private static final int COUNTDOWN_SECONDS = 3;

    private final java.util.List<Particle> particles = new java.util.ArrayList<>();

    private final Font titleFont = new Font("Consolas", Font.BOLD, 46);
    private final Font bigFont = new Font("Consolas", Font.BOLD, 32);
    private final Font mediumFont = new Font("Consolas", Font.BOLD, 22);
    private final Font smallFont = new Font("Consolas", Font.PLAIN, 16);

    public GamePanel() {
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setBackground(new Color(18, 18, 24)); // dark background
        setFocusable(true);
        setDoubleBuffered(true); // reduces flickering

        highScore = loadHighScore();
        initGameObjects();

        addKeyListener(new InputHandler());

        renderTimer = new Timer(RENDER_DELAY_MS, this);
        renderTimer.start();
    }

    private void initGameObjects() {
        snake = new Snake(GRID_WIDTH / 2, GRID_HEIGHT / 2);
        food = new Food(GRID_WIDTH, GRID_HEIGHT, snake);
        score = 0;
        currentMoveDelay = BASE_MOVE_DELAY_MS;
        particles.clear();
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        switch (state) {
            case COUNTDOWN -> updateCountdown();
            case PLAYING -> updateGame();
            default -> { /* menu, paused, game over: no per-frame logic needed */ }
        }
        updateParticles();
        repaint(); // triggers paintComponent() — runs at full 60 FPS regardless of snake speed
    }


    private void updateCountdown() {
        long elapsed = System.currentTimeMillis() - countdownStartTime;
        if (elapsed >= (COUNTDOWN_SECONDS + 1) * 1000L) {
            state = State.PLAYING;
            lastMoveTime = System.currentTimeMillis();
        }
    }

    private void updateGame() {
        long now = System.currentTimeMillis();
        if (now - lastMoveTime < currentMoveDelay) {
            return; // not time to move yet
        }
        lastMoveTime = now;

        snake.move();

        // --- Collision checks ---
        if (snake.hasHitWall(GRID_WIDTH, GRID_HEIGHT) || snake.hasHitSelf()) {
            handleGameOver();
            return;
        }

        // --- Food check ---
        Point head = snake.getHead();
        if (head.x == food.getX() && head.y == food.getY()) {
            eatFood();
        }
    }
    private void eatFood() {
        int points = food.getValue();
        score += points;
        snake.grow();
        spawnParticles(food.getX(), food.getY(), food.isGolden());
        Toolkit.getDefaultToolkit().beep(); // simple built-in "eat" sound effect

        // Speed up every time the score crosses a multiple of 5
        int newDelay = BASE_MOVE_DELAY_MS - (score / 5) * SPEEDUP_PER_5_POINTS;
        currentMoveDelay = Math.max(MIN_MOVE_DELAY_MS, newDelay);

        food.respawn(GRID_WIDTH, GRID_HEIGHT, snake);
    }

    /** Called when the snake dies. Updates high score and switches state. */
    private void handleGameOver() {
        state = State.GAME_OVER;
        if (score > highScore) {
            highScore = score;
            saveHighScore(highScore);
        }
        // A distinct double-beep "you died" sound using the built-in beep
        Toolkit.getDefaultToolkit().beep();
    }


    private void spawnParticles(int gridX, int gridY, boolean golden) {
        int cx = gridX * CELL_SIZE + CELL_SIZE / 2;
        int cy = gridY * CELL_SIZE + CELL_SIZE / 2;
        Color color = golden ? new Color(255, 215, 0) : new Color(220, 50, 47);
        for (int i = 0; i < 10; i++) {
            particles.add(new Particle(cx, cy, color));
        }
    }

    private void updateParticles() {
        particles.removeIf(Particle::isDead);
        for (Particle p : particles) {
            p.update();
        }
    }


    private static class Particle {
        double x, y, vx, vy;
        int life = 20;
        final Color color;

        Particle(int x, int y, Color color) {
            this.x = x;
            this.y = y;
            this.color = color;
            double angle = Math.random() * Math.PI * 2;
            double speed = 1 + Math.random() * 2;
            vx = Math.cos(angle) * speed;
            vy = Math.sin(angle) * speed;
        }

        void update() {
            x += vx;
            y += vy;
            life--;
        }

        boolean isDead() {
            return life <= 0;
        }

        void draw(Graphics2D g2d) {
            float alpha = Math.max(0f, life / 20f);
            g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (alpha * 255)));
            g2d.fillOval((int) x - 2, (int) y - 2, 4, 4);
        }
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        drawGrid(g2d);

        switch (state) {
            case START_MENU -> drawStartMenu(g2d);
            case COUNTDOWN -> {
                drawGameplayLayer(g2d);
                drawCountdown(g2d);
            }
            case PLAYING -> drawGameplayLayer(g2d);
            case PAUSED -> {
                drawGameplayLayer(g2d);
                drawPauseOverlay(g2d);
            }
            case GAME_OVER -> {
                drawGameplayLayer(g2d);
                drawGameOverOverlay(g2d);
            }
        }
    }

    private void drawGrid(Graphics2D g2d) {
        g2d.setColor(new Color(255, 255, 255, 12));
        for (int x = 0; x <= GRID_WIDTH; x++) {
            g2d.drawLine(x * CELL_SIZE, 0, x * CELL_SIZE, SCREEN_HEIGHT);
        }
        for (int y = 0; y <= GRID_HEIGHT; y++) {
            g2d.drawLine(0, y * CELL_SIZE, SCREEN_WIDTH, y * CELL_SIZE);
        }
    }


    private void drawGameplayLayer(Graphics2D g2d) {
        food.draw(g2d, CELL_SIZE);
        snake.draw(g2d, CELL_SIZE);
        for (Particle p : particles) {
            p.draw(g2d);
        }
        drawHUD(g2d);
    }

    private void drawHUD(Graphics2D g2d) {
        g2d.setFont(mediumFont);
        g2d.setColor(Color.WHITE);
        g2d.drawString("Score: " + score, 15, 30);

        String hs = "High Score: " + highScore;
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(hs, SCREEN_WIDTH - fm.stringWidth(hs) - 15, 30);
    }

    private void drawStartMenu(Graphics2D g2d) {
        g2d.setColor(new Color(80, 250, 123));
        g2d.setFont(titleFont);
        centerString(g2d, "SNAKE", SCREEN_HEIGHT / 2 - 80);

        g2d.setColor(Color.WHITE);
        g2d.setFont(mediumFont);
        centerString(g2d, "Press ENTER or SPACE to Start", SCREEN_HEIGHT / 2 - 10);

        g2d.setFont(smallFont);
        g2d.setColor(new Color(200, 200, 200));
        centerString(g2d, "Move: Arrow Keys / WASD", SCREEN_HEIGHT / 2 + 40);
        centerString(g2d, "Pause: P    Restart: R    Exit: ESC", SCREEN_HEIGHT / 2 + 65);

        g2d.setColor(new Color(255, 215, 0));
        centerString(g2d, "High Score: " + highScore, SCREEN_HEIGHT / 2 + 110);

        // Small animated flourish: a pulsing apple icon
        long t = System.currentTimeMillis();
        int pulse = (int) (4 * Math.sin(t / 300.0));
        g2d.setColor(new Color(220, 50, 47));
        g2d.fillOval(SCREEN_WIDTH / 2 - 8, SCREEN_HEIGHT / 2 + 130 + pulse, 16, 16);
    }

    private void drawCountdown(Graphics2D g2d) {
        // Dim the board slightly behind the countdown number
        g2d.setColor(new Color(0, 0, 0, 120));
        g2d.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        long elapsed = System.currentTimeMillis() - countdownStartTime;
        int secondsLeft = COUNTDOWN_SECONDS - (int) (elapsed / 1000);

        String text = secondsLeft > 0 ? String.valueOf(secondsLeft) : "GO!";
        g2d.setColor(new Color(80, 250, 123));
        g2d.setFont(titleFont.deriveFont(64f));
        centerString(g2d, text, SCREEN_HEIGHT / 2);
    }

    private void drawPauseOverlay(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        g2d.setColor(Color.WHITE);
        g2d.setFont(bigFont);
        centerString(g2d, "PAUSED", SCREEN_HEIGHT / 2 - 10);

        g2d.setFont(smallFont);
        g2d.setColor(new Color(200, 200, 200));
        centerString(g2d, "Press P to Resume", SCREEN_HEIGHT / 2 + 25);
    }

    private void drawGameOverOverlay(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 170));
        g2d.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        g2d.setColor(new Color(220, 50, 47));
        g2d.setFont(bigFont);
        centerString(g2d, "GAME OVER", SCREEN_HEIGHT / 2 - 60);

        g2d.setColor(Color.WHITE);
        g2d.setFont(mediumFont);
        centerString(g2d, "Score: " + score, SCREEN_HEIGHT / 2 - 15);

        g2d.setColor(new Color(255, 215, 0));
        centerString(g2d, "High Score: " + highScore, SCREEN_HEIGHT / 2 + 20);

        g2d.setColor(new Color(200, 200, 200));
        g2d.setFont(smallFont);
        centerString(g2d, "Press R to Restart", SCREEN_HEIGHT / 2 + 60);
    }

    private void centerString(Graphics2D g2d, String text, int y) {
        FontMetrics fm = g2d.getFontMetrics();
        int x = (SCREEN_WIDTH - fm.stringWidth(text)) / 2;
        g2d.drawString(text, x, y);
    }
    private int loadHighScore() {
        try {
            Path path = Paths.get(HIGH_SCORE_FILE);
            if (Files.exists(path)) {
                String content = Files.readString(path).trim();
                if (!content.isEmpty()) {
                    return Integer.parseInt(content);
                }
            }
        } catch (IOException | NumberFormatException ex) {
          
            System.err.println("Could not load high score: " + ex.getMessage());
        }
        return 0;
    }

    private void saveHighScore(int value) {
        try {
            Files.writeString(Paths.get(HIGH_SCORE_FILE), String.valueOf(value));
        } catch (IOException ex) {
            System.err.println("Could not save high score: " + ex.getMessage());
        }
    }


    private class InputHandler extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();

            switch (state) {
                case START_MENU -> {
                    if (key == KeyEvent.VK_ENTER || key == KeyEvent.VK_SPACE) {
                        beginCountdown();
                    }
                }
                case COUNTDOWN -> {
             
                }
                case PLAYING -> handlePlayingInput(key);
                case PAUSED -> {
                    if (key == KeyEvent.VK_P) {
                        state = State.PLAYING;
                        lastMoveTime = System.currentTimeMillis(); // avoid a big jump after unpausing
                    }
                }
                case GAME_OVER -> {
                    if (key == KeyEvent.VK_R) {
                        initGameObjects();
                        beginCountdown();
                    }
                }
            }

            // Global keys available in (almost) every state
            if (key == KeyEvent.VK_ESCAPE) {
                System.exit(0);
            }
            if (key == KeyEvent.VK_R && state != State.GAME_OVER) {
                initGameObjects();
                state = State.START_MENU;
            }
        }

        private void handlePlayingInput(int key) {
            switch (key) {
                case KeyEvent.VK_UP, KeyEvent.VK_W -> snake.setDirection(Direction.UP);
                case KeyEvent.VK_DOWN, KeyEvent.VK_S -> snake.setDirection(Direction.DOWN);
                case KeyEvent.VK_LEFT, KeyEvent.VK_A -> snake.setDirection(Direction.LEFT);
                case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> snake.setDirection(Direction.RIGHT);
                case KeyEvent.VK_P -> state = State.PAUSED;
                default -> { /* ignore other keys */ }
            }
        }
    }

    private void beginCountdown() {
        state = State.COUNTDOWN;
        countdownStartTime = System.currentTimeMillis();
    }
}
