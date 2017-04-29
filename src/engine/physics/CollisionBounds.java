package engine.physics;

import org.joml.Vector3f;

import engine.terrain.Terrain;

public interface CollisionBounds {

	public Vector3f overlaps(Body other, Body current);
	
	public float underneath(Body current, Terrain terrain);
	
}
