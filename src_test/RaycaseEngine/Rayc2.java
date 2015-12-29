package RaycaseEngine;
//Raycasting technical demo

//Modified by Raziel
//Original version by F. Permadi: http://www.permadi.com/java/rayc/index.html

//Improvements made by Raziel:
// Minimap supports scrolling
// Collision detection
// Basic HUD support
// Textured walls
// Animated walls
// Variable resolution
// Bang!


//Original version disclaimer:
//********************************************************************//
//* F. PERMADI MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE      *//
//* SUITABILITY OF                                                   *//
//* THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT       *//
//* LIMITED                                                          *//
//* TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A      *//
//* PARTICULAR PURPOSE                                               *//
//********************************************************************//


import java.awt.*;
import java.awt.event.*;
import java.applet.*;


public class Rayc2 extends Applet implements Runnable, KeyListener {


	Thread fThread;
	volatile boolean stop;


	static final int TILE_SIZE = 64;
	static final int PROJECTIONPLANEWIDTH = 320;
	static final int PROJECTIONPLANEHEIGHT = 200;

	static final int ANGLE60 = PROJECTIONPLANEWIDTH;
	static final int ANGLE30 = (ANGLE60/2);
	static final int ANGLE15 = (ANGLE30/2);
	static final int ANGLE90 = (ANGLE30*3);
	static final int ANGLE180 = (ANGLE90*2);
	static final int ANGLE270 = (ANGLE90*3);
	static final int ANGLE360 = (ANGLE60*6);
	static final int ANGLE0 = 0;
	static final int ANGLE5 = (ANGLE30/6);
	static final int ANGLE10 = (ANGLE5*2);

	// trigonometric tables
	float fSinTable[];
	float fISinTable[];
	float fCosTable[];
	float fICosTable[];
	float fTanTable[];
	float fITanTable[];
	float fFishTable[];
	float fXStepTable[];
	float fYStepTable[];

	// offscreen buffer
	Image fOffscreenImage;
	Graphics fOffscreenGraphics;

	Image weapon;
	Image walls[][];
	Image band[][];

	// player's attributes
	int fPlayerX = 285;
	int fPlayerY = 160;
	int fPlayerArc = ANGLE90;
	int fPlayerDistanceToTheProjectionPlane = 277;
	int fPlayerHeight =32;
	int fPlayerSpeed = 16;
	int fProjectionPlaneYCenter = PROJECTIONPLANEHEIGHT/2;
	// the following variables are used to keep the player coordinate in the overhead map
	int fPlayerMapX, fPlayerMapY;
	int fMinimapWidth = 5;
	int mapOffsetY = 0;

	// movement flag
	boolean fKeyUp=false;
	boolean fKeyDown=false;
	boolean fKeyLeft=false;
	boolean fKeyRight=false;

	final int maxRes = 10;
	final int minRes = 1;
	int resolution = minRes;

	// For Amusement Only
	int fBang=-1;
	Color fBangC;
	int fBangX, fBangY;
	int fBand = 0;
	java.util.Random rnd;


