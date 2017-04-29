package engine.water;

public class WaterTile {
	
	public static final float SIZE = 60f;
	
	private float height, x, z;
	
	public WaterTile(float x, float height, float z) {
		this.x = x;
		this.z = z;
		this.height = height;
	}
	
	public float getX() {
		return x;
	}
	
	public float getHeight() {
		return height;
	}
	
	public float getZ() {
		return z;
	}

}
