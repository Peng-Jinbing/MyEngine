package demo.step2;


public class Screen extends BitMap {
	public Screen(int width, int height) {
		super(width, height);
	}

	public void render(Ticker ticker, BitMap bitMap) {
		this.putBitMap(bitMap, 0, 0);
	}

	public void putBitMap(BitMap bitmap, int offX, int offY) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		int[] p = bitmap.getPixels();
		int[] screenPixels = super.pixels;

		int posX = 0, posY = 0;
		for (int y = 0; y < h; y++) {
			posY = offY + y;
			if (posY < 0 || posY >= super.height) {
				continue;
			}

			int screenRowOffset = posY * width;
			int bitmapRowOffset = y * w;

			for (int x = 0; x < w; x++) {
				posX = offX + x;
				if (posX < 0 || posX >= super.width) {
					continue;
				}

				screenPixels[screenRowOffset + posX] = p[bitmapRowOffset + x];
			}
		}
	}
	
	public void getBitMap(BitMap bitmap, int offX, int offY) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		int[] bitMapPixels = bitmap.getPixels();
		int[] screenPixels = super.pixels;

		int posX = 0, posY = 0;
		for (int y = 0; y < h; y++) {
			posY = offY + y;
			if (posY < 0 || posY >= super.height) {
				continue;
			}

			int screenRowOffset = posY * width;
			int bitmapRowOffset = y * w;

			for (int x = 0; x < w; x++) {
				posX = offX + x;
				if (posX < 0 || posX >= super.width) {
					continue;
				}

				bitMapPixels[bitmapRowOffset + x] = screenPixels[screenRowOffset + posX];
			}
		}
	}
}
