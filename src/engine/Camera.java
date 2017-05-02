package engine;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import engine.rendering.Shader;

public abstract class Camera {
	
	private Matrix4f projectionMatrix, viewMatrix;
	
	private Vector3f emptyVector = new Vector3f();
	
	private Quaternionf emptyQuaternion = new Quaternionf();
	
	private Matrix4f projectionViewMatrix = new Matrix4f();
	
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
		return emptyVector;
	}
	
	public Quaternionf getOrientation() {
		return emptyQuaternion;
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
		projectionMatrix.mul(viewMatrix, projectionViewMatrix);
	}

	public float getPitch() {
		throw new IllegalStateException();
	}
	
	public float getYaw() {
		throw new IllegalStateException();
	}

	public void setPitch(float pitch) {
		throw new IllegalStateException();
	}
	
	public void setYaw(float yaw) {
		throw new IllegalStateException();
	}
	
	public Matrix4f getProjectionViewMatrix() {
		return projectionViewMatrix;
	}

}
