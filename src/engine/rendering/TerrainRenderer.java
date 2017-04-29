package engine.rendering;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import engine.Camera;
import engine.terrain.Terrain;
import engine.terrain.TerrainTile;

public class TerrainRenderer {
	
	private Shader shader;
	
	private Terrain terrain;
	
	private Matrix4f emptyMatrix = new Matrix4f();
	
	public TerrainRenderer(Shader shader, Terrain terrain) {
		this.shader = shader;
		this.terrain = terrain;
	}
	
	public void render(Camera camera, Light[] lights, int lightCount, int maxLights, Vector3f skyColor, Vector4f clipPlane) {
//		engine.setCulling(false);
		shader.bind();
		camera.uploadTo(shader);
		shader.uploadVector("plane", clipPlane);
		for (int i = 0; i < maxLights; i++) {
			if (i < lightCount) {
				shader.uploadVector("lightPosition[" + i + "]", lights[i].getPosition());
				shader.uploadVector("lightColor[" + i + "]", lights[i].getColor());
				shader.uploadVector("attenuation[" + i + "]", lights[i].getAttenuation());
			} else {
				shader.uploadVector("lightPosition[" + i + "]", new Vector3f());
				shader.uploadVector("lightColor[" + i + "]", new Vector3f());
				shader.uploadVector("attenuation[" + i + "]", new Vector3f(1, 0, 0));
			}
		}
		shader.uploadVector("skyColor", skyColor);
		TerrainTile[][] tiles = terrain.getTiles();
		Vector3f player = camera.getCenter();
		boolean found = false;
		for (int x = 0; x < tiles.length && !found; x++) {
			for (int z = 0; z < tiles[0].length && !found; z++) {
				if (tiles[x][z].pointInTile(player.x, player.z)) {
					tryRenderTile(x - 1, z - 1, tiles);
					tryRenderTile(x + 0, z - 1, tiles);
					tryRenderTile(x + 1, z - 1, tiles);
					tryRenderTile(x - 1, z + 0, tiles);
					tryRenderTile(x + 0, z + 0, tiles);
					tryRenderTile(x + 1, z + 0, tiles);
					tryRenderTile(x - 1, z + 1, tiles);
					tryRenderTile(x + 0, z + 1, tiles);
					tryRenderTile(x + 1, z + 1, tiles);
					found = true;
				}
			}
		}
		shader.unbind();
//		engine.setCulling(true);
	}
	
	private void tryRenderTile(int x, int z, TerrainTile[][] tiles) {
		if (x < 0 || z < 0)
			return;
		if (x >= tiles.length || z >= tiles[0].length)
			return;
		renderTile(tiles[x][z]);
	}
	
	private void renderTile(TerrainTile tile) {
		tile.getGeometry().bind();
		tile.getTexturePack().bind(shader);
		shader.uploadMatrix("modelMatrix", emptyMatrix);
		tile.getGeometry().renderGeometry();
		tile.getTexturePack().unbind();
		tile.getGeometry().unbind();
	}

}
