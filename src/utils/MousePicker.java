package utils;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import engine.Camera;
import engine.input.Mouse;

public class MousePicker {
	
	private Vector3f currentRay;
	
	private Camera camera;
	
	public MousePicker(Camera  camera) {
		this.camera = camera;
	}
	
	public Vector3f getCurrentRay() {
		return currentRay;
	}
	
	public void update(Mouse mouse, int width, int height) {
		currentRay = calculateMouseRay(mouse, width, height);
	}
	
	private Vector3f calculateMouseRay(Mouse mouse, int width, int height) {
		Matrix4f projectionMatrix = camera.getProjectionMatrix();
		Matrix4f viewMatrix = camera.getViewMatrix();
		int mouseX = mouse.getMouseX();
		int mouseY = mouse.getMouseY();
		Vector2f normalizedDeviceCoords = getNormalizedDeviceCoords(mouseX, mouseY, width, height);
		Vector4f clipCoords = new Vector4f(normalizedDeviceCoords, -1f, 1f);
		Vector4f eyeCoords = getEyeSpaceCoords(clipCoords, projectionMatrix);
		Vector3f worldCoords = getWorldSpaceCoords(eyeCoords, viewMatrix);
		return worldCoords;
	}
	
	private Vector2f getNormalizedDeviceCoords(float mouseX, float mouseY, float width, float height) {
		float x = (2f * mouseX) / width - 1;
		float y = (2f * mouseY) / height - 1;
		return new Vector2f(x, y);
	}
	
	private Vector4f getEyeSpaceCoords(Vector4f clipCoords, Matrix4f projectionMatrix) {
		Matrix4f inverseProjectionMatrix = new Matrix4f(projectionMatrix).invert();
		Vector4f eyeCoords = new Vector4f();
		inverseProjectionMatrix.transform(clipCoords, eyeCoords);
		return new Vector4f(eyeCoords.x, eyeCoords.y, -1f, 0f);
	}
	
	private Vector3f getWorldSpaceCoords(Vector4f eyeCoords, Matrix4f viewMatrix) {
		Matrix4f inverseViewMatrix = new Matrix4f(viewMatrix).invert();
		Vector4f worldCoords = new Vector4f();
		inverseViewMatrix.transform(eyeCoords, worldCoords);
		return new Vector3f(worldCoords.x, worldCoords.y, worldCoords.z).normalize();
	}

}
