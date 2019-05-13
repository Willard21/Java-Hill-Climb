import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.ImageObserver;

public class Player extends Sprite {

	private boolean onGround = false;
	public double gas = 100;
	private int suspension = 0;
	private double[] suspensionLevels = {0.9, 0.8, 0.7, 0.6, 0.5, 0.45, 0.4, 0.35, 0.3, 0.25};
	private int speed = 0;
	private double[] speedLevels = {1, 1.25, 1.5, 1.75, 2, 2.35, 2.7, 3, 3.5, 4};
	private int tank = 0;
	private double[] tankLevels = {1, 1.25, 1.5, 1.75, 2, 2.35, 2.7, 3, 3.5, 4};
	private int engine = 0;
	private double[] engineLevels = {0.5, 0.6, 0.7, 0.8, 0.9, 1.0, 1.1, 1.2, 1.3, 1.4};
	
	public Player(String imagePath) {
		super(imagePath);
	}
	
	public String upgrade() {
		//Pick a random stat to upgrade.
		double rand = Math.random();
		if (rand < 0.25 && suspension < 9) {
			suspension++;
			return "Suspension";
		} else if (rand < 0.5 && speed < 9) {
			speed++;
			return "Speed";
		} else if (rand < 0.75 && tank < 9) {
			tank++;
			return "Gas Tank";
		} else if (rand < 1 && engine < 9) {
			engine++;
			return "Engine";
		} else {
			return "Nothing";
		}
	}
	
	public void stats(Graphics g, int x, int y, int h) {
		g.drawString("Suspension: " + (suspension + 1), x, y);
		g.drawString("Speed: " + (speed + 1), x, y + h);
		g.drawString("Gas Tank: " + (tank + 1), x, y + h*2);
		g.drawString("Engine: " + (engine + 1), x, y + h*3);
	}
	
	public boolean brokenDown() {
		return gas <= 0.0 && vel.magSqr() < 1 && onGround;
	}
	
	public void restart() {
		pos.set(0, Game.HEIGHT / 2);
		vel.set(0, 0);
		acc.set(0, 0);
		gas = 100.0;
	}
	
	public double getFuel() {
		return Math.ceil(gas*10)/10;
	}
	
	@Override
	public void draw(Graphics g, ImageObserver io) {
		g.drawImage(image, 100, getY(), io);
	}
	
	public void update() {
		boolean wasOnGround = onGround;
		onGround = false;
		double prevY = pos.y;
		super.update();
		int floor = Board.floor[100 + w/2];
		if (pos.y >= floor - h) {
			if (pos.y > prevY && !wasOnGround) {
				vel.y = -Math.abs(vel.y) * suspensionLevels[suspension];
				pos.y = floor - h;
				if (vel.y > -2) {
					vel.y = 0;
				}
			} else {
				pos.y = floor - h;
				vel.y = Math.min(pos.y - prevY, vel.y);
			}
			
			pos.y += vel.y;
			vel.x *= 0.98;
			vel.y *= 0.8;
			onGround = true;
			
			double eng = engineLevels[engine];
			if (Game.keys[KeyEvent.VK_LEFT] && gas > 0) {
				acc.x += -eng;
			}
			if (Game.keys[KeyEvent.VK_RIGHT] && gas > 0) {
				acc.x += eng;
			}
			if (Game.keys[KeyEvent.VK_UP] && gas > 0) {
				vel.y = -10 * eng;
				gas -= 10 / tankLevels[tank];
			}
		}
		double gasUse = 1.0 / 50 / tankLevels[tank];
		if (Game.keys[KeyEvent.VK_LEFT] && gas > 0) {
			gas -= gasUse;
		}
		if (Game.keys[KeyEvent.VK_RIGHT] && gas > 0) {
			gas -= gasUse;
		}
		vel.limit(10 * speedLevels[speed]);
		if (gas < 0) {
			gas = 0;
		}
	}

	@Override
	public String toString() {
		return pos.toString() + ";" + vel.toString() + ";" + gas + ";" + suspension + ";" + speed + ";" + tank + ";" + engine;
	}
	
	public void fromString(String data) {
		String[] vars = data.split(";");
		if (vars.length != 7) {
			System.out.println("Unable to load Player data.");
			return;
		}
		String[] Pos = vars[0].split(",");
		String[] Vel = vars[1].split(",");
		
		pos.set(Double.parseDouble(Pos[0]), Double.parseDouble(Pos[1]));
		vel.set(Double.parseDouble(Vel[0]), Double.parseDouble(Vel[1]));
		gas = Double.parseDouble(vars[2]);
		suspension = Integer.parseInt(vars[3]);
		speed = Integer.parseInt(vars[4]);
		tank = Integer.parseInt(vars[5]);
		engine = Integer.parseInt(vars[6]);
	}
}
