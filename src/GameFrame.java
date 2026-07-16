import javax.swing.*;


public class GameFrame extends JFrame {

    public GameFrame() {
        GamePanel panel = new GamePanel();

        this.add(panel);
        this.setTitle("Snake Game");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.pack(); // sizes the window to fit GamePanel's preferred size exactly
        this.setLocationRelativeTo(null); // center the window on screen
        panel.requestFocusInWindow();

        this.setVisible(true);
    }
}
