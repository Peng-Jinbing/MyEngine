package engine;

import java.util.Random;

public class Screen extends Texture {
	public Screen(int width, int height) {
		super(width, height);
	}

	public void render(Ticker ticker) {
		// int textureWidth = 160;
		// int textureHeight = 120 ;
		int textureWidth = 64;
		int textureHeight = 64;

		Texture testGraphics = new Texture(textureWidth, textureHeight);
		int[] testPixels = testGraphics.getPixels();
		Random r = new Random();
		for (int i = 0; i < textureHeight; i++) {
			for (int j = 0; j < textureWidth; j++) {
				testPixels[i * textureWidth + j] = r.nextInt();
			}
		}

		int x1 = (this.width - textureWidth) / 2;
		int y1 = (this.height - textureHeight) / 2;
		int x2 = x1 + textureWidth - 1;
		int y2 = y1 + textureHeight - 1;

		int speedFactor = 2000;

		int xd = (int) (Math.sin(System.currentTimeMillis() % speedFactor / Double.valueOf(speedFactor) * Math.PI * 2) * 40);
		int yd = (int) (Math.cos(System.currentTimeMillis() % speedFactor / Double.valueOf(speedFactor) * Math.PI * 2) * 40);

		this.draw(testGraphics, x1 + xd, y1 + yd);

		Line lineLeft = new Line(x1, y1, x1, y2, 255 << 8);
		Line lineBottom = new Line(x1, y2, x2, y2, 255 << 8);
		Line lineRigth = new Line(x2, y1, x2, y2, 255 << 8);
		Line lineTop = new Line(x1, y1, x2, y1, 255 << 8);

		lineLeft.draw(this);
		lineBottom.draw(this);
		lineRigth.draw(this);
		lineTop.draw(this);

		// Clipping clip = new Clipping();
		// clip.setArea(66, 66, 88, 88);
		// clip.clipLine(line);

	}

	public void draw(Texture texture, int offX, int offY) {
		int textureWidth = texture.getWidth();
		int textureHeight = texture.getHeight();
		int[] texturePixels = texture.getPixels();

		int posX = 0, posY = 0;
		for (int i = 0; i < textureHeight; i++) {
			posY = offY + i;
			if (posY < 0 || posY >= super.height) {
				continue;
			}

			for (int j = 0; j < textureWidth; j++) {
				posX = offX + j;
				if (posX < 0 || posX >= super.width) {
					continue;
				}

				super.pixels[posY * width + posX] = texturePixels[i * textureWidth + j];
			}
		}
	}
}
