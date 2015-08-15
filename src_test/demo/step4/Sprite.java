package demo.step4;

import java.awt.image.BufferedImage;

public class Sprite {
	public static final int MAX_SPRITE_FRAMES = 32;
	public static final int WIDTH_16 = 16;
	public static final int WIDTH_80 = 80;

	public Bitmap[] frames = new Bitmap[MAX_SPRITE_FRAMES];

	public int positionX, positionY;
	public int width, height;

	public int currentFrame;
	public int numberOfFrames;
	public int state;
	public Bitmap background;

	public int xClip, yClip;
	public int widthClip, heightClip;
	public int visible;

	public Sprite(int x, int y, int width, int height) {
		this.positionX = x;
		this.positionY = y;
		this.width = width;
		this.height = height;

		this.visible = 1;
		this.currentFrame = 0;
		this.state = 0;
		this.numberOfFrames = 0;
		this.background = new Bitmap(width, height);

		for (int index = 0; index < MAX_SPRITE_FRAMES; index++) {
			this.frames[index] = null;
		}
	}

	public void loadFrame(Bitmap image, int frameIndex, int cellX, int cellY) {
		Bitmap bitmap = new Bitmap(width, height);
		frames[frameIndex] = bitmap;

		int yOff = height * cellY * image.width; // 16*cellY
		int xOff = width * cellX;

		for (int y = 0; y < height; y++) {
			int bitmapOffset = y * width;
			int imageOffset = yOff + xOff;
			for (int x = 0; x < width; x++) {
				bitmap.pixels[bitmapOffset + x] = image.pixels[imageOffset + x];
			}
			yOff += image.width;
		}
		numberOfFrames++;
	}
	
	public void draw(Bitmap screenImage, boolean transparent) {
		if(frames==null || currentFrame> frames.length || frames[currentFrame]==null){
			return;
		}
		
		Bitmap bitmap = frames[currentFrame];
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		int[] p = bitmap.getPixels();
		
		
		int screenWidth = screenImage.getWidth();
		int screenHeight = screenImage.getHeight();
		
		int[] screenPixels = screenImage.getPixels();

		int posX = 0, posY = 0;
		int screenRowOffset = positionY * screenWidth;
		int bitmapRowOffset = 0;
		for (int y = 0; y < h; y++) {
			posY = positionY + y;
			if (posY >= 0 && posY < screenHeight) {
				for (int x = 0; x < w; x++) {
					posX = positionX + x;
					if (posX > 0 && posX < screenWidth) {
						int pixelColor = p[bitmapRowOffset + x];
						if (transparent && pixelColor == 0XFFFFFFFF) {// white
							continue;
						} else {
							screenPixels[screenRowOffset + posX] = p[bitmapRowOffset + x];
						}
					}
				}
			}
			screenRowOffset +=screenWidth;
			bitmapRowOffset +=w;
		}
	}

	public void under(BufferedImage screenImage) {
		// TODO
	}

	public void erase(BufferedImage screenImage) {
		// TODO
	}

	public void drawClip(BufferedImage screenImage, boolean transparent) {
		// TODO
	}

	public void underClip(BufferedImage screenImage) {
		// TODO
	}

	public void eraseClip(BufferedImage screenImage) {
		// TODO
	}

/*	public void scanBackground(BufferedImage screenImage) {
		Bitmap bitmap = new Bitmap(width, height);
		frames[spriteFrame] = bitmap;

		int yOff = height * cellY * image.width; // 16*cellY
		int xOff = width * cellX;

		for (int y = 0; y < height; y++) {
			int bitmapOffset = y * width;
			int imageOffset = yOff + xOff;
			for (int x = 0; x < width; x++) {
				bitmap.pixels[bitmapOffset + x] = image.pixels[imageOffset + x];
			}
			yOff += image.width;
		}
		numberOfFrames++;
	}
*/}

/**
 * 	public int counter1; 
	public int counter2;
	public int counter3;

	public int threshold1; 
	public int threshold2;
	public int threshold3;
	
	
			this.counter1 = c1;
		this.counter2 = c2;
		this.counter3 = c3;
		this.threshold1 = t1;
		this.threshold2 = t2;
		this.threshold3 = t3;

	*/
