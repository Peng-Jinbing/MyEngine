package engine;

import java.util.Random;

public class Screen extends Texture {
	public Screen(int width, int height) {
		super(width, height);
	}

	public void render(Ticker ticker) {
		Texture testGraphics = new Texture(64,64);
		int[] testPixels = testGraphics.getPixels();
		Random  r =new Random();
		for(int i=0;i<64;i++){
			for(int j=0;j<64;j++){
				testPixels[i*64+j] = (int)(r.nextInt(255));
			}
		}
		this.draw(testGraphics,0,0);
		
		Line line = new Line(10, 10, 60, 80, 255<<8);
		
		Clipping clip= new Clipping();
		clip.setArea(0, 0, 64, 68);
		clip.clipLine(line);
		
		line.draw(this);
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
