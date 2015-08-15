package demo.step4;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;


public class Game extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L;

	private static final int WIDTH = 4 * 160;
	private static final int HEIGHT = 4 * 120;
	private static final int SCALE = 1;

	private boolean running = false;

	private Screen screen;
	private Ticker ticker;

	private BufferedImage screenImage;
	private int[] screenPixels;

	private Thread gameThread;

	public Game() {
		Dimension size = new Dimension(WIDTH * SCALE, HEIGHT * SCALE);
		this.setSize(size);
		this.setPreferredSize(size);
		this.setMaximumSize(size);
		this.setMinimumSize(size);

		this.ticker = new Ticker();
		this.screen = new Screen(WIDTH, HEIGHT);

		this.screenImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		DataBufferInt buffer = (DataBufferInt) screenImage.getRaster().getDataBuffer();
		this.screenPixels = buffer.getData();
	}

	public static void main(String[] args) {
		Game game = new Game();

		JFrame frame = new JFrame("Game step4");
		frame.add(game);
		frame.pack();
		frame.setResizable(false);

		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

		game.startGame();
	}

	private synchronized void startGame() {
		if (this.running) {
			return;
		}

		this.running = true;
		this.gameThread = new Thread(this);
		this.gameThread.start();
	}

	private synchronized void stopGame() {
		this.running = false;
		try {
			this.gameThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (running) {
			this.tick();
			this.render();
		}
		System.exit(0);

	}

	private void tick() {
		this.ticker.tick();
	}

	private void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			this.createBufferStrategy(3);
			return;// skip one render
		}
		
		// populate the bitmap
		Bitmap sprites = ImageLoader.texture;

		Sprite sprite1 = new Sprite(0, 0, Sprite.WIDTH_80, Sprite.WIDTH_80);
		sprite1.loadFrame(sprites, 0, 0, 0);
		sprite1.loadFrame(sprites, 1, 1, 1);
		sprite1.currentFrame=0;
		sprite1.draw(screen, true);

		
		Sprite sprite2 = new Sprite(160, 160, Sprite.WIDTH_80, Sprite.WIDTH_80);
		sprite2.loadFrame(sprites, 0, 3, 0);
		sprite2.loadFrame(sprites, 1, 4, 1);
		sprite2.currentFrame=0;
		sprite2.draw(screen, true);

		
		// draw the line
		Line line = new Line(10, 10, 60, 80, 255 << 8);
		Clipping clip = new Clipping();
		clip.setArea(0, 0, 64, 68);
		clip.clipLine(line);
		line.draw(this.screen);
		
		//copy from the screen bitmap to screen buffer
		int[] pixels =this.screen.getPixels();
		for (int i = 0, len = pixels.length; i < len; i++) {
			this.screenPixels[i] = pixels[i];
		}
		
		Graphics g = bs.getDrawGraphics();
		g.drawImage(this.screenImage, 0, 0, WIDTH * SCALE, HEIGHT * SCALE, null);
		g.dispose();
		bs.show();
	}
}
