package RaycaseEngine;

import java.awt.*;
import java.applet.*;

/**                     rayDir
 *        \             |             /
 *   pos+dir-plane     pos+dir     pos+dir+plane
 *          \           |           / 
 *           .----------.----------.
 *            \         |         /
 *             \        |        /
 *              \       |       /
 *               \      |      /
 *                \     |     /
 *                 \    |    /
 *                  \   |   /
 *                   \  |  /
 *                    \ | /
 *                     \|/
 *                      .  pos
 *   pos           : camera position vector, (posX, posY)
 *   dir           : camera direction vector,(dirX, dirY)
 *   plane         : camera plane vector     (planeX, planeY)
 *   rayDir        : = direction vector + a part of the plane
 *   fov           : = 2* atan(length of pane/length of dir)
 *   cameraX       : x-coordinate in camera space,([-1, 1])
 *   worldMap      :(worldMapX, worldMapY)    
 *   sideDistX     : 
 *   sideDistY     :
 *   deltaDistX    :
 *   deltaDistY    :
 *   stepX
 *   stepY
 *   isHit
 *   hitSide
 *   
 * */	

public class Rayc extends Applet implements Runnable{
	private static final long serialVersionUID = 4174213160762292020L;

	private Thread mainThread;

	private static final int TILE_SIZE = 64;
	private static final int WALL_HEIGHT = 64;
	
	private static final int PROJECTIONPLANEWIDTH = 320;
	private static final int PROJECTIONPLANEHEIGHT = 200;
	
	private static final int ANGLE60 = PROJECTIONPLANEWIDTH;
	private static final int ANGLE30 = (ANGLE60 / 2);
	private static final int ANGLE15 = (ANGLE30 / 2);
	private static final int ANGLE90 = (ANGLE30 * 3);
	private static final int ANGLE180 = (ANGLE90 * 2);
	private static final int ANGLE270 = (ANGLE90 * 3);
	private static final int ANGLE360 = (ANGLE60 * 6);

	private static final int ANGLE0 = 0;
	private static final int ANGLE5 = (ANGLE30 / 6);
	private static final int ANGLE10 = (ANGLE5 * 2);

	// trigonometric tables
	private float sinTable[];
	private float isinTable[];
	private float cosTable[];
	private float icosTable[];
	private float tanTable[];
	private float itanTable[];
	private float fishTable[];
	private float xStepTable[];
	private float yStepTable[];

	private Image fOffscreenImage;
	private Graphics fOffscreenGraphics;

	private int playerPosX = 100;
	private int playerPosY = 160;
	private int playerArc = ANGLE0;
	private int playerDistanceToTheProjectionPlane = 277;
	
	private int playerHeight =32;
	private int playerSpeed = 16;
	
	private 	int fProjectionPlaneYCenter = PROJECTIONPLANEHEIGHT/2;
	// the following variables are used to keep the player coordinate in the overhead map
	private int fPlayerMapX, fPlayerMapY, fMinimapWidth;

	private boolean isKeyUp = false;
	private boolean isKeyDown = false;
	private boolean isKeyLeft = false;
	private boolean isKeyRight = false;

	private byte[] worldMap = new byte[] {
            W,W,W,W,W,W,W,W,W,W,W,W,
            W,O,O,O,O,O,O,O,O,O,O,W,
            W,O,O,O,O,O,O,O,O,O,O,W,
            W,O,O,O,O,O,O,O,W,O,O,W,
            W,O,O,W,O,W,O,O,W,O,O,W,
            W,O,O,W,O,W,W,O,W,O,O,W,
            W,O,O,W,O,O,W,O,W,O,O,W,
            W,O,O,O,W,O,W,O,W,O,O,W,
            W,O,O,O,W,O,W,O,W,O,O,W,
            W,O,O,O,W,W,W,O,W,O,O,W,
            W,O,O,O,O,O,O,O,O,O,O,W,
            W,O,O,O,O,O,O,O,O,O,O,W,
            W,O,O,O,O,O,O,O,O,O,O,W,
            W,O,O,O,O,O,O,O,W,O,O,W,
            W,O,O,W,O,W,O,O,W,O,O,W,
            W,O,O,W,O,W,W,O,W,O,O,W,
            W,O,O,W,O,O,W,O,W,O,O,W,
            W,O,O,O,W,O,W,O,W,O,O,W,
            W,O,O,O,W,O,W,O,W,O,O,W,
            W,O,O,O,W,W,W,O,W,O,O,W,
            W,O,O,O,O,O,O,O,O,O,O,W,
            W,O,O,O,O,O,O,O,O,O,O,W,
            W,O,O,O,O,O,O,O,O,O,O,W,
            W,O,O,O,O,O,O,O,W,O,O,W,
            W,O,O,W,O,W,O,O,W,O,O,W,
            W,O,O,W,O,W,W,O,W,O,O,W,
            W,O,O,W,O,O,W,O,W,O,O,W,
            W,O,O,O,W,O,W,O,W,O,O,W,
            W,O,O,O,W,O,W,O,W,O,O,W,
            W,O,O,O,W,W,W,O,W,O,O,W,
            W,O,O,O,O,O,O,O,O,O,O,W,
            W,W,W,W,W,W,W,W,W,W,W,W
      };
	
