package engine.input;

import org.joml.Vector3f;

import engine.FirstPersonCamera;
import engine.physics.Body;
import engine.terrain.Terrain;

public class FirstPersonCameraController {
	
	private FirstPersonCamera camera;
	
	private Keyboard keyboard;
	
	private Mouse mouse;
	
	private Body body;
	
	private float speed = 50;
	
	private Vector3f target = new Vector3f();
	
	private float mouseSensitivity;
	
	private int mouseX, mouseY;
	
	private float height;
	
	public FirstPersonCameraController(FirstPersonCamera camera, Keyboard keyboard, Mouse mouse, Body body, float mouseSensitivity, float height) {
		this.camera = camera;
		this.keyboard = keyboard;
		this.mouse = mouse;
		this.body = body;
		this.mouseSensitivity = mouseSensitivity;
		mouseX = mouse.getMouseX();
		mouseY = mouse.getMouseY();
		this.height = height;
	}
	
	public float getSpeed() {
		return speed;
	}
	
	public void setSpeed(float speed) {
		this.speed = speed;
	}
	
	public Body getBody() {
		return body;
	}
	
	public void update(float delta, Terrain terrain) {
		Vector3f movementForward = new Vector3f(0, 0, speed);
		movementForward.rotate(body.getOrientation());
		Vector3f movementSideways = new Vector3f(movementForward).cross(new Vector3f(0, 1, 0));
		Vector3f totalMovement = new Vector3f();
		if (keyboard.isKeyDown(Key.W)) {
			totalMovement.add(movementForward.x, 0, movementForward.z);
		}
		if (keyboard.isKeyDown(Key.A)) {
			totalMovement.sub(movementSideways.x, 0, movementSideways.z);
		}
		if (keyboard.isKeyDown(Key.S)) {
			totalMovement.sub(movementForward.x, 0, movementForward.z);
		}
		if (keyboard.isKeyDown(Key.D)) {
			totalMovement.add(movementSideways.x, 0, movementSideways.z);
		}
		body.getLinearVelocity().set(totalMovement);
		if (mouseX >= 0 && mouseY >= 0) {
			float maxRotation = (float) (3 * Math.PI) * mouseSensitivity * delta;
			float maxDx = 1.5f, maxDy = 1.5f;
			float dx = mouse.getMouseX() - mouseX, dy = mouse.getMouseY() - mouseY;
			body.getOrientation().rotateXYZ(maxRotation * dy / maxDy, -maxRotation * dx / maxDx, 0);
		}
		
		// START TEST PHYSICS
			body.getPosition().fma(delta, totalMovement);
			body.getPosition().y = terrain.getHeightAt(body.getPosition().x, body.getPosition().z) + height;
		// STOP TEST PHYSICS
			
		mouseX = mouse.getMouseX();
		mouseY = mouse.getMouseY();
		Vector3f direction = new Vector3f(0, 0, 100);
		target.set(direction).rotate(body.getOrientation()).add(body.getPosition());
		camera.getPosition().set(body.getPosition()).add(0, height, 0);
		camera.getTarget().set(target).add(0, height, 0);
	}

}
