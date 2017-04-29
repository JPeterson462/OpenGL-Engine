package engine.terrain;

import org.joml.Vector2f;

public interface TerrainGenerator {

	public float getTileSize();
	
	public float getHeightAt(float x, float z);
	
	public Vector2f getOffset();
	
	public Vector2f getSize();
	
	public int getVertexCount();

}