	private static final byte W = 1; // wall
	private static final byte O = 0; // opening
	private static final int MAP_WIDTH = 12;
	private static final int MAP_HEIGHT = 12;
	
	public void start() {
		createTables();
		
		mainThread = new Thread(this);
		mainThread.start();
	}

	public void run() {
		requestFocus();

		fOffscreenImage = createImage(size().width, size().height);
		fOffscreenGraphics = fOffscreenImage.getGraphics();

		while (true) {
			if (isKeyLeft) {
				playerArc -= ANGLE10;
				if (playerArc < ANGLE0) {
					playerArc += ANGLE360;
				}
			} else if (isKeyRight) {
				playerArc += ANGLE10;
				if (playerArc >= ANGLE360){
					playerArc -= ANGLE360;
				}
			}

			float playerXDir = cosTable[playerArc];
			float playerYDir = sinTable[playerArc];

			if (isKeyUp) {
				playerPosX += (int) (playerXDir * playerSpeed);
				playerPosY += (int) (playerYDir * playerSpeed);
			} else if (isKeyDown) {
				playerPosX -= (int) (playerXDir * playerSpeed);
				playerPosY -= (int) (playerYDir * playerSpeed);
			}

			render();
			try {
				Thread.sleep(50);
			} catch (Exception sleepProblem) {
				showStatus("Sleep problem");
			}
		}
	}


	private float arcToRad(float arcAngle) {
		return ((float) (arcAngle * Math.PI) / (float) ANGLE180);
	}

	public void createTables() {
		sinTable = new float[ANGLE360 + 1];
		isinTable = new float[ANGLE360 + 1];
		cosTable = new float[ANGLE360 + 1];
		icosTable = new float[ANGLE360 + 1];
		tanTable = new float[ANGLE360 + 1];
		itanTable = new float[ANGLE360 + 1];
		
		fishTable = new float[ANGLE60 + 1];
		xStepTable = new float[ANGLE360 + 1];
		yStepTable = new float[ANGLE360 + 1];

		for (int i = 0; i <= ANGLE360; i++) {
			// get the radian value (the last addition is to avoid division by 0, try removing
			// that and you'll see a hole in the wall when a ray is at 0, 90, 180, or 270 degree)
			float radian = arcToRad(i) + (float) (0.0001);
			
			sinTable[i] = (float) Math.sin(radian);
			isinTable[i] = (1.0F / (sinTable[i]));
			
			cosTable[i] = (float) Math.cos(radian);
			icosTable[i] = (1.0F / (cosTable[i]));
			
			tanTable[i] = (float) Math.tan(radian);
			itanTable[i] = (1.0F / tanTable[i]);

            //moving in clock wise
			//xStep = the distance of X when the Y moves one tile Size
			if (i >= ANGLE90 && i < ANGLE270) {									// facing left
				xStepTable[i] = (float) (TILE_SIZE * itanTable[i]);
				if (xStepTable[i] > 0) {
					xStepTable[i] = -xStepTable[i];
				}
			} else { 															// facing right
				xStepTable[i] = (float) (TILE_SIZE *itanTable[i]);
				if (xStepTable[i] < 0) {
					xStepTable[i] = -xStepTable[i];
				}
			}

            //yStep = the distance of Y when the x moves one tile Size
			if (i >= ANGLE0 && i < ANGLE180) {								   // FACING DOWN
				yStepTable[i] = (float) (TILE_SIZE * tanTable[i]);
				if (yStepTable[i] < 0){
					yStepTable[i] = -yStepTable[i];
				}
			}else {									                           // FACING UP
				yStepTable[i] = (float) (TILE_SIZE * tanTable[i]);
				if (yStepTable[i] > 0){
					yStepTable[i] = -yStepTable[i];
				}
			}
		}

		// 0 to 60 map to -30 to 30
		for (int i = -ANGLE30; i <= ANGLE30; i++) {
			float radian = arcToRad(i);
			fishTable[i + ANGLE30] = (float) (1.0F / Math.cos(radian));
		}
    }


