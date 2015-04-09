package wormchase;
import java.awt.Color;
import java.awt.Graphics;

public class WormChaseGame extends Game {
	private static final long serialVersionUID = 593876016102024415L;
	// entity objects
	private Obstacles obs;
	private Worm fred;

	public WormChaseGame() {
		super();
		this.obs = new Obstacles();
		this.fred = new Worm(pWidth, pHeight, obs);
	}

	@Override
	protected void renderGame(Graphics dbg) {
		obs.draw(dbg);
		fred.draw(dbg);
	}

	@Override
	protected void updateGame() {
		if (!isPaused && !isGameOver) {
			fred.move();
		}
	}

	@Override
	protected void testPress(int x, int y) {
		if (isOverPauseButton)
			isPaused = !isPaused; // toggle pausing
		else if (isOverQuitButton)
			isRunning = false;
		else if (!isPaused && !isGameOver) {
			if (fred.nearHead(x, y)) { // was mouse press near the head?
				isGameOver = true;
				score = (40 - timeSpentInGame) + 40 - obs.getNumObstacles();
				// hack together a score
			} else { // add an obstacle if possible
				// was worm's body not touched?
				if (!fred.touchedAt(x, y)) {
					obs.add(x, y);
					boxesUsed = "" + obs.getNumObstacles();
				}
			}
		}
	}

	@Override
	protected void onGameOver(Graphics g) {
		String msg = "Game Over. Your Score: " + score;
		int x = (pWidth - metrics.stringWidth(msg)) / 2;
		int y = (pHeight - metrics.getHeight()) / 2;
		g.setColor(Color.red);
		g.setFont(font);
		g.drawString(msg, x, y);
	}

	public static void main(String args[]) {
		WormChaseGame game = new WormChaseGame();
		game.runGame();
	}
}
