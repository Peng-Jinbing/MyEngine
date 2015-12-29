package Engine3DSpinningCube;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

public class MainWindow extends Applet implements Runnable {
	Graphics offScrGraphics;
	Image offScrImage;
	Thread runner;
	Vertex cubeVertices[];
	Line cubeLines[];
	int mScreenWidth, mScreenHeight;

	Shape shape;
	Matrix matrix = new Matrix();
	float rotX = (float) 0.0;
	float rotY = (float) 0.0;
	float rotZ = (float) 0.0;
	float scale = (float) 0.5;
	float scaleIncrement = (float) 0.01;

	public void createPoly() {
		cubeVertices[0].SetLocal(-10, -10, 10);
		cubeVertices[1].SetLocal(10, -10, 10);
		cubeVertices[2].SetLocal(10, 10, 10);
		cubeVertices[3].SetLocal(-10, 10, 10);
		cubeVertices[4].SetLocal(-10, -10, -10);
		cubeVertices[5].SetLocal(10, -10, -10);
		cubeVertices[6].SetLocal(10, 10, -10);
		cubeVertices[7].SetLocal(-10, 10, -10);

		cubeLines[0].Set(0, 1);
		cubeLines[1].Set(1, 2);
		cubeLines[2].Set(2, 3);
		cubeLines[3].Set(3, 0);
		cubeLines[4].Set(4, 5);
		cubeLines[5].Set(5, 6);
		cubeLines[6].Set(6, 7);
		cubeLines[7].Set(7, 4);
		cubeLines[8].Set(0, 4);
		cubeLines[9].Set(1, 5);
		cubeLines[10].Set(2, 6);
		cubeLines[11].Set(3, 7);

		shape = new Shape(8, cubeVertices, 12, cubeLines, 10, offScrGraphics);
		matrix.InitTrans();
		matrix.Scale(2);
		matrix.Rotate(1.1, 0.7, 0.3);
	}

	public void start() {
		if (runner == null) {
			runner = new Thread(this);
			runner.start();
		}
	}

	public void stop() {
		if (runner != null) {
			if (runner.isAlive())
				runner.stop();
			runner = null;
		}
	}

	public void run() {
		int i;

		mScreenWidth = size().width;
		mScreenHeight = size().height;

		offScrImage = createImage(mScreenWidth, mScreenHeight);
		offScrGraphics = offScrImage.getGraphics();

		cubeVertices = new Vertex[8];
		for (i = 0; i < 8; i++)
			cubeVertices[i] = new Vertex();
		cubeLines = new Line[12];
		for (i = 0; i < 12; i++)
			cubeLines[i] = new Line();
		createPoly();

		while (true) {
			rotX += 0.12F;
			rotY += 0.12F;
			rotZ += 0.12F;
			scale += scaleIncrement;
			if (scale >= 5)
				scaleIncrement = -(float) 0.01;
			else if (scale <= 0.2)
				scaleIncrement = (float) 0.01;
			matrix.InitTrans();
			matrix.Scale(scale);
			matrix.Rotate(rotX, rotY, rotZ);
			matrix.Translate(0, 0, mScreenWidth >> 1);
			shape.Transform(matrix);
			shape.Project(150);
			shape.Draw(mScreenWidth, mScreenHeight);

			matrix.Translate(0, -20, 0);
			shape.Transform(matrix);
			shape.Project(150);
			shape.Draw(mScreenWidth, mScreenHeight);

			matrix.Translate(0, 40, 0);
			shape.Transform(matrix);
			shape.Project(150);
			shape.Draw(mScreenWidth, mScreenHeight);

			repaint();

			try {
				Thread.sleep(30);
			} catch (InterruptedException e) {
			}
			;
			{
			}
		}
	}

	public synchronized void update(Graphics g) {
		g.drawImage(offScrImage, 0, 0, this);
		offScrGraphics.setColor(Color.black);
		offScrGraphics.fillRect(0, 0, mScreenWidth, mScreenHeight);
	}
}