	public void drawBackground() {
		// sky
		int c = 25;
		int r;
		for (r = 0; r < PROJECTIONPLANEHEIGHT / 2; r += 10) {
			fOffscreenGraphics.setColor(new Color(c, 125, 225));
			fOffscreenGraphics.fillRect(0, r, PROJECTIONPLANEWIDTH, 10);
			c += 20;
		}
		
		// ground
		c = 22;
		for (; r < PROJECTIONPLANEHEIGHT; r += 15) {
			fOffscreenGraphics.setColor(new Color(c, 20, 20));
			fOffscreenGraphics.fillRect(0, r, PROJECTIONPLANEWIDTH, 15);
			c += 15;
		}
	}

	// *******************************************************************//
	// * Draw map on the right side
	// *******************************************************************//
	public void drawOverheadMap() {
		fMinimapWidth = 10;
		for (int u = 0; u < MAP_WIDTH; u++) {
			for (int v = 0; v < MAP_HEIGHT; v++) {
				if (worldMap[v * MAP_WIDTH + u] == W) {
					fOffscreenGraphics.setColor(Color.red);
				} else {
					fOffscreenGraphics.setColor(Color.black);
				}
				int x0 = PROJECTIONPLANEWIDTH + (u * fMinimapWidth);
				int y0 = (v * fMinimapWidth);

				fOffscreenGraphics.fillRect(x0, y0, fMinimapWidth, fMinimapWidth);
			}
		}
		fPlayerMapX = PROJECTIONPLANEWIDTH + (int) (((float) playerPosX / (float) TILE_SIZE) * fMinimapWidth);
		fPlayerMapY = (int) (((float) playerPosY / (float) TILE_SIZE) * fMinimapWidth);
	}

	// *******************************************************************//
	// * Draw ray on the overhead map (for illustartion purpose)
	// * This is not part of the ray-casting process
	// *******************************************************************//
	public void drawRayOnOverheadMap(float x, float y) {
		fOffscreenGraphics.setColor(Color.yellow);
		// draw line from the player position to the position where the ray intersect with wall
		int rayTargetX = (int) (PROJECTIONPLANEWIDTH + ((float) (x * fMinimapWidth) / (float) TILE_SIZE));
		int rayTargetY = (int) (((float) (y * fMinimapWidth) / (float) TILE_SIZE));
		fOffscreenGraphics.drawLine(fPlayerMapX, fPlayerMapY, rayTargetX, rayTargetY);
		
		// draw a red line indication the player's direction
		//this is repeated, 
		fOffscreenGraphics.setColor(Color.red);
		
		int faceTargetX =(int) (fPlayerMapX + cosTable[playerArc] * 10);
		int faceTargetY = (int) (fPlayerMapY + sinTable[playerArc] * 10);
		fOffscreenGraphics.drawLine(fPlayerMapX, fPlayerMapY, faceTargetX, faceTargetY);
	}

