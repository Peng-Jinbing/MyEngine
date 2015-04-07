package demo.step3;

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

	public void setArea(double xMin, double yMin, double xMax, double yMax) {
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
	}

	public boolean clipLine(Line line) {
		double p1x = line.getX1(), p1y = line.getY1();
		double p2x = line.getX2(), p2y = line.getY2();
		double qx = 0d, qy = 0d;
		boolean vertical = p1x == p2x;
		double slope = vertical ? 0d : (p2y - p1y) / (p2x - p1x);

		int code1 = getRegionCode(p1x, p1y);
		int code2 = getRegionCode(p2x, p2y);

		while (true) {
			if(code1 == INSIDE && code2 == INSIDE){
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
		line.setPosition1((int)p1x, (int)p1y);
		line.setPosition2((int)p2x, (int)p2y);
		return true;
	}
	
	private final int getRegionCode(double x, double y) {
		int xcode = x < xMin ? LEFT : x > xMax ? RIGHT : INSIDE;
		int ycode = y < yMin ? BOTTOM : y > yMax ? TOP : INSIDE;
		return xcode | ycode;
	}
}