package engine;

import java.util.Random;

public class Screen extends Texture {
	public static final int PANEL_HEIGHT = 8*3;
	Texture testArea;
	Texture gameArea;
	Texture viewArea;
	
	
	public Screen(int width, int height) {
		super(width, height);

		this.gameArea = new Texture(width,  PANEL_HEIGHT);
		this.viewArea = new Texture(width,  height - PANEL_HEIGHT);
	}

	public void render(Ticker ticker) {
		//clear(255<<16|255<<8|255);
		clear(0);
		
		int textureWidth = 64;
		int textureHeight = 64;
		int x1 = (this.width - textureWidth) / 2;
		int y1 = (this.height - textureHeight) / 2;
		int x2 = x1 + textureWidth - 1;
		int y2 = y1 + textureHeight - 1;

		testArea = new Texture(textureWidth, textureHeight);
		int[] testPixels = testArea.getPixels();
		Random r = new Random();
		for (int i = 0; i < textureHeight*textureWidth; i++) {
			testPixels[i] = r.nextInt()* ((int)(r.nextInt(5)/4));
		}

		for (int i = 0; i < 900; i++) {
			int xd = (ticker.time + i * 8) % 400 - 100;
			int yd = 0;// (int) (Math.cos(ticker.time) * 80);
			gameArea.draw(testArea, (gameArea.width - textureWidth) / 2  + xd, (gameArea.height - textureHeight) / 2 + yd);
		}

		draw(viewArea, 0, 0);
		draw(gameArea, 0, height - PANEL_HEIGHT);
		
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


	
	public void clear(int color){
		for (int i = 0; i < width * height; i++) {
			this.pixels[i] = color;
		}
	}	
}
