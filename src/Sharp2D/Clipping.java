package Sharp2D;

import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

public final class Clipping {
	private static final int INSIDE = 0;
	private static final int LEFT = 1;
	private static final int RIGHT = 2;
	private static final int BOTTOM = 4;
	private static final int TOP = 8;
	
	private double xMin;
	private double yMin;
	private double xMax;
	private double yMax;

	public Clipping() {
	}

	public Clipping(Rectangle2D clip) {
		setClip(clip);
	}

	public void setClip(Rectangle2D clip) {
		xMin = clip.getX();
		xMax = xMin + clip.getWidth();
		yMin = clip.getY();
		yMax = yMin + clip.getHeight();
	}

	private final int getRegionCode(double x, double y) {
		int xcode = x < xMin ? LEFT : x > xMax ? RIGHT : INSIDE;
		int ycode = y < yMin ? BOTTOM : y > yMax ? TOP : INSIDE;
		return xcode | ycode;
	}

	public boolean clip(Line2D.Float line) {
		double p1x = line.getX1(), p1y = line.getY1();
		double p2x = line.getX2(), p2y = line.getY2();
		double qx = 0d, qy = 0d;
		boolean vertical = p1x == p2x;
		double slope = vertical ? 0d : (p2y - p1y) / (p2x - p1x);

		int code1 = getRegionCode(p1x, p1y);
		int code2 = getRegionCode(p2x, p2y);

		while (true) {
			if(code1 == INSIDE & code2 == INSIDE){
				break;
			}			

			if ((code1 & code2) != INSIDE){
				return false;
			}

			int codeout = code1 == INSIDE ? code2 : code1;

			if ((codeout & LEFT) != INSIDE) {
				qx = xMin;
				qy = (qx - p1x) * slope + p1y;
			} else if ((codeout & RIGHT) != INSIDE) {
				qx = xMax;
				qy = (qx - p1x) * slope + p1y;
			} else if ((codeout & BOTTOM) != INSIDE) {
				qy = yMin;
				qx = vertical ? p1x : (qy - p1y) / slope + p1x;
			} else if ((codeout & TOP) != INSIDE) {
				qy = yMax;
				qx = vertical ? p1x : (qy - p1y) / slope + p1x;
			}

			if (codeout == code1) {
				p1x = qx;
				p1y = qy;
				code1 = getRegionCode(p1x, p1y);
			} else {
				p2x = qx;
				p2y = qy;
				code2 = getRegionCode(p2x, p2y);
			}
		}
		line.setLine(p1x, p1y, p2x, p2y);
		return true;
	}
}