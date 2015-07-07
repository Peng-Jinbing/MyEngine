package demo.step4;

public class Sprite {
	public static final int MAX_SPRITE_FRAMES = 32;
	public static final int WIDTH_16 = 16;

	public Bitmap[] frames = new Bitmap[MAX_SPRITE_FRAMES];

	public int x, y;
	public int width, height;

	public int currentFrame;
	public int numberOfFrames;
	public int state;
	public int[] background;

	public int xClip, yClip;
	public int widthClip, heightClip;
	public int visible;

	public Sprite(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;

		this.visible = 1;
		this.currentFrame = 0;
		this.state = 0;
		this.numberOfFrames = 0;
		this.background = null;

		for (int index = 0; index < MAX_SPRITE_FRAMES; index++) {
			this.frames[index] = null;
		}
	}

	public void loadFrameFromImage(Bitmap image, int spriteFrame, int cellX, int cellY) {
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
}

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
