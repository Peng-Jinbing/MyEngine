package demo.step3;

import java.util.Random;

public class Screen extends BitMap {
	public Screen(int width, int height) {
		super(width, height);
	}

	public void render(Ticker ticker) {
		BitMap testGraphics = new BitMap(64,64);
		int[] testPixels = testGraphics.getPixels();
		Random  r =new Random();
		for(int i=0;i<64;i++){
			for(int j=0;j<64;j++){
				testPixels[i*64+j] = (int)(r.nextInt(255));
			}
		}
		this.draw(testGraphics,0,0);
		this.drawLine(10, 10, 50, 20, 255<<8);
	}

	public void draw(BitMap bitmap, int offX, int offY) {
		int bitmapWidth = bitmap.getWidth();
		int bitmapHeight = bitmap.getHeight();
		int[] bitmapPixels = bitmap.getPixels();

		int posX = 0, posY = 0;
		for (int i = 0; i < bitmapHeight; i++) {
			posY = offY + i;
			if (posY < 0 || posY >= super.height) {
				continue;
			}

			for (int j = 0; j < bitmapWidth; j++) {
				posX = offX + j;
				if (posX < 0 || posX >= super.width) {
					continue;
				}

				super.pixels[posY * width + posX] = bitmapPixels[i * bitmapWidth + j];
			}
		}
	}
	
	public void drawPixel(int posX, int posY, int color) {
		super.pixels[posY * width + posX] = color;
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

}
