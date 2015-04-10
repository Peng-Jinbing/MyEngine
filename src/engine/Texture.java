package engine;

public class Texture {

	protected int width;
	protected int height;
	protected int[] pixels;

	public Texture(int width, int height) {
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

	public void draw(Texture texture, int offX, int offY) {
		int textureWidth = texture.getWidth();
		int textureHeight = texture.getHeight();
		int[] texturePixels = texture.getPixels();

		int posX = 0, posY = 0;
		for (int i = 0; i < textureHeight; i++) {
			posY = offY + i;
			if (posY < 0 || posY >= height) {
				continue;
			}

			for (int j = 0; j < textureWidth; j++) {
				posX = offX + j;
				if (posX < 0 || posX >= width) {
					continue;
				}

				int srcPixel = texturePixels[i * textureWidth + j];
				if (srcPixel > 0) {
					pixels[posY * width + posX] = srcPixel;
				}
			}
		}
	}

}
