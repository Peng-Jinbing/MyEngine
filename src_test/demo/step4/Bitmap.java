package demo.step4;

public class Bitmap {
	public int width;
	public int height;
	public int[] pixels;

	public Bitmap(int width, int height) {
		this.width = width;
		this.height = height;
		this.pixels = new int[width * height];
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int[] getPixels() {
		return pixels;
	}
	
	public void put(Bitmap bitmap, int posX, int posY, boolean transparent) {
		int bWidth = bitmap.getWidth();
		int bHeight =bitmap.getHeight();
		int[] bPixels = bitmap.getPixels();
		int bRowOffset = 0;
		
		int tWidth = this.getWidth();
		int tHeight = this.getHeight();
		int[] tPixels = this.getPixels();
		int tRowOffset = posY * tWidth;
		
		int px = 0;
		int py = 0;

		if (transparent) {
			for (int y = 0; y < bHeight; y++) {
				py = posY + y;
				if (py < 0 || py >= tHeight) {
					continue;
				}
				
				for (int x = 0; x < bWidth; x++) {
					px = posX + x;
					if (px < 0 || px > tWidth) {
						continue;
					}

					int bPixel = bPixels[bRowOffset + x];
					if (bPixel == 0XFFFFFFFF) {// white
						continue;
					} else {
						tPixels[tRowOffset + px] = bPixel;
					}
				}

				tRowOffset += tWidth;
				bRowOffset += bWidth;
			}
		} else {
			for (int y = 0; y < bHeight; y++) {
				py = posY + y;
				if (py < 0 || py >= tHeight) {
					continue;
				}

				for (int x = 0; x < bWidth; x++) {
					px = posX + x;
					if (px < 0 || px > tWidth) {
						continue;
					}

					tPixels[tRowOffset + px] = bPixels[bRowOffset + x];
				}
				tRowOffset += tWidth;
				bRowOffset += bWidth;
			}
		}
	}
	
	

	public void get(Bitmap bitmap, int posX, int posY) {
		int bWidth = bitmap.getWidth();
		int bHeight = bitmap.getHeight();
		int[] bPixels = bitmap.getPixels();
		int bRowOffset = 0;
		
		int tWidth = this.getWidth();
		int tHeight = this.getHeight();
		int[] tPixels = getPixels();
		int tRowOffset = posY * tWidth;

		int px = 0;
		int py = 0;

		for (int y = 0; y < bHeight; y++) {
			py = posY + y;
			if (py < 0 || py >= tHeight) {
				continue;
			}

			for (int x = 0; x < bWidth; x++) {
				px = posX + x;
				if (px < 0 || px >= tWidth) {
					continue;
				}

				bPixels[bRowOffset + x] = tPixels[tRowOffset + px];
			}
			
			bRowOffset += bWidth;
			tRowOffset += tWidth;
		}
	}
}