	public void render() {
		drawBackground();
		drawOverheadMap();

		// Horizontal or vertical coordinate of intersection theoretically, this
		// will be multiple of TILE_SIZE, but some trick did here might cause
		// the values off by 1
		int verticalGrid; 
		int horizontalGrid; 

		// how far to the next bound (this is multiple of tile size)
		int distToNextVerticalGrid; 
		int distToNextHorizontalGrid; 
		
		float xIntersection; 
		float yIntersection;
		
		float distToNextXIntersection;
		float distToNextYIntersection;

		// the current cell that the ray is in
		int xGridIndex; 
		int yGridIndex;

		// the distance of the x and y ray intersections from the viewpoint
		float distToVerticalGridBeingHit; 
		float distToHorizontalGridBeingHit; 

		int castArc, castColumn;

		castArc = playerArc;
        // field of view is 60 degree with the point of view (player's direction in the middle)
        // 30  30
        //    ^
        //  \ | /
        //   \|/
        //    v
        // we will trace the rays starting from the leftmost ray
        castArc-=ANGLE30;
      // wrap around if necessary
		if (castArc < 0) {
			castArc += ANGLE360;
		}

        // ray is between 0 to 180 degree (1st and 2nd quadrant)
		for (castColumn = 0; castColumn < PROJECTIONPLANEWIDTH; castColumn += 5) {
			// ray is facing down
			if (castArc > ANGLE0 && castArc < ANGLE180) {
            // truncuate then add to get the coordinate of the FIRST grid (horizontal
            // wall) that is in front of the player (this is in pixel unit)
            // ROUND DOWN
				horizontalGrid = (playerPosY / TILE_SIZE) * TILE_SIZE + TILE_SIZE;

				// compute distance to the next horizontal wall
				distToNextHorizontalGrid = TILE_SIZE;

				float xtemp = itanTable[castArc] * (horizontalGrid - playerPosY);
				// we can get the vertical distance to that wall by
				// (horizontalGrid-GLplayerY)
				// we can get the horizontal distance to that wall by
				// 1/tan(arc)*verticalDistance
				// find the x interception to that wall
				xIntersection = xtemp + playerPosX;
			}
			// else, the ray is facing up
			else {
				horizontalGrid = (playerPosY / TILE_SIZE) * TILE_SIZE;
				distToNextHorizontalGrid = -TILE_SIZE;

				float xtemp = itanTable[castArc] * (horizontalGrid - playerPosY);
				xIntersection = xtemp + playerPosX;

				horizontalGrid--;
			}
			
			//todo
			// LOOK FOR HORIZONTAL WALL,			// else, move the ray until it hits a horizontal wall
			if (castArc == ANGLE0 || castArc == ANGLE180) {
				distToHorizontalGridBeingHit = 9999999F;// Float.MAX_VALUE;
			} else {
				distToNextXIntersection = xStepTable[castArc];
				while (true) {
					xGridIndex = (int) (xIntersection / TILE_SIZE);
					// in the picture, yGridIndex will be 1
					yGridIndex = (horizontalGrid / TILE_SIZE);

					if ((xGridIndex >= MAP_WIDTH) || (yGridIndex >= MAP_HEIGHT) || xGridIndex < 0 || yGridIndex < 0) {
						distToHorizontalGridBeingHit = Float.MAX_VALUE;
						break;
					} else if ((worldMap[yGridIndex * MAP_WIDTH + xGridIndex]) != O) {
						distToHorizontalGridBeingHit = (xIntersection - playerPosX) * icosTable[castArc];
						break;
					}
					// else, the ray is not blocked, extend to the next block
					else {
						xIntersection += distToNextXIntersection;
						horizontalGrid += distToNextHorizontalGrid;
					}
				}
			}

			// FOLLOW X RAY
			if (castArc < ANGLE90 || castArc > ANGLE270) {
				verticalGrid = TILE_SIZE + (playerPosX / TILE_SIZE) * TILE_SIZE;
				distToNextVerticalGrid = TILE_SIZE;

				float ytemp = tanTable[castArc] * (verticalGrid - playerPosX);
				yIntersection = ytemp + playerPosY;
			}
			// RAY FACING LEFT
			else {
				verticalGrid = (playerPosX / TILE_SIZE) * TILE_SIZE;
				distToNextVerticalGrid = -TILE_SIZE;

				float ytemp = tanTable[castArc] * (verticalGrid - playerPosX);
				yIntersection = ytemp + playerPosY;

				verticalGrid--;
			}
			// LOOK FOR VERTICAL WALL
			if (castArc == ANGLE90 || castArc == ANGLE270) {
				distToVerticalGridBeingHit = 9999999;// Float.MAX_VALUE;
			} else {
				distToNextYIntersection = yStepTable[castArc];
				while (true) {
					// compute current map position to inspect
					xGridIndex = (verticalGrid / TILE_SIZE);
					yGridIndex = (int) (yIntersection / TILE_SIZE);

					if ((xGridIndex >= MAP_WIDTH) || (yGridIndex >= MAP_HEIGHT) || xGridIndex < 0 || yGridIndex < 0) {
						distToVerticalGridBeingHit = Float.MAX_VALUE;
						break;
					} else if ((worldMap[yGridIndex * MAP_WIDTH + xGridIndex]) != O) {
						distToVerticalGridBeingHit = (yIntersection - playerPosY) * isinTable[castArc];
						break;
					} else {
						yIntersection += distToNextYIntersection;
						verticalGrid += distToNextVerticalGrid;
					}
				}
			}

			// DRAW THE WALL SLICE
			float scaleFactor;
			float dist;
			int topOfWall; // used to compute the top and bottom of the sliver that
			int bottomOfWall; // will be the staring point of floor and ceiling
			// determine which ray strikes a closer wall.
			// if yray distance to the wall is closer, the yDistance will be shorter than
			// the xDistance
			if (distToHorizontalGridBeingHit < distToVerticalGridBeingHit) {
				// the next function call (drawRayOnMap()) is not a part of raycating rendering part,
				// it just draws the ray on the overhead map to illustrate the raycasting process
				drawRayOnOverheadMap(xIntersection, horizontalGrid);
				dist = distToHorizontalGridBeingHit;
				fOffscreenGraphics.setColor(Color.gray);
			}
			// else, we use xray instead (meaning the vertical wall is closer than
			// the horizontal wall)
			else {
				// the next function call (drawRayOnMap()) is not a part of raycating rendering part,
				// it just draws the ray on the overhead map to illustrate the raycasting process
				drawRayOnOverheadMap(verticalGrid, yIntersection);
				dist = distToVerticalGridBeingHit;
				fOffscreenGraphics.setColor(Color.darkGray);
			}

			// correct distance (compensate for the fishbown effect)
			dist /= fishTable[castColumn];
			// projected_wall_height/wall_height = fPlayerDistToProjectionPlane/dist;
			int projectedWallHeight = (int) (WALL_HEIGHT * (float) playerDistanceToTheProjectionPlane / dist);
			bottomOfWall = fProjectionPlaneYCenter + (int) (projectedWallHeight * 0.5F);
			topOfWall = PROJECTIONPLANEHEIGHT - bottomOfWall;
			if (bottomOfWall >= PROJECTIONPLANEHEIGHT)
				bottomOfWall = PROJECTIONPLANEHEIGHT - 1;
			// fOffscreenGraphics.drawLine(castColumn, topOfWall, castColumn, bottomOfWall);
			fOffscreenGraphics.fillRect(castColumn, topOfWall, 5, projectedWallHeight);

			// TRACE THE NEXT RAY
			castArc += 5;
			if (castArc >= ANGLE360){
				castArc -= ANGLE360;
			}
		}

		// blit to screen
		paint(getGraphics());
	}

