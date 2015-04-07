package Sharp2D;

public class Line {
	public void drawLineRaw(int x, int y, int x2, int y2, int color) {
		int w = x2 - x;
		int h = y2 - y;
		double m = h / (double) w;
		double j = y;
		for (int i = x; i <= x2; i++) {
			drawPixel(i, (int) j, color);
			j += m;
		}
	}

	public void drawIn2ndOctant(int x, int y, int x2, int y2, int color) {
		int w = x2 - x;
		int h = y2 - y;
		int dy1 = -1;
		int fastStep = Math.abs(w);
		int slowStep = Math.abs(h);
		int numerator = fastStep >> 1;
		for (int i = 0; i <= fastStep; i++) {
			drawPixel(x, y, color);
			numerator += slowStep;
			if (!(numerator < fastStep)) {
				numerator -= fastStep;
				y += dy1;
			}
			x++;
		}
	}

	public void drawIn3rdOctant(int x, int y, int x2, int y2, int color) {
		int w = x2 - x;
		int h = y2 - y;
		int dy1 = 1;
		int fastStep = Math.abs(w);
		int slowStep = Math.abs(h);
		int numerator = fastStep >> 1;
		for (int i = 0; i <= fastStep; i++) {
			drawPixel(x, y, color);
			numerator += slowStep;
			if (!(numerator < fastStep)) {
				numerator -= fastStep;
				y += dy1;
			}
			x++;
		}
	}

	public void drawIn6thOctant(int x, int y, int x2, int y2, int color) {
		int w = x2 - x;
		int h = y2 - y;
		int dy1 = 1;
		int fastStep = Math.abs(w);
		int slowStep = Math.abs(h);
		int numerator = fastStep >> 1;
		for (int i = 0; i <= fastStep; i++) {
			drawPixel(x, y, color);
			numerator += slowStep;
			if (!(numerator < fastStep)) {
				numerator -= fastStep;
				y += dy1;
			}
			x--;
		}
	}

	public void drawIn7thOctant(int x, int y, int x2, int y2, int color) {
		int w = x2 - x;
		int h = y2 - y;
		int dy1 = -1;
		int fastStep = Math.abs(w);
		int slowStep = Math.abs(h);
		int numerator = fastStep >> 1;
		for (int i = 0; i <= fastStep; i++) {
			drawPixel(x, y, color);
			numerator += slowStep;
			if (!(numerator < fastStep)) {
				numerator -= fastStep;
				y += dy1;
			}
			x--;
		}
	}

	public void drawInEvenOctant(int x, int y, int x2, int y2, int color) {
		int w = x2 - x;
		int h = y2 - y;
		int dx1 = w < 0 ? -1 : (w > 0 ? 1 : 0);
		int dy1 = h < 0 ? -1 : (h > 0 ? 1 : 0);
		int dx2 = 0;
		int dy2 = dx2 = w < 0 ? -1 : (w > 0 ? 1 : 0);

		int fastStep = Math.abs(w);
		int slowStep = Math.abs(h);
		int numerator = fastStep >> 1;
		for (int i = 0; i <= fastStep; i++) {
			drawPixel(x, y, color);
			numerator += slowStep;
			if (!(numerator < fastStep)) {
				numerator -= fastStep;
				x += dx1;
				y += dy1;
			} else {
				x += dx2;
				y += dy2;
			}
		}
	}

	public void drawLine(int x0, int y0, int x1, int y1, int color) {
		int x = x0;
		int y = y0;

		int w = x1 - x0;
		int h = y1 - y0;

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
			drawPixel(x, y, color);
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

	public void drawPixel(int x, int y, int color) {

	}

}