import javax.swing.SwingUtilities;

/**
 * Main.java
 *
 * Entry point of the application. All Swing UI code should be created
 * and manipulated on the Event Dispatch Thread (EDT), which is why we
 * wrap the frame creation in SwingUtilities.invokeLater(). This avoids
 * subtle threading bugs that can cause flickering or unresponsive UI.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(GameFrame::new);
    }
}
