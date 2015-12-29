package RaycaseEngine;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Event;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;

///////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////
public class CMBubble extends Applet implements Runnable
{
Thread mThread = null;
int mAppletWidth, mAppletHeight;
Image mOffScrImage;
Graphics mOffScrGraphics;
boolean	mStillDrawingOffScreen;

//status code
int mStatus;
static final int PLAYING=2;
static final int LOADING=3;

//background image
Image mBgrImage;

//FONTS
static final Font mFont1=new Font("Helvetica", Font.BOLD, 12);

static final boolean DEBUG=false;

CBub mBubbles[];
int mNumOfBubbles;
int mCurrentBubble;
int mMouseX, mMouseY, mDir;
boolean mUserHasMoved;

/////////////////////////////////////////////////////////////////////
//Create background image
/////////////////////////////////////////////////////////////////////
public void createBackground()
{
String s=getParameter("bgrImage");
if (s!=null)
mBgrImage=loadImage(s);
else
{
mBgrImage=createImage(mAppletWidth, mAppletHeight);
Graphics g=mBgrImage.getGraphics();
g.setColor(Color.red);
g.fillRect(0,0,mAppletWidth,mAppletHeight);
}
}

/////////////////////////////////////////////////////////////////////
//standard constructor
/////////////////////////////////////////////////////////////////////
public CMBubble()
{
}

/////////////////////////////////////////////////////////////////////
//standard applet info
/////////////////////////////////////////////////////////////////////
public String getAppletInfo()
{
return "Buble, programmed by F. Permadi";
}

/////////////////////////////////////////////////////////////////////
//standard applet initializer
/////////////////////////////////////////////////////////////////////
public void init()
{
}

/////////////////////////////////////////////////////////////////////
//load image
/////////////////////////////////////////////////////////////////////
public Image loadImage(String imageName)
{
MediaTracker tracker=new MediaTracker(this);

//Load image
Image i=getImage(getCodeBase(),imageName);
tracker.addImage(i,0);
try
{
tracker.waitForID(0);
}
catch (InterruptedException e)
{
System.out.println(e);
}
return i;
}


/////////////////////////////////////////////////////////////////////
//for painting the applet
/////////////////////////////////////////////////////////////////////
public void update(Graphics g)
{
if (!mStillDrawingOffScreen)
g.drawImage(mOffScrImage,0,0,this);
}

public void drawOffScreen()
{
mStillDrawingOffScreen=true;
if (mStatus==PLAYING)
{
// draw background
mOffScrGraphics.drawImage(mBgrImage,0,0,this);

for (int i=0;i<mNumOfBubbles; i++)
{
mBubbles[i].draw();
mBubbles[i].move();
}
}
else if (mStatus==LOADING)
{
mOffScrGraphics.setColor(Color.black);
mOffScrGraphics.fillRect(0,0,size().width, size().height);
mOffScrGraphics.setFont(mFont1);
drawTextCentered(0, mAppletHeight/2, mAppletWidth,
"LOADING...", mOffScrGraphics);
}
mStillDrawingOffScreen=false;
}

////////////////////////////////////////////////////////////////////
//standard applet startup
/////////////////////////////////////////////////////////////////////
public void start()
{
if (mThread == null)
{
mThread = new Thread(this);
mThread.start();
}
requestFocus();
}

/////////////////////////////////////////////////////////////////////
//this will be called when user leave the applet page
/////////////////////////////////////////////////////////////////////
public void stop()
{
if (mThread != null)
{
mThread.stop();
mThread = null;
}
}

/////////////////////////////////////////////////////////////////////
//create the game image/bitmap/objects, etc
//this should only be called once
/////////////////////////////////////////////////////////////////////
public void createGame()
{
// create offscreen buffer
mAppletWidth=size().width;
mAppletHeight=size().height;
mOffScrImage=createImage(mAppletWidth, mAppletHeight);
mOffScrGraphics=mOffScrImage.getGraphics();

// show "LOADING" message
mStatus=LOADING;
drawOffScreen();
repaint();

// create background
mBgrImage=createImage(mAppletWidth, mAppletHeight);
createBackground();

CBub.setupStatic(mOffScrGraphics, mAppletWidth, 
mAppletHeight, this);
mNumOfBubbles=30;
mBubbles=new CBub[mNumOfBubbles];
for (int i=0; i<mNumOfBubbles; i++)
mBubbles[i]=new CBub();
}

/////////////////////////////////////////////////////////////////////
//applet thread runner
/////////////////////////////////////////////////////////////////////
public void run()
{
int delay=50;
createGame();
mStatus=PLAYING;
mUserHasMoved=false;
mMouseX=mAppletWidth/2;
mMouseY=mAppletHeight-20;

while (true)
{
drawOffScreen();
repaint();

if (!mUserHasMoved)
{
if ((int)(Math.random()*2)==1)
{
mMouseX=mAppletWidth/2+(int)(Math.random()*20);
mDir=CBub.RIGHT;
}
else
{
mMouseX=mAppletWidth/2-(int)(Math.random()*20);
mDir=CBub.LEFT;
}
}

if (mCurrentBubble<mNumOfBubbles)
mBubbles[mCurrentBubble].deploy(mMouseX-(mBubbles[0].mWidth>>1), 
mMouseY-(mBubbles[0].mHeight>>1), mDir);
mCurrentBubble++;
if (mCurrentBubble>=mNumOfBubbles)
mCurrentBubble=0;

try
{
Thread.sleep(delay);
}
catch (InterruptedException e)
{
stop();
}
}
}

///////////////////////////////////////////////////////////////
//The user has clicked in the applet.
///////////////////////////////////////////////////////////////
public boolean mouseDown(Event evt, int x, int y)
{
mCurrentBubble=0;
return true;
}

public boolean mouseDrag(Event  evt, int  x, int  y)
{
mUserHasMoved=true;
if (mMouseX>x)
mDir=CBub.RIGHT;
else
mDir=CBub.LEFT;
mMouseX=x;
mMouseY=y;
return true;
}

public boolean mouseMove(Event  evt, int  x, int  y)
{
mUserHasMoved=true;
if (mMouseX>x)
mDir=CBub.RIGHT;
else
mDir=CBub.LEFT;
mMouseX=x;
mMouseY=y;
return true;
}

public boolean mouseExit(Event  evt, int  x, int  y)
{
mUserHasMoved=false;
return false;
}

///////////////////////////////////////////////////////////////
//draw raised texts on the center
///////////////////////////////////////////////////////////////
public void drawTextCentered(
int left, int top, int right, String s, Graphics g)
{
FontMetrics fm=g.getFontMetrics();
int x=left+(((right-left)-fm.stringWidth(s))>>1);

g.setColor(Color.white);
g.drawString(s, x, top);
}
}