	static byte fMap[] = {
		1,1,1,1,4,1,1,1,1,1,1,1,1,1,1,1,1,1,4,1,1,1,1,
		1,0,0,5,0,1,0,0,0,0,0,1,0,0,0,0,0,1,0,5,0,0,1,
		1,0,0,1,0,6,0,0,0,0,0,1,0,0,0,0,0,0,0,1,0,0,1,
		1,1,1,1,0,1,0,0,0,0,0,1,0,0,0,0,0,1,0,1,1,1,1,
		1,0,0,1,0,1,0,0,0,0,0,1,0,0,0,0,0,1,0,1,0,0,1,
		1,0,0,5,0,1,0,0,0,0,0,1,0,0,0,0,0,1,0,5,0,0,1,
		1,1,1,1,0,1,0,0,0,0,0,1,0,0,0,0,0,1,0,1,1,1,1,
		1,0,0,5,0,1,0,0,0,0,0,1,0,0,0,0,0,1,0,5,0,0,1,
		1,0,0,1,0,1,0,0,0,0,0,1,0,0,0,0,0,1,0,5,0,0,1,
		1,1,1,1,0,1,0,0,0,0,0,1,0,0,0,0,0,1,0,1,3,1,1,
		1,0,0,1,0,1,1,1,1,1,1,1,1,1,1,1,1,1,0,1,0,0,1,
		1,0,0,5,0,1,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,1,
		1,1,1,1,0,1,0,0,0,0,0,1,0,0,0,0,0,1,0,1,2,1,1,
		1,0,0,5,0,1,0,0,0,0,0,1,0,0,0,0,0,1,0,5,0,0,1,
		1,0,0,1,0,1,0,0,0,0,0,1,0,0,0,0,0,1,0,1,0,0,1,
		1,1,1,1,0,1,0,0,0,0,0,1,0,0,0,0,0,1,0,1,1,1,1,
		1,0,0,1,0,1,0,0,0,0,0,1,0,0,0,0,0,1,0,1,0,0,1,
		1,0,0,5,0,1,0,0,0,0,0,1,0,0,0,0,0,1,0,5,0,0,1,
		1,1,1,1,0,1,0,0,0,3,0,3,0,3,0,0,0,1,0,1,1,1,1,
		1,0,0,5,0,1,1,1,1,1,0,0,0,1,1,1,1,1,0,5,0,0,1,
		1,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,1,
		1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,
		1,0,0,1,0,1,1,1,1,0,0,0,0,0,1,1,1,1,0,1,0,0,1,
		1,0,0,5,0,1,0,0,5,0,0,0,0,0,0,0,0,1,0,5,0,0,1,
		1,1,1,1,0,1,0,0,1,0,0,0,0,0,1,0,0,1,0,1,1,1,1,
		1,0,0,0,0,1,0,0,1,0,0,0,0,0,1,0,0,1,0,5,0,0,1,
		7,0,0,1,0,1,0,0,1,0,0,0,0,0,1,0,0,1,0,1,0,0,1,
		1,1,1,1,4,1,1,1,1,1,6,1,0,1,1,1,1,1,4,1,1,1,1,

		0,0,0,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,0,0,
		0,0,0,0,0,0,0,0,4,0,0,0,0,0,4,0,0,0,0,0,0,0,0,
		0,0,0,0,0,0,0,0,4,0,0,0,0,0,4,0,0,0,0,0,0,0,0,

		1,1,1,1,1,1,1,1,1,0,0,0,0,0,1,1,1,1,1,1,1,1,1,
		1,0,0,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,0,1,
		1,1,1,0,0,0,0,0,6,0,0,0,0,0,0,0,0,0,0,0,1,1,1,
		1,0,0,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,0,1,
		1,0,0,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,0,1,
		1,1,1,1,1,1,5,1,1,0,0,0,0,0,1,1,5,1,1,1,1,1,1,
		1,0,0,0,0,0,0,1,7,0,0,0,0,0,7,1,0,0,0,0,0,0,1,
		1,1,1,1,1,1,1,1,1,0,0,0,0,0,1,1,1,1,1,1,1,1,1,
		1,0,0,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,0,1,
		1,0,0,0,0,0,0,0,1,0,0,0,0,0,6,0,0,0,0,0,0,0,1,
		1,0,0,0,0,0,0,0,6,0,0,0,0,0,1,0,0,0,0,0,0,0,1,
		1,0,0,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,0,1,
		1,0,0,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,0,1,
		1,0,0,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,0,1,
		1,0,0,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,0,1,
		1,0,0,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,0,1,
		1,0,0,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,0,1,
		1,0,0,0,0,0,0,0,1,0,0,0,0,0,6,0,0,0,0,0,0,0,1,
		1,0,0,0,0,0,0,0,6,0,0,0,0,0,1,0,0,0,0,0,0,0,1,
		1,0,0,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,0,1,
		1,1,1,1,1,1,1,1,2,0,0,0,0,0,2,1,1,1,1,1,1,1,1,
		1,0,0,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,0,1,
		1,0,0,0,0,0,0,0,1,0,0,0,0,0,6,0,0,0,0,0,0,0,1,
		1,0,0,0,0,0,0,0,6,0,0,0,0,0,1,0,0,0,0,0,0,0,1,
		1,0,0,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,0,1,
		1,0,0,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,0,1,
		1,0,0,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,0,1,
		1,0,0,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,0,1,
		1,0,0,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,0,1,
		1,0,0,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,0,1,
		1,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,1,
		1,0,0,0,0,0,0,0,6,0,0,0,0,0,1,0,0,0,0,0,0,0,1,
		1,0,0,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,0,1,
		1,1,1,1,1,1,1,1,1,0,0,0,0,0,1,1,1,1,1,1,1,1,1,

		0,0,0,0,0,0,4,0,0,0,0,0,0,0,0,0,0,0,1, 0,0,0,0,
		0,0,0,1,1,1,1,0,1,0,0,0,0,0,0,0,0,0,3, 0,0,0,0,
		0,0,0,1,0,0,1,0,2,0,0,0,0,0,0,0,0,0,8, 0,0,0,0,
		0,0,0,1,0,0,5,0,1,0,0,0,0,0,0,0,0,0,9, 0,0,0,0,
		0,0,0,1,0,0,1,0,1,0,0,0,0,0,0,0,0,0,12,0,0,0,0,
		0,0,0,1,1,1,1,1,7,0,0,0,0,0,0,0,0,0,13,0,0,0,0,
		0,0,0,1,0,0,1,0,1,0,0,0,0,0,0,0,0,0,14,0,0,0,0,
		0,0,0,1,0,0,0,0,1,0,0,0,0,0,0,0,0,0,11,0,0,0,0,
		0,0,0,1,0,0,1,0,2,0,0,0,0,0,0,0,0,0,10,0,0,0,0,
		0,0,0,1,1,1,1,0,1,0,0,0,0,0,0,0,0,0,3, 0,0,0,0,
		0,0,0,0,0,0,4,0,0,0,0,0,0,0,0,0,0,0,1, 0,0,0,0,

		1,1,1,1,1,1,1,1,1,0,0,0,0,0,1,1,1,1,1,1,1,1,1,
		1,0,0,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,0,1,
		1,0,0,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,0,1,
		1,0,0,0,0,0,0,0,1,0,0,0,0,0,6,0,0,0,0,0,0,0,1,
		1,0,0,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,0,1,
		1,0,0,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,0,1,
		1,0,0,0,0,0,0,0,6,0,0,0,0,0,1,0,0,0,0,0,0,0,1,
		1,0,0,0,0,0,0,0,1,0,0,0,0,0,2,0,0,0,0,0,0,0,1,
		1,1,1,1,1,1,1,1,1,0,0,0,0,0,1,1,1,1,1,1,1,1,1,
		1,0,0,0,0,0,0,0,2,0,0,0,0,0,1,0,0,0,0,0,0,0,1,
		1,0,0,0,0,0,0,0,1,0,0,0,0,0,6,0,0,0,0,0,0,0,1,
		1,0,0,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,0,1,
		1,0,0,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,0,1,
		1,0,0,0,0,0,0,0,6,0,0,0,0,0,1,0,0,0,0,0,0,0,1,
		1,0,0,0,0,0,0,0,1,0,0,0,0,0,2,0,0,0,0,0,0,0,1,
		1,1,1,1,1,1,1,1,1,0,0,0,0,0,1,1,1,1,1,1,1,1,1,
		1,0,0,0,0,0,0,0,2,0,0,0,0,0,1,0,0,0,0,0,0,0,1,
		1,0,0,0,0,0,0,0,1,0,0,0,0,0,6,0,0,0,0,0,0,0,1,
		1,0,0,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,0,1,
		1,0,0,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,0,1,
		1,0,0,0,0,0,0,0,6,0,0,0,0,0,1,0,0,0,0,0,0,0,1,
		1,0,0,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,0,1,
		1,1,1,1,1,1,1,1,1,1,4,2,4,1,1,1,1,1,1,1,1,1,1
	};

