package wormchase;
import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferStrategy;
import java.text.DecimalFormat;

import javax.swing.JFrame;

public abstract class Game extends JFrame implements Runnable {
	private static final long serialVersionUID = -8548882882984735279L;

	public static int DEFAULT_FPS = 10;

	protected int pWidth;
	protected int pHeight;

	protected static final int NUM_BUFFERS = 2;
	protected static final int NUM_FPS = 10;
	protected static final int MAX_FRAME_SKIPS = 5;
	protected static final int NO_DELAYS_PER_YIELD = 16;
	protected static final long MAX_STATS_INTERVAL = 1000000000L;

	// style
	protected Font font;
	protected FontMetrics metrics;

	private DecimalFormat df = new DecimalFormat("0.##"); // 2 dp
	private DecimalFormat timedf = new DecimalFormat("0.####"); // 4 dp

	private Thread gameThread; // the thread that performs the animation

	// input
	private long period;

	// Status
	protected volatile boolean isPaused;
	protected volatile boolean isGameOver;
	protected volatile boolean isRunning;

	// total
	protected long gameStartTime;
	protected int timeSpentInGame;
	protected int totalFramesSkipped;
	protected int totalFrameCount;
	protected int statsCount;

	// period
	private long prevStatsTime;
	private long statsInterval;
	private int periodFramesSkipped;
	private int periodFrameCount;

	// average
	private double averageFPS;
	private double averageUPS;
	private double[] fpsStore;
	private double[] upsStore;

	// oupput
	protected Object score;
	protected String boxesUsed = "0";
	protected boolean isOverPauseButton;
	protected boolean isOverQuitButton;

	private Rectangle pauseArea, quitArea; // globals
	private boolean finishedOff;
	private GraphicsDevice gd;

	private BufferStrategy bufferStrategy;

	private Graphics gScr;

