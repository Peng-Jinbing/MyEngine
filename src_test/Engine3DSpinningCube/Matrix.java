package Engine3DSpinningCube;
public class Matrix
{
  static float fScalingMatrix[][]=new float[4][4];
  static float fTranslationMatrix[][]=new float[4][4];
  static float fXRotationMatrix[][]=new float[4][4];
  static float fYRotationMatrix[][]=new float[4][4];
  static float fZRotationMatrix[][]=new float[4][4];

  float fMatrix[][]=new float[4][4];

  public Matrix()
  {
  }

  public Matrix(
    int r0a, int r0b, int r0c, int r0d,
    int r1a, int r1b, int r1c, int r1d,
    int r2a, int r2b, int r2c, int r2d,
    int r3a, int r3b, int r3c, int r3d)
  {
    fMatrix[0][0]=r0a;
    fMatrix[0][1]=r0b;
    fMatrix[0][2]=r0c;
    fMatrix[0][3]=r0d;

    fMatrix[1][0]=r1a;
    fMatrix[1][1]=r1b;
    fMatrix[1][2]=r1c;
    fMatrix[1][3]=r1d;

    fMatrix[2][0]=r2a;
    fMatrix[2][1]=r2b;
    fMatrix[2][2]=r2c;
    fMatrix[2][3]=r2d;

    fMatrix[3][0]=r3a;
    fMatrix[3][1]=r3b;
    fMatrix[3][2]=r3c;
    fMatrix[3][3]=r3d;
  }

  public Matrix Multiply(Matrix matrix)
  {
    Matrix result=new Matrix();
//    float result[][]=new float[4][4];

    for (int i=0;i<4;i++)
    {
      for (int j=0;j<4;j++)
      {
        result.fMatrix[i][j]=0;
        for (int k=0;k<4;k++)
          result.fMatrix[i][j]+=matrix.fMatrix[i][k]*this.fMatrix[k][j];
      }
    }

    return result;
  }

  public Matrix Multiply(float[][] matrix)
  {
    Matrix result=new Matrix();

    for (int i=0;i<4;i++)
    {
      for (int j=0;j<4;j++)
      {
        result.fMatrix[i][j]=0;
        for (int k=0;k<4;k++)
          result.fMatrix[i][j]+=this.fMatrix[i][k]*matrix[k][j];
      }
    }

    return result;
  }

  public void CopyFrom(Matrix source)
  {
    for (int i=0;i<4;i++)
      for (int j=0;j<4;j++)
        this.fMatrix[i][j]=source.fMatrix[i][j];
  }

  public void CopyFrom(float[][] source)
  {
    for (int i=0;i<4;i++)
      for (int j=0;j<4;j++)
        this.fMatrix[i][j]=source[i][j];
  }

  public void Scale(float sf)
  {
    fScalingMatrix[0][0]=sf;
    fScalingMatrix[0][1]=0;
    fScalingMatrix[0][2]=0;
    fScalingMatrix[0][3]=0;

    fScalingMatrix[1][0]=0;
    fScalingMatrix[1][1]=sf;
    fScalingMatrix[1][2]=0;
    fScalingMatrix[1][3]=0;

    fScalingMatrix[2][0]=0;
    fScalingMatrix[2][1]=0;
    fScalingMatrix[2][2]=sf;
    fScalingMatrix[2][3]=0;

    fScalingMatrix[3][0]=0;
    fScalingMatrix[3][1]=0;
    fScalingMatrix[3][2]=0;
    fScalingMatrix[3][3]=1;

    Matrix temp=Multiply(fScalingMatrix);
    CopyFrom(temp);
  }

  void Translate(int xt, int yt, int zt)
  {
    fTranslationMatrix[0][0]=1;
    fTranslationMatrix[0][1]=0;
    fTranslationMatrix[0][2]=0;
    fTranslationMatrix[0][3]=0;

    fTranslationMatrix[1][0]=0;
    fTranslationMatrix[1][1]=1;
    fTranslationMatrix[1][2]=0;
    fTranslationMatrix[1][3]=0;

    fTranslationMatrix[2][0]=0;
    fTranslationMatrix[2][1]=0;
    fTranslationMatrix[2][2]=1;
    fTranslationMatrix[2][3]=0;

    fTranslationMatrix[3][0]=xt;
    fTranslationMatrix[3][1]=yt;
    fTranslationMatrix[3][2]=zt;
    fTranslationMatrix[3][3]=1;

    Matrix temp=Multiply(fTranslationMatrix);
    CopyFrom(temp);
  }

