package engine;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Art {
	public static Texture floors = loadTexture("/tex/floors.png");

	private static Texture loadTexture(String filename) {
		try {
			BufferedImage img = ImageIO.read(Art.class.getResource(filename));
			int w = img.getWidth();
			int h = img.getHeight();
			Texture result = new Texture(w, h);
			img.getRGB(0, 0, w, h, result.pixels, 0, w);
			return result;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}

// 1:10:40