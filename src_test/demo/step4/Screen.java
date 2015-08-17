package demo.step4;


public class Screen extends Bitmap {
	public Screen(int width, int height) {
		super(width, height);
	}

	public void render(Ticker ticker, Bitmap bitmap, int posX, int posY) {
		super.put(bitmap, posX, posY, false);
	}
}
