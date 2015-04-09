package demo.step0;

import java.awt.Canvas;
import java.awt.Dimension;

import javax.swing.JFrame;

public class Game extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L;

	private static final int WIDTH = 160;
	private static final int HEIGHT = 120;
	private static final int SCALE = 4;
	
	private boolean running = false;
	private Thread gameThread;

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
		frame.add(game);
		frame.pack();
		frame.setResizable(false);

		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		game.startGame();
	}
	
	public void startGame(){
		if(this.running){
			return;
		}
		this.running=true;
		this.gameThread = new Thread(this);
		this.gameThread.start();
	}
	
	public void stopGame(){
		this.running=false;
		try{
			this.gameThread.join();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while(running){
			System.out.println("tick() method");
			System.out.println("render() method");
		}
		System.exit(0);
	}
}
