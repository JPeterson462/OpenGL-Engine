package engine.terrain;

import java.util.ArrayList;

import org.joml.Vector2f;
import org.joml.Vector3f;

import engine.Engine;
import engine.rendering.Geometry;
import engine.rendering.Vertex;

public class TerrainTile {
	
	private Geometry geometry;
	
	private TerrainTexturePack texturePack;
	
	private TerrainGenerator generator;
	
	public TerrainTile(TerrainGenerator generator, Engine engine, TerrainTexturePack texturePack) {
		this.generator = generator;
		float tileSize = generator.getTileSize();
		generateGeometry(tileSize, engine);
		this.texturePack = texturePack;
	}
	
	public Vector2f getOffset() {
		return generator.getOffset();
	}
	
	public Vector2f getSize() {
		return generator.getSize();
	}
	
	public float getHeightAt(float x, float z) {
		return generator.getHeightAt(x, z);
	}
	
	public boolean pointInTile(float x, float z) {
		Vector2f offset = getOffset(), size = getSize();
		return x >= offset.x && z >= offset.y && x < (offset.x + size.x) && z < (offset.y + size.y);
	}
	
	private void generateGeometry(float tileSize, Engine engine) {
		Vector2f size = getSize();
		ArrayList<Vertex> vertices = new ArrayList<>();
		final int VERTEX_COUNT = generator.getVertexCount();
		for (int i = 0; i < VERTEX_COUNT; i++) {
			for (int j = 0; j < VERTEX_COUNT; j++) {
				float x = getOffset().x + (float) j / ((float) VERTEX_COUNT - 1) * size.x;
				float z = getOffset().y + (float) i / ((float) VERTEX_COUNT - 1) * size.y;
				float y = getHeightAt(x, z);
				Vector3f position = new Vector3f(x, y, z);
				Vector3f normal = calculateNormal(j, i);
				Vector2f texCoord = new Vector2f((float) j / ((float) VERTEX_COUNT - 1), (float) i / ((float) VERTEX_COUNT - 1));
				vertices.add(new Vertex(position, texCoord, normal));
			}
		}
		ArrayList<Integer> indices = new ArrayList<>();
		for (int gz = 0; gz < VERTEX_COUNT - 1; gz++) {
			for (int gx = 0; gx < VERTEX_COUNT - 1; gx++) {
				int topLeft = (gz * VERTEX_COUNT) + gx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gz + 1) * VERTEX_COUNT) + gx;
				int bottomRight = bottomLeft + 1;
				indices.add(topLeft);
				indices.add(bottomLeft);
				indices.add(topRight);
				indices.add(topRight);
				indices.add(bottomLeft);
				indices.add(bottomRight);
			}
		}
		geometry = engine.getRenderingBackend().createGeometry(vertices, indices);
	}
	
	private Vector3f calculateNormal(int x, int z) {
		float heightL = getHeightAt(x - 1, z);
		float heightR = getHeightAt(x + 1, z);
		float heightD = getHeightAt(x, z - 1);
		float heightU = getHeightAt(x, z + 1);
		Vector3f normal = new Vector3f(heightL - heightR, 2f, heightD - heightU);
		normal.normalize();
		return normal;
	}
	
	public Geometry getGeometry() {
		return geometry;
	}
	
	public TerrainTexturePack getTexturePack() {
		return texturePack;
	}

}
