package RaycaseEngine;
///// F. Permadi 1998
///// Compiled with JDK 1.0

import java.applet.*;
import java.awt.*;
import java.awt.image.*;
import java.util.*;

/////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////
class CBub
{
static CMBubble mGame;
static Graphics mOffScrGraphics;
static int mAppletWidth, mAppletHeight;

// status code
int mStatus;
int mVDir, mHDir;
static final int DEAD=3;
static final int UP=1;
static final int DOWN=2;
static final int LEFT=3;
static final int RIGHT=4;

// PLAYER'S STUFF
int mStartX, mStartY;
int mLeftX, mTopY;
int mYSpeed, mXSpeed;
static int mWidth, mHeight;
static Image mImage;
int mLifeTime;

/////////////////////////////////////////////////////////////////////
// standard constructor
/////////////////////////////////////////////////////////////////////
public CBub()
{
	mStatus=DEAD;
}

static void setupStatic(Graphics g, int gameAreaWidth, int gameAreaHeight, CMBubble game)
{
	mOffScrGraphics=g;
	mAppletWidth=gameAreaWidth;
	mAppletHeight=gameAreaHeight;
	mGame=game;
	String s=game.getParameter("bubbleImage");
	if (s!=null)
		mImage=mGame.loadImage(s);
	else
	{
		int width=20;
		mImage=game.createImage(width,width);
		Graphics gr=mImage.getGraphics();
		int transColorValue=0xFF000000;
		Color transparentColor=new Color(transColorValue);
		gr.setColor(transparentColor);
		gr.fillRect(0,0,width,width);
		gr.setColor(Color.white);
		gr.fillOval(0,0,width-1,width-1);
		gr.setColor(Color.blue);
		gr.drawOval(0,0,width-1,width-1);

		// MAKE TRANSPARENT
	    //Allocate buffer to hold the image's pixels
		int pixels[] = new int[width * width];

		PixelGrabber pg = new PixelGrabber(mImage, 0, 0, 
			width, width, pixels, 0, width);
		try 
		{
			pg.grabPixels();
		} 
		catch (InterruptedException e)
		{
		};
		for (int i=0; i<width*width;i++)
		{
			if (pixels[i]==transColorValue)
				pixels[i]=0x00000000;
		}
		mImage = game.createImage(new MemoryImageSource(width, 
			width, pixels, 0, width));
	}

	mWidth=mImage.getWidth(null);
	mHeight=mImage.getHeight(null);
}

public void deploy(int x, int y, int dir)
{
	mLeftX=x;
	mTopY=y;
	mStatus=DOWN;
	mVDir=DOWN;
	mLifeTime=0;
	mHDir=dir;
}

public void move()
{
	if (mStatus==DEAD)
		return;

	mLifeTime++;

	if (mHDir==LEFT)
		mLeftX-=mXSpeed;
	else
		mLeftX+=mXSpeed;

	if ((mLifeTime%3)==0)
	{
		mXSpeed++;
		if (mXSpeed>10)
			mXSpeed=10;
	}

	if (mVDir==DOWN)
	{
		mTopY+=mYSpeed;
		mYSpeed++;
		if (mTopY+mHeight>=mAppletHeight)
		{
			mTopY=mAppletHeight-mHeight;
			mVDir=UP;
			mYSpeed=3;
			mXSpeed=3;
		}
	}
	else if (mVDir==UP)
	{
		mTopY-=mYSpeed;
		mYSpeed++;
		if (mTopY<=0)
		{
			mTopY=0;
			mVDir=DOWN;
			mYSpeed=3;
			mXSpeed=3;
		}
	}
}

public void draw()
{
	if (mStatus==DEAD)
		return;

	mOffScrGraphics.drawImage(mImage, 
		mLeftX, mTopY,null);
}
}

