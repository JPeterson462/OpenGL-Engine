package engine.lights;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import engine.Assets;
import engine.rendering.Geometry;
import engine.rendering.Shader;

public class AmbientLight implements Light {
	
	private Vector4f color;
	
	private Vector3f position = new Vector3f();
	
	private Matrix4f modelMatrix = new Matrix4f();
	
	private Geometry geometry;
	
	public AmbientLight(Vector4f color) {
		this.color = color;
		geometry = Assets.newFullscreenQuad();
	}

	@Override
	public Vector4f getColor() {
		return color;
	}

	@Override
	public void uploadTo(Shader shader) {
		shader.uploadVector("lightColor", color);
		shader.uploadVector("lightPosition", position);
		shader.uploadFloat("lightRadius", 0);
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
