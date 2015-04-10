package engine;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;

public class Game extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L;

	private static final int WIDTH = 160;
	private static final int HEIGHT = 120;
	private static final int SCALE = 4;
	
	private static final double NANO_PER_SECOND = 1000000000.0;
	private static final double NANO_PER_SECOND_1_OVER_10 = 1000000000.0;

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

		JFrame frame = new JFrame("Game step5");
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
		int frameCount = 0;
		
		double notProcessedTimeInSec = 0;
		double secondsPerTick = 1 / 60.0;
		int tickCount =0;

		long lastTimeNS = System.nanoTime();
		while (running) {
			long currentTimeNS = System.nanoTime();
			long passedTimeNS = currentTimeNS - lastTimeNS;
			lastTimeNS = currentTimeNS;

			if (passedTimeNS < 0) {
				passedTimeNS = 0;
			} else if (passedTimeNS > NANO_PER_SECOND_1_OVER_10) {
				passedTimeNS = (int) NANO_PER_SECOND_1_OVER_10;
			}

			notProcessedTimeInSec += passedTimeNS / NANO_PER_SECOND;

			boolean ticked = false;
			while (notProcessedTimeInSec > secondsPerTick) {
				this.tick();
				tickCount++;
				ticked = true;
				
				notProcessedTimeInSec -= secondsPerTick;

				if (tickCount%60==0) {
					System.out.println(frameCount + "fps");
					//lastTimeNS += NANO_PER_SECOND;
					frameCount = 0;
				}
			}

			this.render();
			frameCount++;
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
		this.screen.render(ticker);

		Graphics g = bs.getDrawGraphics();
		for (int i = 0; i < this.screen.getPixels().length; i++) {
			this.screenPixels[i] = this.screen.getPixels()[i];
		}

		g.drawImage(this.screenImage, 0, 0, WIDTH * SCALE, HEIGHT * SCALE, null);
		g.dispose();
		bs.show();
	}

}
