package engine.lights;

import org.joml.Matrix4f;
import org.joml.Vector4f;

import engine.rendering.Shader;

public interface Light {
	
	public Vector4f getColor();
	
	public void uploadTo(Shader shader);
	
	public void render();
	
	public Matrix4f getModelMatrix();

}
