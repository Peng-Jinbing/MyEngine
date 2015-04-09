package engine;

import java.util.Random;

public class Screen extends Texture {
	public Screen(int width, int height) {
		super(width, height);
	}

	int time=0;
	public void render(Ticker ticker) {
		//clear(255<<16|255<<8|255);
		clear(0);
		this.time++;
		
		int textureWidth = 64;
		int textureHeight = 64;
		int x1 = (this.width - textureWidth) / 2;
		int y1 = (this.height - textureHeight) / 2;
		int x2 = x1 + textureWidth - 1;
		int y2 = y1 + textureHeight - 1;

		Texture testGraphics = new Texture(textureWidth, textureHeight);
		int[] testPixels = testGraphics.getPixels();
		Random r = new Random();
		for (int i = 0; i < textureHeight*textureWidth; i++) {
			testPixels[i] = r.nextInt()* ((int)(r.nextInt(5)/4));
		}

		
		for(int i=0;i<100;i++){
			int xd = (int) (Math.sin(time) * 80);
			int yd = (int) (Math.cos(time) * 80);
			this.draw(testGraphics, x1 + xd, y1 + yd);
		}
		

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

				int srcPixel = texturePixels[i * textureWidth + j];
				if(srcPixel>0){
					super.pixels[posY * width + posX] = srcPixel;
				}
			}
		}
	}
	
	
	public void clear(int color){
		for (int i = 0; i < width * height; i++) {
			this.pixels[i] = color;
		}
	}	
}
