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
		
		Line line = new Line(10, 10, 60, 80, 255<<8);
		line.draw(this);
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
}