	static final int MAP_WIDTH = 23;
	static final int MAP_HEIGHT = fMap.length/MAP_WIDTH;


	float arcToRad(float arcAngle) {
		return ((float)(arcAngle*Math.PI)/(float)ANGLE180);    
	}


	// Creates tigonometric values to speed up the engine
	public void createTables() {

		int i;
		float radian;
		fSinTable   = new float[ANGLE360+1];
		fISinTable  = new float[ANGLE360+1];
		fCosTable   = new float[ANGLE360+1];
		fICosTable  = new float[ANGLE360+1];
		fTanTable   = new float[ANGLE360+1];
		fITanTable  = new float[ANGLE360+1];
		fFishTable  = new float[ANGLE60+1];
		fXStepTable = new float[ANGLE360+1];
		fYStepTable = new float[ANGLE360+1];

		for (i=0; i<=ANGLE360; i++) {

			// get the radian value (the last addition is to avoid division by 0, try removing
			// that and you'll see a hole in the wall when a ray is at 0, 90, 180, or 270 degree)

			radian = arcToRad(i) + (float)(0.0001);
			fSinTable[i]=(float)Math.sin(radian);
			fISinTable[i]=(1.0F/(fSinTable[i]));
			fCosTable[i]=(float)Math.cos(radian);
			fICosTable[i]=(1.0F/(fCosTable[i]));
			fTanTable[i]=(float)Math.tan(radian);
			fITanTable[i]=(1.0F/fTanTable[i]);


			//  you can see that the distance between xi is the same
			//  if we know the angle
			//  _____|_/next xi______________
			//       |
			//  ____/|next xi_________   slope = tan = height / dist between xi's
			//     / |
			//  __/__|_________  dist between xi = height/tan where height=tile size
			// old xi|
			//                  distance between xi = x_step[view_angle];
			//
			//


			// facing left
			if (i>=ANGLE90 && i<ANGLE270) {
				fXStepTable[i] = (float)(TILE_SIZE/fTanTable[i]);
				if (fXStepTable[i]>0)
					fXStepTable[i]=-fXStepTable[i];
			}

			// facing right
			else {
				fXStepTable[i] = (float)(TILE_SIZE/fTanTable[i]);
				if (fXStepTable[i]<0)
					fXStepTable[i]=-fXStepTable[i];
			}

			// facing down
			if (i>=ANGLE0 && i<ANGLE180) {
				fYStepTable[i] = (float)(TILE_SIZE*fTanTable[i]);
				if (fYStepTable[i]<0)
					fYStepTable[i]=-fYStepTable[i];
			}

			// facing up
			else {
				fYStepTable[i] = (float)(TILE_SIZE*fTanTable[i]);
				if (fYStepTable[i]>0)
					fYStepTable[i]=-fYStepTable[i];
			}

		}

		for (i=-ANGLE30; i<=ANGLE30; i++) {
			radian = arcToRad(i);
			// we don't have negative angle, so make it start at 0
			// this will give range 0 to 320
			fFishTable[i+ANGLE30] = (float)(1.0F/Math.cos(radian));
		}

	}


