import java.awt.EventQueue;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

public class Game extends JFrame {

	public static final int WIDTH = 800;
	public static final int HEIGHT = 600;
	public static boolean[] keys = new boolean[128];
	public static Game game;
	public static int highScore = 0;
	
	public Game() {
		initUI();
	}
	
	private void initUI() {
		add(new Board());

		setTitle("Road Runner");
		setSize(WIDTH, HEIGHT);
		
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	/**
	 * Closes the application
	 */
	public static void close() {
		game.dispatchEvent(new WindowEvent(game, WindowEvent.WINDOW_CLOSING));
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			game = new Game();
			game.setVisible(true);
			Menu.init();
		});
	}
}