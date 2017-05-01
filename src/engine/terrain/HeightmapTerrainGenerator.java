package engine.terrain;

import org.joml.Vector2f;
import org.joml.Vector3f;

import engine.RawImage;
import utils.MathUtils;

public class HeightmapTerrainGenerator implements TerrainGenerator {

	private static final float SIZE = 150;
	
	private float[][] heights;
	
	private Vector2f offset, size;
	
	private static final float MAX_COLOR = 256 * 256 * 256;
	
	private RawImage heightmap;

	public HeightmapTerrainGenerator(RawImage heightmap, float maxHeight, float minHeight, int gridX, int gridZ) {
		offset = new Vector2f(gridX * SIZE, gridZ * SIZE);
		heights = new float[heightmap.getWidth()][heightmap.getHeight()];
		for (int x = 0; x < heightmap.getWidth(); x++) {
			for (int y = 0; y < heightmap.getHeight(); y++) {
				float height = heightmap.getRGB(x, y);
				height /= MAX_COLOR;
				heights[x][y] = interpolate(minHeight, maxHeight, height);
			}
		}
		size = new Vector2f(SIZE, SIZE);
		this.heightmap = heightmap;
	}
	
	private float interpolate(float a, float b, float t) {
		return (b - a) * t + a;
	}

	@Override
	public float getTileSize() {
		return SIZE / ((float) heights.length - 1);
	}
	
	@Override
	public float getHeightAt(float x, float z) {
		float terrainX = x - offset.x;
		float terrainZ = z - offset.y;
		float gridSquareSize = getTileSize();
		int gridX = (int) Math.floor(terrainX / gridSquareSize);
		int gridZ = (int) Math.floor(terrainZ / gridSquareSize);
		gridX = MathUtils.clamp(gridX, 0, heights.length - 2);
		gridZ = MathUtils.clamp(gridZ, 0, heights[0].length - 2);
		float xCoord = (terrainX % gridSquareSize) / gridSquareSize;
		float zCoord = (terrainZ % gridSquareSize) / gridSquareSize;
		float answer;
		if (xCoord <= (1 - zCoord)) {
			answer = MathUtils.barryCentric(new Vector3f(0, heights[gridX][gridZ], 0), new Vector3f(1, heights[gridX + 1][gridZ], 0), 
					new Vector3f(0, heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
		} else {
			answer = MathUtils.barryCentric(new Vector3f(1, heights[gridX + 1][gridZ], 0), new Vector3f(1, heights[gridX + 1][gridZ + 1], 1), 
					new Vector3f(0, heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
		}
		return answer;
	}

	@Override
	public Vector2f getOffset() {
		return offset;
	}

	@Override
	public Vector2f getSize() {
		return size;
	}

	@Override
	public int getVertexCount() {
		return heightmap.getHeight();
	}

}
