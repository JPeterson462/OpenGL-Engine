package engine;

import org.joml.Vector3f;

public class FirstPersonCamera extends Camera {
	
	private float fov, aspectRatio, near, far;
	
	private static final Vector3f UP = new Vector3f(0, 1, 0);
	
	private Vector3f position, target;
	
	public FirstPersonCamera(float fov, float aspectRatio, float near, float far, Vector3f position, Vector3f target) {
		this.fov = fov;
		this.aspectRatio = aspectRatio;
		this.near = near;
		this.far = far;
		this.position = position;
		this.target = target;
		update();
	}
	
	public Vector3f getPosition() {
		return position;
	}
	
	public Vector3f getTarget() {
		return target;
	}
	
	public void setPosition(Vector3f position) {
		this.position = position;
		update();
	}
	
	public void setTarget(Vector3f target) {
		this.target = target;
		update();
	}

	@Override
	public void newProjectionMatrix() {
		getProjectionMatrix().identity().perspective((float) Math.toRadians(fov), aspectRatio, near, far);
	}

	@Override
	public void newViewMatrix() {
		getViewMatrix().identity().lookAt(position, target, UP);
	}
	
	@Override
	public Vector3f getCenter() {
		return position;
	}

}
