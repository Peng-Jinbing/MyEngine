package Engine3DSpinningCube;

import java.awt.*;

public class Shape
{
  int fColor;
  int fNumVertices;
  int fNumLines;
  Vertex fVertices[];
  Line fLines[];
  Graphics fOffScrGraphics;

  Shape(int numVertices, Vertex vertices[],
    int numLines, Line lines[], int color, 
	java.awt.Graphics offScrGraphics)
  {
    int i;
    fColor=color;
    fNumVertices=numVertices;
    fNumLines=numLines;

    if (fOffScrGraphics==null)
    {
      fOffScrGraphics=offScrGraphics;
    }

    fLines=new Line[numLines];
    // we don't declare new twice because this is the same
    // as pointer (ie:fLines[i] is pointing at lines[i]
    // not copying from it)
    for (i=0;i<numLines;i++)
      fLines[i]=lines[i];

    fVertices=new Vertex[numVertices];
    for (i=0;i<numVertices;i++)
      fVertices[i]=vertices[i];
  }

  public void Project(int distance)
  {
    for (int i=0;i<fNumVertices;i++)
    {
      fVertices[i].fSx=distance*fVertices[i].fWx/fVertices[i].fWz;
      fVertices[i].fSy=distance*fVertices[i].fWy/fVertices[i].fWz;
    }
  }

  public void Draw(int scrWidth, int scrHeight)
  {
    fOffScrGraphics.setColor(Color.red);
    fOffScrGraphics.clipRect(0,0,scrWidth,scrHeight);
    for (int i=0;i<fNumLines;i++)
    {
      fOffScrGraphics.drawLine(
        fVertices[fLines[i].fStart].fSx+(scrWidth>>1),
        fVertices[fLines[i].fStart].fSy+(scrHeight>>1),
        fVertices[fLines[i].fEnd].fSx+(scrWidth>>1),
        fVertices[fLines[i].fEnd].fSy+(scrHeight>>1));
    }
  }

  public void Transform(Matrix tMatrix)
  {
    for (int i=0;i<fNumVertices;i++)
    {
      fVertices[i].fWx=(int)(fVertices[i].fLx*tMatrix.fMatrix[0][0]+
        fVertices[i].fLy*tMatrix.fMatrix[1][0]+
        fVertices[i].fLz*tMatrix.fMatrix[2][0]+
        tMatrix.fMatrix[3][0]);
      fVertices[i].fWy=(int)(fVertices[i].fLx*tMatrix.fMatrix[0][1]+
        fVertices[i].fLy*tMatrix.fMatrix[1][1]+
        fVertices[i].fLz*tMatrix.fMatrix[2][1]+
        tMatrix.fMatrix[3][1]);
      fVertices[i].fWz=(int)(fVertices[i].fLx*tMatrix.fMatrix[0][2]+
        fVertices[i].fLy*tMatrix.fMatrix[1][2]+
        fVertices[i].fLz*tMatrix.fMatrix[2][2]+
        tMatrix.fMatrix[3][2]);
    }
  }
}

