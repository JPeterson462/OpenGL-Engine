package engine.rendering.deferred;

import java.util.ArrayList;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import engine.Camera;
import engine.Engine;
import engine.rendering.Framebuffer;
import engine.rendering.Geometry;
import engine.rendering.Shader;
import engine.rendering.Texture;
import engine.rendering.Vertex;
import engine.water.Water;
import engine.water.WaterTile;

public class WaterRenderer {

	public static float WATER_HEIGHT = 50;

	private static final float WAVE_SPEED = 0.01f;
	
	private Matrix4f modelMatrix = new Matrix4f();

	private Geometry geometry;
	
	private float moveFactor = 0;
	
	private Vector2f viewPlane = new Vector2f();
	
	private Texture dudvMap;
	
	public WaterRenderer(Engine engine, Texture dudvMap) {
		ArrayList<Vertex> vertices = new ArrayList<>();
		vertices.add(new Vertex(new Vector3f(-1, -1, 0)));
		vertices.add(new Vertex(new Vector3f(1, -1, 0)));
		vertices.add(new Vertex(new Vector3f(1, 1, 0)));
		vertices.add(new Vertex(new Vector3f(-1, 1, 0)));
		ArrayList<Integer> indices = new ArrayList<>();
		indices.add(0);
		indices.add(1);
		indices.add(2);
		indices.add(2);
		indices.add(3);
		indices.add(0);
		geometry = engine.getRenderingBackend().createGeometry(vertices, indices, true);
		this.dudvMap = dudvMap;
	}
	
	public void renderWater(Shader shader, Camera camera, Water water, float delta, 
			Engine engine, Framebuffer reflectionBuffer, Framebuffer refractionBuffer) {
		moveFactor += WAVE_SPEED * delta;
		if (moveFactor > 1) {
			moveFactor -= 1;
		}
		shader.bind();
		camera.uploadTo(shader);
		shader.uploadVector("cameraPosition", camera.getCenter());
		shader.uploadFloat("moveFactor", moveFactor);
		viewPlane.set(engine.getSettings().nearPlane, engine.getSettings().farPlane);
		shader.uploadVector("viewPlane", viewPlane);
		reflectionBuffer.getColorTexture(0).bind(0);
		refractionBuffer.getColorTexture(0).bind(1);
		dudvMap.bind(2);
		refractionBuffer.getDepthTexture().bind(3);
		geometry.bind();
		ArrayList<WaterTile> tiles = water.getTiles();
		for (int i = 0; i < tiles.size(); i++) {
			WaterTile tile = tiles.get(i);
			modelMatrix.translationRotateScale(tile.getX(), tile.getHeight(), tile.getZ(), 0, 0, 0, 1, WaterTile.SIZE, WaterTile.SIZE, WaterTile.SIZE);
			shader.uploadMatrix("modelMatrix", modelMatrix);
			geometry.renderGeometry();
		}
		geometry.unbind();
		reflectionBuffer.getColorTexture(0).unbind();
		refractionBuffer.getColorTexture(0).unbind();
		dudvMap.unbind();
		refractionBuffer.getDepthTexture().unbind();
		shader.unbind();
	}

	
}