	void loadBitmaps() {
		
		weapon = getImage(getClass().getResource("images/gun.png"));
		prepareImage(weapon, this);

		final int nWalls = 15;

		walls = new Image[nWalls][2];
		walls[0][0] = null;
		walls[0][1] = null;
		walls[1][0] = getImage(getClass().getResource("images/cath01.png"));
		walls[1][1] = getImage(getClass().getResource("images/cath01d.png"));
		walls[2][0] = getImage(getClass().getResource("images/cath04.png"));
		walls[2][1] = getImage(getClass().getResource("images/cath04d.png"));
		walls[3][0] = getImage(getClass().getResource("images/cath03.png"));
		walls[3][1] = getImage(getClass().getResource("images/cath03d.png"));
		walls[4][0] = getImage(getClass().getResource("images/window.png"));
		walls[4][1] = walls[4][0];
		walls[5][0] = getImage(getClass().getResource("images/door1.png"));
		walls[5][1] = getImage(getClass().getResource("images/door1d.png"));
		walls[6][0] = getImage(getClass().getResource("images/door2.png"));
		walls[6][1] = walls[6][0];
		walls[7][0] = getImage(getClass().getResource("images/elev.png"));
		walls[7][1] = getImage(getClass().getResource("images/elevd.png"));


		// Band (lol)
		band = new Image[4][2];
		band[0][0] = getImage(getClass().getResource("images/band00.png"));
		band[0][1] = getImage(getClass().getResource("images/band01.png"));
		band[1][0] = getImage(getClass().getResource("images/band10.png"));
		band[1][1] = getImage(getClass().getResource("images/band11.png"));
		band[2][0] = getImage(getClass().getResource("images/band20.png"));
		band[2][1] = getImage(getClass().getResource("images/band21.png"));
		band[3][0] = getImage(getClass().getResource("images/band30.png"));
		band[3][1] = getImage(getClass().getResource("images/band31.png"));
		walls[8][1] = band[0][0];
		walls[9][1] = band[1][0];
		walls[10][1] = band[2][0];
		walls[11][1] = band[3][0];
		walls[12][1] = getImage(getClass().getResource("images/rocks1.png"));
		walls[13][1] = getImage(getClass().getResource("images/rocks2.png"));
		walls[14][1] = getImage(getClass().getResource("images/rocks3.png"));
		
		for (int i=1; i<nWalls; i++) {
			prepareImage(walls[i][0], this);
			prepareImage(walls[i][1], this);
		}
		for (int i=0; i<4; i++)
			prepareImage(band[i][1], this);

	}