	public Game() {
		super("The Worm Chase");

		int fps = DEFAULT_FPS;
		this.period = (long) (1000000L * (1000.0 / fps));

		System.out.println("fps: " + fps + "; period: " + period + " ns");

		this.initFullScreen();
		this.readyForTermination();

		// create game components

		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				testMove(e.getX(), e.getY());
			}
		});

		// in the WormPanel constructor
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					testPress(e.getX(), e.getY());
				}
			}
		});

		// init timing elements
		fpsStore = new double[NUM_FPS];
		for (int i = 0; i < NUM_FPS; i++) {
			fpsStore[i] = 0.0;
		}

		upsStore = new double[NUM_FPS];
		for (int i = 0; i < NUM_FPS; i++) {
			upsStore[i] = 0.0;
		}

		// set up message font
		this.font = new Font("SansSerif", Font.BOLD, 24);
		this.metrics = this.getFontMetrics(font);

		this.pauseArea = new Rectangle(pWidth - 100, pHeight - 45, 70, 20);
		this.quitArea = new Rectangle(pWidth - 100, pHeight - 20, 70, 20);
	} // end of WormPanel( )

	private void initFullScreen() {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		gd = ge.getDefaultScreenDevice();

		setUndecorated(true); // no menu bar, borders, etc. or Swing components
		setIgnoreRepaint(true); // turn off all paint events since doing active
								// rendering
		setResizable(false);

		if (!gd.isFullScreenSupported()) {
			System.out.println("Full-screen exclusive mode not supported");
			System.exit(0);
		}
		gd.setFullScreenWindow(this); // switch on full-screen exclusive mode

		// we can now adjust the display modes, if we wish
		showCurrentMode();

		// reportCapabilities();

		pWidth = getBounds().width;
		pHeight =getBounds().height;

		setBufferStrategy();
	}

	private void setBufferStrategy() {
		try {
			EventQueue.invokeAndWait(new Runnable() {
				public void run() {
					createBufferStrategy(NUM_BUFFERS);
				}
			});
		} catch (Exception e) {
			System.out.println("Error while creating buffer strategy");
			System.exit(0);
		}

		try { // sleep to give time for the buffer strategy to be carried out
			Thread.sleep(500); // 0.5 sec
		} catch (InterruptedException ex) {
		}

		bufferStrategy = getBufferStrategy(); // store for later
	} // end of setBufferStrategy()

	private void showCurrentMode() {
		DisplayMode dm = gd.getDisplayMode();
		System.out.println("Current Display Mode: (" + dm.getWidth() + "," + dm.getHeight() + "," + dm.getBitDepth()
				+ "," + dm.getRefreshRate() + ")  ");

	}

	protected void runGame() {
		if (gameThread == null || !isRunning) {
			gameThread = new Thread(this);
			gameThread.start();
		}
	} // end of startGame()

	/* The frames of the animation are drawn inside the while loop. */
	public void run() {
		long beforeTime, afterTime, timeDiff, sleepTime;
		long overSleepTime = 0L;
		int noDelays = 0;
		long excess = 0L;
		this.gameStartTime = System.nanoTime();
		this.prevStatsTime = gameStartTime;
		beforeTime = gameStartTime;
		isRunning = true;

		while (isRunning) {
			updateGame();
			screenUpdate();
			// paintScreen();
			afterTime = System.nanoTime();
			timeDiff = afterTime - beforeTime;
			sleepTime = (period - timeDiff) - overSleepTime;

			if (sleepTime > 0) { // some time left in this cycle
				try {
					Thread.sleep(sleepTime / 1000000L); // nano -> ms
				} catch (InterruptedException ex) {
				}
				overSleepTime = (System.nanoTime() - afterTime) - sleepTime;
			} else { // sleepTime <= 0; frame took longer than the period
				excess -= sleepTime; // store excess time value
				overSleepTime = 0L;
				if (++noDelays >= NO_DELAYS_PER_YIELD) {
					Thread.yield(); // give another thread a chance to run
					noDelays = 0;
				}
			}

			beforeTime = System.nanoTime();
			/*
			 * If frame animation is taking too long, update the game state
			 * without rendering it, to get the updates/sec nearer to the
			 * required FPS.
			 */
			int skips = 0;
			while ((excess > period) && (skips < MAX_FRAME_SKIPS)) {
				excess -= period;
				updateGame(); // update state but don't render
				skips++;
			}
			periodFramesSkipped += skips;
			storeStats();
		}
		printStats();
		System.exit(0); // so window disappears
	}

	private void printStats() {
		System.out.println("Frame Count/Loss: " + totalFrameCount + " / " + totalFramesSkipped);
		System.out.println("Average FPS: " + df.format(averageFPS));
		System.out.println("Average UPS: " + df.format(averageUPS));
		System.out.println("Time Spent: " + timeSpentInGame + " secs");
		// System.out.println("Boxes used: " + obs.getNumObstacles( ));

	}

	private void storeStats() {
		totalFrameCount++;
		periodFrameCount++;
		statsInterval += period;
		if (statsInterval >= MAX_STATS_INTERVAL) {
			long timeNow = System.nanoTime();
			this.timeSpentInGame = (int) ((timeNow - gameStartTime) / 1000000000L); // ns-->secs
			long realElapsedTime = timeNow - prevStatsTime;
			// time since last stats collection
			// totalElapsedTime += realElapsedTime;
			double timingError = (double) ((realElapsedTime - statsInterval) / statsInterval) * 100.0;
			totalFramesSkipped += periodFramesSkipped;
			double actualFPS = 0; // calculate the latest FPS and UPS
			double actualUPS = 0;
			if (realElapsedTime > 0) {
				actualFPS = (((double) periodFrameCount / realElapsedTime) * 1000000000L);
				actualUPS = (((double) (periodFrameCount + periodFramesSkipped) / realElapsedTime) * 1000000000L);
			}
			// store the latest FPS and UPS
			fpsStore[(int) statsCount % NUM_FPS] = actualFPS;
			upsStore[(int) statsCount % NUM_FPS] = actualUPS;
			statsCount++;

			double totalFPS = 0.0; // total the stored FPSs and UPSs
			double totalUPS = 0.0;
			for (int i = 0; i < NUM_FPS; i++) {
				totalFPS += fpsStore[i];
				totalUPS += upsStore[i];
			}
			if (statsCount < NUM_FPS) { // obtain the average FPS and UPS
				averageFPS = totalFPS / statsCount;
				averageUPS = totalUPS / statsCount;
			} else {
				averageFPS = totalFPS / NUM_FPS;
				averageUPS = totalUPS / NUM_FPS;
			}

			System.out.println(timedf.format((double) statsInterval / 1000000000L) + " "
					+ timedf.format((double) realElapsedTime / 1000000000L) + "s " + df.format(timingError) + "% "
					+ periodFrameCount + "c " + periodFramesSkipped + "/" + totalFramesSkipped + " skip; "
					+ df.format(actualFPS) + " " + df.format(averageFPS) + " afps; " + df.format(actualUPS) + " "
					+ df.format(averageUPS) + " aups");

			periodFramesSkipped = 0;
			prevStatsTime = timeNow;
			statsInterval = 0L; // reset
			periodFrameCount = 0;
		}
	}

	private void screenUpdate() {
		try {
			gScr = bufferStrategy.getDrawGraphics();
			render(gScr);
			gScr.dispose();
			if (!bufferStrategy.contentsLost())
				bufferStrategy.show();
			else
				System.out.println("Contents Lost");
			// Sync the display on some systems.
			// (on Linux, this fixes event queue problems)
			Toolkit.getDefaultToolkit().sync();
		} catch (Exception e) {
			e.printStackTrace();
			isRunning = false;
		}
	}

	private void render(Graphics dbg) {
		// clear the background
		dbg.setColor(Color.white);
		dbg.fillRect(0, 0, pWidth, pHeight);
		dbg.setColor(Color.blue);
		dbg.setFont(font);
		// report average FPS and UPS at top left
		dbg.drawString("Average FPS/UPS: " + df.format(averageFPS) + ", " + df.format(averageUPS), 20, 25);

		dbg.drawString("Time Spent: " + timeSpentInGame + " secs", 10, pHeight - 15);
		dbg.drawString("Boxes used: " + boxesUsed, 260, pHeight - 15);
		drawButtons(dbg);

		dbg.setColor(Color.black);
		// draw game elements: the obstacles and the worm
		renderGame(dbg);
		if (isGameOver) {
			onGameOver(dbg);
		}
	}

	protected abstract void renderGame(Graphics dbg);

	private void drawButtons(Graphics g) {
		g.setColor(Color.black);
		// draw the Pause "button"
		if (isOverPauseButton)
			g.setColor(Color.green);
		g.drawOval(pauseArea.x, pauseArea.y, pauseArea.width, pauseArea.height);

		if (isPaused)
			g.drawString("Paused", pauseArea.x, pauseArea.y + 10);
		else
			g.drawString("Pause", pauseArea.x + 5, pauseArea.y + 10);

		if (isOverPauseButton)
			g.setColor(Color.black);

		if (isOverQuitButton)
			g.setColor(Color.green);

		g.drawOval(quitArea.x, quitArea.y, quitArea.width, quitArea.height);
		g.drawString("Quit", quitArea.x + 15, quitArea.y + 10);
		if (isOverQuitButton)
			g.setColor(Color.black);
	} // drawButtons

	// center the game-over message in the panel
	protected abstract void onGameOver(Graphics g);

	protected abstract void updateGame();

	protected void testMove(int x, int y) {
		if (isRunning) { // stops problems with a rapid move after pressing Quit
			isOverPauseButton = pauseArea.contains(x, y) ? true : false;
			isOverQuitButton = quitArea.contains(x, y) ? true : false;
		}
	}

	protected abstract void testPress(int x, int y);

	private void readyForTermination() {
		addKeyListener(new KeyAdapter() {
			// listen for esc, q, end, ctrl-c on the canvas to
			// allow a convenient exit from the full screen configuration
			public void keyPressed(KeyEvent e) {
				int keyCode = e.getKeyCode();
				if ((keyCode == KeyEvent.VK_ESCAPE) || (keyCode == KeyEvent.VK_Q) || (keyCode == KeyEvent.VK_END)
						|| ((keyCode == KeyEvent.VK_C) && e.isControlDown())) {
					isRunning = false;
				}
			}
		});

		// for shutdown tasks
		// a shutdown may not only come from the program
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				isRunning = false;
				finishOff();
			}
		});
	}

	public void resumeGame() {
		this.isPaused = false;
	}

	public void pauseGame() {
		this.isPaused = true;
	}

	public void stopGame() {
		this.isRunning = false;
	}

	private void finishOff() {
		if (!finishedOff) {
			finishedOff = true;
			printStats();
			System.exit(0);
		}
	}
}
