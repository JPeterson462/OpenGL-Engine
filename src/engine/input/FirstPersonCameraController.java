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
		float distance = speed;
		Vector3f totalMovement = new Vector3f();
		if (keyboard.isKeyDown(Key.W)) {
			totalMovement.add(distance * (float) Math.sin(Math.toRadians(camera.getYaw())), 0, -distance * (float) Math.cos(Math.toRadians(camera.getYaw())));
		}
		if (keyboard.isKeyDown(Key.A)) {
			totalMovement.add(distance * (float) Math.sin(Math.toRadians(camera.getYaw()) - (float) Math.PI * 0.5f), 0, -distance * (float) Math.cos(Math.toRadians(camera.getYaw()) - (float) Math.PI * 0.5f));
		}
		if (keyboard.isKeyDown(Key.S)) {
			totalMovement.add(-distance * (float) Math.sin(Math.toRadians(camera.getYaw())), 0, distance * (float) Math.cos(Math.toRadians(camera.getYaw())));
		}
		if (keyboard.isKeyDown(Key.D)) {
			totalMovement.add(-distance * (float) Math.sin(Math.toRadians(camera.getYaw()) - (float) Math.PI * 0.5f), 0, distance * (float) Math.cos(Math.toRadians(camera.getYaw()) - (float) Math.PI * 0.5f));
		}
		body.getLinearVelocity().set(totalMovement);
		if (mouseX >= 0 && mouseY >= 0) {
			float maxRotation = (float) 90 * mouseSensitivity * delta;
			float dx = mouse.getMouseX() - mouseX, dy = mouse.getMouseY() - mouseY;
			camera.setPitch(camera.getPitch() + maxRotation * dy);
			camera.setYaw(camera.getYaw() + maxRotation * dx);
		}
		
		// START TEST PHYSICS
			body.getPosition().fma(delta, totalMovement);
			body.getPosition().y = terrain.getHeightAt(body.getPosition().x, body.getPosition().z) + height;
		// STOP TEST PHYSICS
			
		mouseX = mouse.getMouseX();
		mouseY = mouse.getMouseY();
		
		camera.getPosition().set(body.getPosition()).add(0, height, 0);
	}

}