	public void stop() {
		if ((mainThread != null) && mainThread.isAlive()) {
			mainThread.stop();
			mainThread = null;
		}
	}

	public void paint(Graphics g) {
		if (fOffscreenImage != null)
			g.drawImage(fOffscreenImage, 0, 0, this);
	}

	public boolean keyDown(Event evt, int key) {
		switch (key) {
		case Event.UP:
		case 'i':
		case 'I':
			isKeyUp = true;
			break;
		case Event.DOWN:
		case 'k':
		case 'K':
			isKeyDown = true;
			break;
		case Event.LEFT:
		case 'j':
		case 'J':
			isKeyLeft = true;
			break;
		case Event.RIGHT:
		case 'l':
		case 'L':
			isKeyRight = true;
			break;
		default:
		}
		return true;
	}

	public boolean keyUp(Event evt, int key) {
		switch (key) {
		case Event.UP:
		case 'i':
		case 'I':
			isKeyUp = false;
			break;
		case Event.DOWN:
		case 'k':
		case 'K':
			isKeyDown = false;
			break;
		case Event.LEFT:
		case 'j':
		case 'J':
			isKeyLeft = false;
			break;
		case Event.RIGHT:
		case 'l':
		case 'L':
			isKeyRight = false;
			break;
		default:
		}
		return true;
	}
}


