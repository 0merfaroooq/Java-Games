import javax.swing.*;

/**
 * GameFrame.java
 *
 * The top-level window (JFrame) that hosts the GamePanel.
 * Keeping this separate from GamePanel follows good OOP practice:
 * GameFrame is only responsible for window setup, while GamePanel
 * is responsible for the actual game logic and rendering.
 */
public class GameFrame extends JFrame {

    public GameFrame() {
        GamePanel panel = new GamePanel();

        this.add(panel);
        this.setTitle("Snake Game");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.pack(); // sizes the window to fit GamePanel's preferred size exactly
        this.setLocationRelativeTo(null); // center the window on screen

        // Ensure the panel has keyboard focus as soon as the window opens
        panel.requestFocusInWindow();

        this.setVisible(true);
    }
}
