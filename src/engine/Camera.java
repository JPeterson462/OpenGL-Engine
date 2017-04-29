package engine;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import engine.rendering.Shader;

public abstract class Camera {
	
	private Matrix4f projectionMatrix, viewMatrix;
	
	public abstract void newProjectionMatrix();
	
	public abstract void newViewMatrix();
	
	public Camera() {
		projectionMatrix = new Matrix4f();
		viewMatrix = new Matrix4f();
	}
	
	public void uploadTo(Shader shader) {
		shader.uploadMatrix("projectionMatrix", projectionMatrix);
		shader.uploadMatrix("viewMatrix", viewMatrix);
	}
	
	public Vector3f getCenter() {
		return new Vector3f();
	}
	
	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}
	
	public Matrix4f getViewMatrix() {
		return viewMatrix;
	}
	
	public void update() {
		newProjectionMatrix();
		newViewMatrix();
	}
	
	//getOrientation()

}
