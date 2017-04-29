package engine.rendering;

import java.util.ArrayList;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import engine.Camera;
import engine.Engine;
import engine.water.Water;
import engine.water.WaterTile;

public class WaterRenderer {
	
	private Shader shader;
	
	private Geometry geometry;
	
	private Matrix4f modelMatrix;
	
	private Water water;
	
	private Framebuffer reflectionBuffer, refractionBuffer;
	
	public WaterRenderer(Shader shader, Engine engine, Water water) {
		this.shader = shader;
		modelMatrix = new Matrix4f();
		this.water = water;
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
		geometry = engine.getRenderingBackend().createGeometry(vertices, indices);
		reflectionBuffer = engine.getRenderingBackend().createFramebuffer(engine.getSettings().width, engine.getSettings().height, 1, true);
		refractionBuffer = engine.getRenderingBackend().createFramebuffer(engine.getSettings().width / 4, engine.getSettings().height / 4, 1, false);
	}
	
	public void render(Camera camera, PlaneRender sceneRenderCall) {
		float waterHeight = 22;
		float distanceFromWater = camera.getCenter().y - waterHeight;
		camera.getCenter().y -= 2 * distanceFromWater;
		camera.update();
		reflectionBuffer.bind();
		sceneRenderCall.renderPlane(new Vector4f(0, 1, 0, -waterHeight));
		reflectionBuffer.unbind();
		camera.getCenter().y += 2 * distanceFromWater;
		camera.update();
		refractionBuffer.bind();
		sceneRenderCall.renderPlane(new Vector4f(0, -1, 0, waterHeight));
		refractionBuffer.unbind();
		sceneRenderCall.renderPlane(new Vector4f(0, 0, 0, 0));
		shader.bind();
		camera.uploadTo(shader);
		reflectionBuffer.getColorTexture(0).bind(0);
		refractionBuffer.getColorTexture(0).bind(1);
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
		shader.unbind();
	}

}
