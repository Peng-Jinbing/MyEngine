package wormchase;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

public class Obstacles {

	private static final int BOX_LENGTH = 12;
	private ArrayList<Rectangle> boxes; // arraylist of Rectangle objects

	public Obstacles() {
		this.boxes = new ArrayList<>();
	}

	public int getNumObstacles() {
		return boxes.size();
	}

	synchronized public void add(int x, int y) {
		boxes.add(new Rectangle(x, y, BOX_LENGTH, BOX_LENGTH));
	}

	// draw a series of blue boxes
	synchronized public void draw(Graphics g) {
		Rectangle box;
		g.setColor(Color.blue);
		for (int i = 0; i < boxes.size(); i++) {
			box = boxes.get(i);
			g.fillRect(box.x, box.y, box.width, box.height);
		}
	} // end of draw( )

	synchronized public boolean hits(Point p, int size) {
		Rectangle r = new Rectangle(p.x, p.y, size, size);
		Rectangle box;
		for (int i = 0; i < boxes.size(); i++) {
			box = (Rectangle) boxes.get(i);
			if (box.intersects(r))
				return true;
		}
		return false;
	} // end of hits( )
}