	public void start() {
		this.addKeyListener(this);

		createTables();
		loadBitmaps();

		rnd = new java.util.Random();
		fThread = new Thread(this);
		fThread.start();
	}


	public void run() {

     requestFocus();

     // create offscreen buffer
		fOffscreenImage=createImage(getSize().width, getSize().height);
		fOffscreenGraphics=fOffscreenImage.getGraphics();

		bigFatLoop();
		
	}


	public void stop() {
     if((fThread != null) && fThread.isAlive())
			stop = true;
	}


	public void paint(Graphics g) {
		if (fOffscreenImage!=null)
			g.drawImage(fOffscreenImage, 0, 0, this);
	}


	public void keyPressed(KeyEvent e) {

		switch (e.getKeyCode())
			{
			case KeyEvent.VK_UP:
			case KeyEvent.VK_W:
				fKeyUp=true;
				break;
			case KeyEvent.VK_DOWN:
			case KeyEvent.VK_S:
				fKeyDown=true;
				break;
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_A:
				fKeyLeft=true;
				break;
			case KeyEvent.VK_RIGHT:
			case KeyEvent.VK_D:
				fKeyRight=true;
				break;

			case KeyEvent.VK_SPACE:
				fBang=5;
				fBangX = rnd.nextInt()%50 - 15;
				fBangY = rnd.nextInt()%40 - 10;
				fBangC = new Color(rnd.nextFloat(), rnd.nextFloat(), rnd.nextFloat());
				break;

			case KeyEvent.VK_R:
				if (resolution > minRes)
					--resolution;
				break;
			case KeyEvent.VK_F:
				if (resolution < maxRes)
					++resolution;
				break;

			default:
			}

	}

	public void keyReleased(KeyEvent e) {

		switch (e.getKeyCode())
			{
			case KeyEvent.VK_UP:
			case KeyEvent.VK_W:
				fKeyUp=false;
				break;
			case KeyEvent.VK_DOWN:
			case KeyEvent.VK_S:
				fKeyDown=false;
				break;
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_A:
				fKeyLeft=false;
				break;
			case KeyEvent.VK_RIGHT:
			case KeyEvent.VK_D:
				fKeyRight=false;
				break;
			default:
			}

	}
		
	public void keyTyped(KeyEvent e) {}


