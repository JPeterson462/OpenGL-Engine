package engine.lights;

import org.joml.Vector3f;

public interface PositionalLight extends Light {
	
	public Vector3f getPosition();
	
	public float getRange();

}
