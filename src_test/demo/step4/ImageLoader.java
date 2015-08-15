package demo.step4;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

public class ImageLoader {
	//public static Bitmap sprites = load("/tex/sprites.png");
	public static Bitmap texture = load("/tex/pokemonSprite.png");//80*80

	private static Bitmap load(String filename) {
		try {
			URL url = ImageLoader.class.getResource(filename);
			BufferedImage img = ImageIO.read(url);
			int w = img.getWidth();
			int h = img.getHeight();
			Bitmap result = new Bitmap(w, h);
			int[] pixels = result.getPixels();
			img.getRGB(0, 0, w, h, pixels, 0, w);
			return result;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}