	private void bigFatLoop() {

		while(!stop) {
			// rotate left
			if (fKeyLeft) {
				if ((fPlayerArc-=ANGLE10) < ANGLE0)
					fPlayerArc+=ANGLE360;
			}

			// rotate right
			else if (fKeyRight) {
				if ((fPlayerArc+=ANGLE10) >= ANGLE360)
					fPlayerArc-=ANGLE360;
			}

			//  _____     _
			// |\ arc     |
			// |  \       y
			// |    \     |
			//            -
			// |--x--|  
			//
			//  sin(arc)=y/diagonal
			//  cos(arc)=x/diagonal   where diagonal=speed

			float playerXDir = fCosTable[fPlayerArc];
			float playerYDir = fSinTable[fPlayerArc];
			float playerXMargin = 5*playerXDir;
			float playerYMargin = 5*playerYDir;

			// move forward
			if (fKeyUp) {
				int fPlayerXinc = fPlayerX + (int)(playerXDir*fPlayerSpeed);
				int fPlayerYinc = fPlayerY + (int)(playerYDir*fPlayerSpeed);
				if (fMap[(int)((fPlayerXinc+playerXMargin)/TILE_SIZE)
						 + MAP_WIDTH * (int)((fPlayerY+playerYMargin)/TILE_SIZE)] == 0)
					fPlayerX = fPlayerXinc;
				if (fMap[(int)((fPlayerX+playerXMargin)/TILE_SIZE)
						 + MAP_WIDTH * (int)((fPlayerYinc+playerYMargin)/TILE_SIZE)] == 0)
					fPlayerY = fPlayerYinc;
			}

			// move backward
			else if (fKeyDown) {
				int fPlayerXinc = fPlayerX - (int)(playerXDir*fPlayerSpeed);
				int fPlayerYinc = fPlayerY - (int)(playerYDir*fPlayerSpeed);
				if (fMap[(int)((fPlayerXinc-playerXMargin)/TILE_SIZE)
						 + MAP_WIDTH * (int)((fPlayerY-playerYMargin)/TILE_SIZE)] == 0)
					fPlayerX = fPlayerXinc;
				if (fMap[(int)((fPlayerX-playerXMargin)/TILE_SIZE)
						 + MAP_WIDTH * (int)((fPlayerYinc-playerYMargin)/TILE_SIZE)] == 0)
					fPlayerY = fPlayerYinc;
			}

			// Band stuff
			switch (fBand++) {
			case 0:
				walls[9][1] = band[1][0];
				walls[11][1] = band[3][0];
				break;

			case 1:
				walls[8][1] = band[0][0];
				walls[10][1] = band[2][0];
				break;

			case 3:
				walls[8][1] = band[0][1];
				walls[10][1] = band[2][1];
				break;

			case 4:
				walls[9][1] = band[1][1];
				walls[11][1] = band[3][1];
				break;

			case 6:
				fBand = -1;
				break;

			default:
				break;
			}


			render();

			try {
				Thread.sleep(50);
			} catch (Exception sleepProblem) {
				showStatus("Insomnia");
			}
		}

	}



	private void drawBackground() {

		/*
		// sky
		int c=25;
		int r;
		for (r=0; r<PROJECTIONPLANEHEIGHT/2; r+=10) {
		fOffscreenGraphics.setColor(new Color(c, 125, 225));
		fOffscreenGraphics.fillRect(0, r, PROJECTIONPLANEWIDTH, 10);
		c+=20;
		}

		// ground
		c=22;
		for (; r<PROJECTIONPLANEHEIGHT; r+=15) {
		fOffscreenGraphics.setColor(new Color(c, 20, 20));
		fOffscreenGraphics.fillRect(0, r, PROJECTIONPLANEWIDTH, 15);
		c+=15;
		}
		*/

		fOffscreenGraphics.setColor(Color.gray);
		fOffscreenGraphics.fillRect(0, 0,
									PROJECTIONPLANEWIDTH, PROJECTIONPLANEHEIGHT/2);
		fOffscreenGraphics.setColor(Color.darkGray);
		fOffscreenGraphics.fillRect(0, PROJECTIONPLANEHEIGHT/2,
									PROJECTIONPLANEWIDTH, PROJECTIONPLANEHEIGHT/2);

	}



	private void drawOverheadMap() {

		fPlayerMapX=PROJECTIONPLANEWIDTH+(int)(((float)fPlayerX/(float)TILE_SIZE) * fMinimapWidth);
		fPlayerMapY=(int)(((float)fPlayerY/(float)TILE_SIZE) * fMinimapWidth);

		int plrOffsetY = fPlayerMapY/fMinimapWidth;
		int mapTilesY = PROJECTIONPLANEHEIGHT/fMinimapWidth;

		if (plrOffsetY <= mapTilesY/2)
			mapOffsetY = 0;
		else if (plrOffsetY >= MAP_HEIGHT - (mapTilesY/2))
			mapOffsetY = MAP_HEIGHT - mapTilesY;
		else
			mapOffsetY = plrOffsetY - (mapTilesY/2);

		for (int u=0; u<MAP_WIDTH; u++) {
			for (int v=0; v<mapTilesY; v++) {
				if (fMap[(v+mapOffsetY)*MAP_WIDTH+u] != 0) {
					fOffscreenGraphics.setColor(Color.cyan);
				} else {
					fOffscreenGraphics.setColor(Color.black);
				}
				fOffscreenGraphics.fillRect(PROJECTIONPLANEWIDTH+(u*fMinimapWidth), (v*fMinimapWidth),
											fMinimapWidth, fMinimapWidth);
			}
		}
     
	}



