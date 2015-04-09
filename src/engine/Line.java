package engine;

public class Line {
	private int x1;
	private int y1;
	private int x2;
	private int y2;
	private int color;

	public Line(int x1, int y1, int x2, int y2, int color) {
		this.setPosition1(x1, y1);
		this.setPosition2(x2, y2);
		this.setColor(color);
	}

	public void draw(Screen sreen) {
		int[] screenPixels = sreen.getPixels();
		int screenWidth = sreen.getWidth();

		int x = x1;
		int y = y1;

		int w = x2 - x1;
		int h = y2 - y1;

		int dx1 = w < 0 ? -1 : (w > 0 ? 1 : 0);
		int dy1 = h < 0 ? -1 : (h > 0 ? 1 : 0);

		int dx2 = w < 0 ? -1 : (w > 0 ? 1 : 0);
		int dy2 = 0;

		int fastStep = Math.abs(w);
		int slowStep = Math.abs(h);
		if (fastStep <= slowStep) {
			fastStep = Math.abs(h);
			slowStep = Math.abs(w);

			dx2 = 0;
			dy2 = h < 0 ? -1 : (h > 0 ? 1 : 0);
		}

		int numerator = fastStep >> 1;

		for (int i = 0; i <= fastStep; i++) {
			screenPixels[y * screenWidth + x] = color;
			numerator += slowStep;
			if (numerator >= fastStep) {
				numerator -= fastStep;
				x += dx1;
				y += dy1;
			} else {
				x += dx2;
				y += dy2;
			}
		}
	}

	public void setPosition1(int x1, int y1) {
		this.x1 = x1;
		this.y1 = y1;
	}

	public void setPosition2(int x2, int y2) {
		this.x2 = x2;
		this.y2 = y2;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public int getX1() {
		return x1;
	}

	public int getY1() {
		return y1;
	}

	public int getX2() {
		return x2;
	}

	public int getY2() {
		return y2;
	}

	public int getColor() {
		return color;
	}
}
