import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Board extends JPanel implements ActionListener, Runnable {
	private Timer timer;
	private Sprite upgrade;
	private Player player;
	private final int DELAY = 16;
	public static final int FLOOR = Game.HEIGHT - 27;
	public static int[] floor = new int[Game.WIDTH + 1];
	public static boolean paused = true;
	public static int score = 0;
	public static Board board;
	
	public Board() {
		board = this;
		initBoard();
	}
	
	private void initBoard() {
		addKeyListener(new KeyListener());
		setBackground(Color.black);
		setFocusable(true);
		
		player = new Player("red-car.png");
		load();
		upgrade = new Sprite("upgrade.png");
		upgrade.destroy();
		genFloor(player.getX() - 100);
		
		timer = new Timer(DELAY, this);
		timer.start();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		draw(g);
		Toolkit.getDefaultToolkit().sync();
		repaint();
	}
	
	private void textSize(Graphics g, float size) {
		Font currentFont = g.getFont();
		Font newFont = currentFont.deriveFont(size);
		g.setFont(newFont);
	}
	
	private void drawString(Graphics g, String text, int x, int y) {
		String[] lines = text.split("\n");
		for (int i = 0; i < lines.length; i++) {
			g.drawString(lines[i], x, y);
			y += g.getFontMetrics().getHeight();
		}
	}
	
	private void drawPauseScreen(Graphics g) {
		g.setColor(Color.WHITE);
		textSize(g, 12);
		g.drawString("Score: " + score, 20, 40);
		g.drawString("Fuel: " + player.getFuel() + "%", 20, 60);
		g.drawString("High Score: " + Game.highScore, 20, 80);
		
		textSize(g, 30);
		player.stats(g, 20, 150, 30);
		
		g.drawString("Controls:", 20, 330);
		textSize(g, 20);
		drawString(g, "Left Arrow: Move Backwards\n" +
					  "Right Arrow: Move Forward\n" +
					  "Up Arrow: Bounce\n" +
					  "Esc: Pause/Unpause", 20, 350);
		
		String howtoplay = 
			"To start playing, unpause the game.\n" +
			"Your objective is to travel as far as possible.\n" +
			"You have a limited amount of fuel.\n" +
			"Collect upgrades along the way to\n" +
			"help your progress. Use the Menu\n" +
			"button on the top left to save your\n" +
			"progress.";
		textSize(g, 30);
		g.drawString("How To Play:", 300, 70);
		textSize(g, 20);
		drawString(g, howtoplay, 300, 100);
	}
	
	private void drawGame(Graphics g) {
		g.setColor(Color.WHITE);
		
		for (int x = 0; x < Game.WIDTH; x++) {
			g.drawLine(x, floor[x], x+1, floor[x+1]);
		}
		
		upgrade.draw(g, upgrade.getX() - player.getX() + player.getWidth() + upgrade.getWidth() / 2, upgrade.getY(), this);
		
		g.drawString("Score: " + score, 20, 40);
		g.drawString("Fuel: " + player.getFuel() + "%", 20, 60);
		g.drawString("High Score: " + Game.highScore, 20, 80);
		
		player.draw(g, this);
	}

	private void draw(Graphics g) {
		if (paused) {
			drawPauseScreen(g);
		} else {
			drawGame(g);
		}
	}
	
	public void save() {
		System.out.println("Saving data.");
		FileWriter out = null;
		
		try {
			out = new FileWriter("save.txt");
			out.write(player.toString() + "\n" + Game.highScore);
		} catch(Exception e){
			System.out.println("Unable to save game.");
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void load() {
		FileReader in = null;
		System.out.println("Loading data.");
		
		try {
			in = new FileReader("save.txt");
			int c;
			String data = "";
			
			while ((c = in.read()) != -1) {
				data += (char) c;
			}
			
			String[] lines = data.split("\n");
			
			if (lines.length > 1) {
				player.fromString(lines[0]);
				Game.highScore = Integer.parseInt(lines[1]);
				paused = true;
			} else {
				System.out.println("Unable to load save.");
			}
		} catch(Exception e){
			System.out.println("Unable to load save.");
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		//Start a new thread to update positions and such
		Thread t = new Thread(board);
        t.start();
	}
	
	private void genFloor(int x) {
		double smoothness = 500;
		double rshift = 0.0128975331423; //Just random decimal
		int maxHeight = 100;
		for (int i = 0; i < floor.length; i++) {
			floor[i] = FLOOR - (int)(ImprovedNoise.noise((x + i) / smoothness + rshift) * maxHeight) - maxHeight;
		}
	}

	private void step() {
		if (paused) return;
		
		int prevX = player.getX();
		player.update();
		if (prevX != player.getX()) {
			genFloor(player.getX() - 100);
			if (Math.random() < 0.0001 * (player.getX() - prevX) && player.getX() - 1000 > upgrade.getX()) {
				upgrade.set(player.getX() + Game.WIDTH, (int) (Math.random() * Game.HEIGHT / 2 + Game.HEIGHT / 4));
				System.out.println("Upgrade found");
			}
		}
		
		if (upgrade.colliding(player)) {
			String up = player.upgrade();
			upgrade.destroy();
			System.out.println("Upgraded " + up); //Add some kind of notification in-game.
		}
		
		score = player.getX() / 50;
		
		if (player.brokenDown()) {
			upgrade.destroy();
			Game.highScore = Math.max(Game.highScore, score);
			player.restart();
			genFloor(player.getX() - 100);
		}
	}

	private class KeyListener extends KeyAdapter {
		@Override
		public void keyReleased(KeyEvent e) {
			int key = e.getKeyCode();
			if (key < Game.keys.length)
				Game.keys[key] = false;
		
			if (key == KeyEvent.VK_ESCAPE) {
				paused ^= true;
				Menu.pauseButton.setText(paused ? "Unpause" : "Pause");
			}
		}

		@Override
		public void keyPressed(KeyEvent e) {
			int key = e.getKeyCode();
			if (key < Game.keys.length)
				Game.keys[key] = true;
		}
	}

	public void newGame() {
		player = new Player("red-car.png");
		upgrade.destroy();
		genFloor(player.getX() - 100);
		Game.highScore = 0;
	}

	@Override
	public void run() {
		step();
	}
}