	private void drawRayOnOverheadMap(float x, float y) {

     // draw line from the player position to the position where the ray
     // intersect with wall
     fOffscreenGraphics.setColor(Color.yellow);
     fOffscreenGraphics.drawLine(fPlayerMapX,
									fPlayerMapY - mapOffsetY * fMinimapWidth,
									(int)(PROJECTIONPLANEWIDTH
										  + ((float)(x * fMinimapWidth)/(float)TILE_SIZE)),
									(int)(((float)(y * fMinimapWidth)/(float)TILE_SIZE)
										  - mapOffsetY * fMinimapWidth));

     // draw a red line indication the player's direction
     fOffscreenGraphics.setColor(Color.red);
		fOffscreenGraphics.drawLine(fPlayerMapX,
									fPlayerMapY - mapOffsetY * fMinimapWidth,
									(int)(fPlayerMapX + fCosTable[fPlayerArc]*10),
									(int)((fPlayerMapY + fSinTable[fPlayerArc]*10)
										  - mapOffsetY * fMinimapWidth));
	}

	private void drawHUD() {

		// Weapon
		fOffscreenGraphics.drawImage(weapon,
									 (PROJECTIONPLANEWIDTH-weapon.getWidth(this))/2,
									 (PROJECTIONPLANEHEIGHT-weapon.getHeight(this)),
									 this);

		if (fBang > 0) {
			// Alright, it's kinda dirty, but who cares?
			--fBang;
			fOffscreenGraphics.setColor(fBangC);
			fOffscreenGraphics.setFont(new Font("Monospaced", Font.BOLD|Font.CENTER_BASELINE, 20));
			fOffscreenGraphics.drawString("*Bang!*", PROJECTIONPLANEWIDTH/2-22 + fBangX,
										  PROJECTIONPLANEHEIGHT*3/4 + fBangY);
		}

	}


