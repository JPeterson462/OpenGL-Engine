package engine;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class FirstPersonCamera extends Camera {
	
	private float fov, aspectRatio, near, far;
	
	private Vector3f position;
	
	private Quaternionf orientation = new Quaternionf();
	
	private float pitch = 0, yaw = 0;
	
	public FirstPersonCamera(float fov, float aspectRatio, float near, float far, Vector3f position) {
		this.fov = fov;
		this.aspectRatio = aspectRatio;
		this.near = near;
		this.far = far;
		this.position = position;
		update();
	}
	
	public float getPitch() {
		return pitch;
	}
	
	public float getYaw() {
		return yaw;
	}
	
	public void setPitch(float pitch) {
		this.pitch = pitch;
	}
	
	public void setYaw(float yaw) {
		this.yaw = yaw;
	}
	
	public Vector3f getPosition() {
		return position;
	}
	
	public void setPosition(Vector3f position) {
		this.position = position;
		update();
	}
	
	@Override
	public void newProjectionMatrix() {
		getProjectionMatrix().identity().perspective((float) Math.toRadians(fov), aspectRatio, near, far);
	}

	@Override
	public void newViewMatrix() {
		Matrix4f viewMatrix = getViewMatrix();
		viewMatrix.identity();
		viewMatrix.rotateX((float) Math.toRadians(pitch));
		viewMatrix.rotateY((float) Math.toRadians(yaw));
		viewMatrix.translate(-position.x, -position.y, -position.z);
		orientation.rotationXYZ((float) Math.toRadians(pitch), (float) Math.toRadians(yaw), 0);		
	}
	
	@Override
	public Vector3f getCenter() {
		return position;
	}
	
	@Override
	public Quaternionf getOrientation() {
		return orientation;
	}

}
