package demo.step1;

import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JFrame;

public class Game extends JComponent {
	private static final long serialVersionUID = 1L;

	private static final int WIDTH = 160;
	private static final int HEIGHT = 120;
	private static final int SCALE = 4;

	public Game() {
		Dimension size = new Dimension(WIDTH * SCALE, HEIGHT * SCALE);
		this.setSize(size);
		this.setPreferredSize(size);
		this.setMaximumSize(size);
		this.setMinimumSize(size);
	}

	public static void main(String[] args) {
		Game game = new Game();

		JFrame frame = new JFrame("Game step1");
		frame.setContentPane(game);
		frame.pack();
		frame.setResizable(false);

		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