	public void render() {

     drawBackground();
		drawOverheadMap();

		int verticalGrid;                        // horizotal or vertical coordinate of intersection
		int horizontalGrid;                      // theoritically, this will be multiple of TILE_SIZE
		                                         // , but some trick did here might cause
		                                         // the values off by 1
		int distToNextVerticalGrid;              // how far to the next bound (this is multiple of
		int distToNextHorizontalGrid;            // tile size)
		float xIntersection;                     // x and y intersections
		float yIntersection;
		float distToNextXIntersection;
		float distToNextYIntersection;

		int xGridIndex;                          // the current cell that the ray is in
		int yGridIndex;

		float distToVerticalGridBeingHit;        // the distance of the x and y ray intersections from
		float distToHorizontalGridBeingHit;      // the viewpoint

		int castArc, castColumn;

		float dist;
		int slice, material, wallColor;


     // Field of view is 60 degree with the point of view
		// (player's direction in the middle)
     //   30   30
     //      ^
     //    \ | /
     //     \|/
     //      v
     // We will trace the rays starting from the leftmost ray


		castArc = fPlayerArc - ANGLE30;

		if (castArc < 0)
			castArc += ANGLE360;


		for (castColumn=0; castColumn<PROJECTIONPLANEWIDTH; castColumn+=resolution) {

			/* Horizontal Collision */

			// Ray is facing down
			if (castArc > ANGLE0 && castArc < ANGLE180) {
				horizontalGrid = (fPlayerY/TILE_SIZE) * TILE_SIZE + TILE_SIZE;
				distToNextHorizontalGrid = TILE_SIZE;

				xIntersection = (fITanTable[castArc] * (horizontalGrid - fPlayerY)) + fPlayerX;
			}

			// Ray is facing up
			else {
				horizontalGrid = (fPlayerY/TILE_SIZE) * TILE_SIZE;
				distToNextHorizontalGrid = -TILE_SIZE;

				xIntersection = (fITanTable[castArc] * (horizontalGrid - fPlayerY)) + fPlayerX;

				--horizontalGrid;
			}


			// Looking for horizontal wall
			if ((castArc == ANGLE0) || (castArc == ANGLE180)) {
				distToHorizontalGridBeingHit = Float.MAX_VALUE;

			} else {

				distToNextXIntersection = fXStepTable[castArc];

				while (true) {

					xGridIndex = (int)(xIntersection/TILE_SIZE);
					// in the picture, yGridIndex will be 1
					yGridIndex = (horizontalGrid/TILE_SIZE);

					if ((xGridIndex >= MAP_WIDTH) ||
						(yGridIndex >= MAP_HEIGHT) ||
						(xGridIndex < 0) ||
						(yGridIndex < 0)) {
						distToHorizontalGridBeingHit = Float.MAX_VALUE;
						break;

					} else if ((fMap[yGridIndex*MAP_WIDTH+xGridIndex]) != 0) {
						distToHorizontalGridBeingHit  = (xIntersection-fPlayerX)*fICosTable[castArc];
						break;

					} else {
						xIntersection += distToNextXIntersection;
						horizontalGrid += distToNextHorizontalGrid;
					}

				}
			}



			/* Vertical Collision */

			// Ray facing right
			if (castArc < ANGLE90 || castArc > ANGLE270) {

				verticalGrid = TILE_SIZE + (fPlayerX/TILE_SIZE) * TILE_SIZE;
				distToNextVerticalGrid = TILE_SIZE;

				yIntersection = (fTanTable[castArc] * (verticalGrid - fPlayerX)) + fPlayerY;
				
			}

			// Ray facing left
			else {

				verticalGrid = (fPlayerX/TILE_SIZE) * TILE_SIZE;
				distToNextVerticalGrid = -TILE_SIZE;

				yIntersection = (fTanTable[castArc] * (verticalGrid - fPlayerX)) + fPlayerY;

				verticalGrid--;

			}

			// Looking for vertical wall
			if ((castArc == ANGLE90) || (castArc == ANGLE270)) {

				distToVerticalGridBeingHit = Float.MAX_VALUE;

			} else {

				distToNextYIntersection = fYStepTable[castArc];

				while (true) {

					// compute current map position to inspect
					xGridIndex = (verticalGrid/TILE_SIZE);
					yGridIndex = (int)(yIntersection/TILE_SIZE);

					if ((xGridIndex >= MAP_WIDTH) ||
						(yGridIndex >= MAP_HEIGHT) ||
						(xGridIndex < 0) ||
						(yGridIndex < 0)) {
						distToVerticalGridBeingHit = Float.MAX_VALUE;
						break;

					} else if ((fMap[yGridIndex*MAP_WIDTH+xGridIndex]) != 0) {
						distToVerticalGridBeingHit = (yIntersection-fPlayerY) * fISinTable[castArc];
						break;

					} else {
						yIntersection += distToNextYIntersection;
						verticalGrid += distToNextVerticalGrid;
					}

				}
			}



			/* Drawing the wall slice */


			// Determining which ray strikes a closer wall.

			if (distToHorizontalGridBeingHit < distToVerticalGridBeingHit) {
				drawRayOnOverheadMap(xIntersection, horizontalGrid);
				dist = distToHorizontalGridBeingHit;
				material = fMap[(int)((int)(xIntersection/TILE_SIZE)
									  + MAP_WIDTH * (int)(horizontalGrid/TILE_SIZE))];
				slice = (int)(xIntersection % TILE_SIZE);
				wallColor = 0;
			} else {
				drawRayOnOverheadMap(verticalGrid, yIntersection);
				dist = distToVerticalGridBeingHit;
				material = fMap[(int)((int)(verticalGrid/TILE_SIZE)
									  + MAP_WIDTH * (int)(yIntersection/TILE_SIZE))];
				slice = (int)(yIntersection % TILE_SIZE);
				wallColor = 1;
			}

			// Fishbown effect compensation
			dist /= fFishTable[castColumn];

			int projectedWallHeight = (int)(TILE_SIZE*(float)fPlayerDistanceToTheProjectionPlane/dist);
			int topOfWall = fProjectionPlaneYCenter - (int)(projectedWallHeight*0.5F);
			int bottomOfWall = fProjectionPlaneYCenter + (int)(projectedWallHeight*0.5F);
			
			fOffscreenGraphics.drawImage(walls[material][wallColor],
										 castColumn, topOfWall,
										 castColumn+resolution, bottomOfWall,
										 slice, 0,
										 slice+1, 64,
										 this);
			// 			
			// Next ray
			castArc += resolution;
			if (castArc >= ANGLE360)
				castArc -= ANGLE360;

		}

		drawHUD();

		paint(getGraphics());

	}

	
}


