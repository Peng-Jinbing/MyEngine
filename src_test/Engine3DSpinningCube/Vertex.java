package Engine3DSpinningCube;
public class Vertex
{
  public int fLx;
  public int fLy;
  public int fLz;
  public int fLt;
  public int fWx;
  public int fWy;
  public int fWz;
  public int fWt;
  public int fSx;
  public int fSy;
  public int fSz;

  public Vertex()
  {

  }

  public Vertex(int lx, int ly, int lz)
  {
    fLx=lx;
    fLy=ly;
    fLz=lz;
    fLt=1;

    fWx=fWy=fWz=0;
    fWy=1;
    fSx=fSy=fSz=0;
  }

  public void SetLocal(int lx, int ly, int lz)
  {
    fLx=lx;
    fLy=ly;
    fLz=lz;
    fLt=1;

    fWx=fWy=fWz=0;
    fWt=1;
    fSx=fSy=fSz=0;
  }


  public void SetWorld(int wx, int wy, int wz)
  {
    fWx=wx;
    fWy=wy;
    fWz=wz;
  }

  public void SetScreen(int sx, int sy)
  {
    fSx=sx;
    fSy=sy;
  }
}
