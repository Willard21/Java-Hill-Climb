import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Sprite {

	protected Vector2d vel;
	protected Vector2d pos;
	protected Vector2d acc;
	protected double mass;
	protected boolean visible = true;
	protected int w;
	protected int h;
	protected Image image;
	
	public static final double GRAVITY = 0.2;

	public Sprite(String imagePath) {
		mass = 1;
		pos = new Vector2d();
		vel = new Vector2d();
		acc = new Vector2d();
		loadImage(imagePath);
	}

	private void loadImage(String imagePath) {
		try {
			image = ImageIO.read(new File(imagePath)).getScaledInstance(60, -1, Image.SCALE_DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		w = image.getWidth(null);
		h = image.getHeight(null);
	}

	public void update() {
		acc.add(0, GRAVITY);
		vel.add(acc);
		pos.add(vel);
		acc.set(0, 0);
	}
	
	public void set(int x, int y) {
		pos.set(x, y);
		visible = true;
	}
	
	public void destroy() {
		pos.set(-1000, -1000);
		visible = false;
	}
	
	public void draw(Graphics g, int x, int y, ImageObserver io) {
		if (visible)
			g.drawImage(image, x, y, io);
	}
	
	public void draw(Graphics g, ImageObserver io) {
		draw(g, getX(), getY(), io);
	}
	
	public void applyForce(double x, double y) {
		acc.add(x / mass, y / mass);
	}
	
	public boolean colliding(Sprite o) {
		//AABB collision test
		return pos.x + w > o.pos.x && pos.x < o.pos.x + o.w &&
				pos.y + h > o.pos.y && pos.y < o.pos.y + o.h;
	}

	public int getX() {
		return (int)pos.x;
	}

	public int getY() {
		return (int)pos.y;
	}
	
	public int getWidth() {
		return w;
	}
	
	public int getHeight() {
		return h;
	}	

	public Image getImage() {
		return image;
	}
}