  void Rotate(double ax, double ay, double az)
  {
    Matrix temp1;
    Matrix temp2;
    Matrix temp3;

    fXRotationMatrix[0][0]=1;
    fXRotationMatrix[0][1]=0;
    fXRotationMatrix[0][2]=0;
    fXRotationMatrix[0][3]=0;

    fXRotationMatrix[1][0]=0;
    fXRotationMatrix[1][1]=(float)Math.cos(ax);
    fXRotationMatrix[1][2]=(float)Math.sin(ax);
    fXRotationMatrix[1][3]=0;

    fXRotationMatrix[2][0]=0;
    fXRotationMatrix[2][1]=-(float)Math.sin(ax);
    fXRotationMatrix[2][2]=(float)Math.cos(ax);
    fXRotationMatrix[2][3]=0;

    fXRotationMatrix[3][0]=0;
    fXRotationMatrix[3][1]=0;
    fXRotationMatrix[3][2]=0;
    fXRotationMatrix[3][3]=1;

    temp1=Multiply(fXRotationMatrix);
    CopyFrom(temp1);

    fYRotationMatrix[0][0]=(float)Math.cos(ay);
    fYRotationMatrix[0][1]=0;
    fYRotationMatrix[0][2]=-(float)Math.sin(ay);
    fYRotationMatrix[0][3]=0;

    fYRotationMatrix[1][0]=0;
    fYRotationMatrix[1][1]=1;
    fYRotationMatrix[1][2]=0;
    fYRotationMatrix[1][3]=0;

    fYRotationMatrix[2][0]=(float)Math.sin(ay);
    fYRotationMatrix[2][1]=0;
    fYRotationMatrix[2][2]=(float)Math.cos(ay);
    fYRotationMatrix[2][3]=0;

    fYRotationMatrix[3][0]=0;
    fYRotationMatrix[3][1]=0;
    fYRotationMatrix[3][2]=0;
    fYRotationMatrix[3][3]=1;

    temp2=Multiply(fYRotationMatrix);
    CopyFrom(temp2);

    fZRotationMatrix[0][0]=(float)Math.cos(az);
    fZRotationMatrix[0][1]=(float)Math.sin(az);
    fZRotationMatrix[0][2]=0;
    fZRotationMatrix[0][3]=0;

    fZRotationMatrix[1][0]=-(float)Math.sin(az);
    fZRotationMatrix[1][1]=(float)Math.cos(az);
    fZRotationMatrix[1][2]=0;
    fZRotationMatrix[1][3]=0;

    fZRotationMatrix[2][0]=0;
    fZRotationMatrix[2][1]=0;
    fZRotationMatrix[2][2]=1;
    fZRotationMatrix[2][3]=0;

    fZRotationMatrix[3][0]=0;
    fZRotationMatrix[3][1]=0;
    fZRotationMatrix[3][2]=0;
    fZRotationMatrix[3][3]=1;

    temp3=Multiply(fZRotationMatrix);
    CopyFrom(temp3);
  }

  public void InitTrans()
  {
    fMatrix[0][0]=1;
    fMatrix[0][1]=0;
    fMatrix[0][2]=0;
    fMatrix[0][3]=0;

    fMatrix[1][0]=0;
    fMatrix[1][1]=1;
    fMatrix[1][2]=0;
    fMatrix[1][3]=0;

    fMatrix[2][0]=0;
    fMatrix[2][1]=0;
    fMatrix[2][2]=1;
    fMatrix[2][3]=0;

    fMatrix[3][0]=0;
    fMatrix[3][1]=0;
    fMatrix[3][2]=0;
    fMatrix[3][3]=1;
  }
}



