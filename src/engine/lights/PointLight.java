package engine.lights;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import engine.Engine;
import engine.rendering.Geometry;
import engine.rendering.Shader;
import utils.Polygons;

public class PointLight implements PositionalLight {
	
	private Vector4f color;
	
	private Vector3f position;
	
	private float range;
	
	private Geometry geometry;
	
	private Matrix4f modelMatrix;
	
	public PointLight(Vector4f color, Vector3f position, float range, Engine engine) {
		this.color = color;
		this.position = position;
		this.range = range;
		geometry = Polygons.newSphere(engine);
		modelMatrix = new Matrix4f();
		modelMatrix.translate(position);
		modelMatrix.scale(range);
	}

	@Override
	public Vector4f getColor() {
		return color;
	}

	@Override
	public Vector3f getPosition() {
		return position;
	}

	@Override
	public float getRange() {
		return range;
	}

	@Override
	public void uploadTo(Shader shader) {
		shader.uploadVector("lightColor", color);
		shader.uploadVector("lightPosition", position);
		shader.uploadFloat("lightRadius", range);
		shader.uploadMatrix("modelMatrix", modelMatrix);
	}

	@Override
	public void render() {
		geometry.bind();
		geometry.renderGeometry();
		geometry.unbind();
	}

	@Override
	public Matrix4f getModelMatrix() {
		return modelMatrix;
	}

}
