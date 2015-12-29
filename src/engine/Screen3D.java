package engine;

public class Screen3D extends Texture {

	public Screen3D(int width, int height) {
		super(width, height);
	}

	public void renderFloor(Ticker ticker) {
		double centerX = width/2.0;
		double centerY = height/2.0;

		for (int y = 0; y < height; y++) {
			double yd = ((y+0.5) - centerY)/height; // -0.5 0.5
			if(yd<0){
				yd = -yd;
			}
			
			double z =16/ yd;

			for (int x = 0; x < width; x++) {
				double xd = (x - centerX) /height;// width;

				xd = xd * z;
				int xx = (int) (xd) & 15;
				int zz = (int) (z*ticker.time) & 15;

				pixels[x + y * width] =Art.floors.pixels[xx+zz*64];// xx * 16 | (zz * 16 )<<8;
			}
		}
	}
}


//1:00:53
//1:09:09