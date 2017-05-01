package engine.terrain;

import org.joml.Vector2f;

public class ProceduralTerrainGenerator implements TerrainGenerator {
	
	private static final float SIZE = 150;
	
	private Vector2f offset, size;

	private static final int RESOLUTION = 5;
	
	private static final float NOISE_RESOLUTION = 0.01f;
	
	private OpenSimplexNoise noise;
	
	private float maxHeight, minHeight;

	public ProceduralTerrainGenerator(long seed, float maxHeight, float minHeight, int gridX, int gridZ) {
		offset = new Vector2f(gridX * SIZE, gridZ * SIZE);
		size = new Vector2f(SIZE, SIZE);
		noise = new OpenSimplexNoise(seed);
		this.maxHeight = maxHeight;
		this.minHeight = minHeight;
	}

	@Override
	public float getTileSize() {
		return RESOLUTION;
	}
	
	@Override
	public float getHeightAt(float x, float z) {
		return ((float) noise.eval(x * NOISE_RESOLUTION, z * NOISE_RESOLUTION)) * (maxHeight - minHeight) + minHeight;
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
		return (int) (SIZE / RESOLUTION);
	}

}
