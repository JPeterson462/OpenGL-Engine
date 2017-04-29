package engine.particles;

import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

public class Particle implements Comparable<Particle> {

	private Vector3f position, velocity;
	
	private float gravityEffect, lifeLength, rotation, scale, elapsedTime;
	
	public static float GRAVITY = -50;
	
	private Vector2i atlasSize;
	
	private Vector2f atlasOffset = new Vector2f(), nextAtlasOffset = new Vector2f();
	
	private float blendFactor, distanceSquared;
	
	private Vector3f distanceVector = new Vector3f();

	public Particle(Vector3f position, Vector3f velocity, float gravityEffect, float lifeLength, float rotation, float scale, int[] frames) {
		this.position = position;
		this.velocity = velocity;
		this.gravityEffect = gravityEffect;
		this.lifeLength = lifeLength;
		this.rotation = rotation;
		this.scale = scale;
		elapsedTime = 0;
		atlasSize = new Vector2i(frames[0], frames[1]);
		computeAtlasOffset();
	}
	
	private void computeAtlasOffset() {
		float lifeFactor = elapsedTime / lifeLength;
		int stageCount = atlasSize.x * atlasSize.y;
		float atlasProgression = lifeFactor * stageCount;
		int index1 = (int) Math.floor(atlasProgression);
		int index2 = index1 < stageCount - 1 ? index1 + 1 : index1;
		blendFactor = atlasProgression % 1;
		int column = index1 % atlasSize.y;
		int row = index1 / atlasSize.y;
		atlasOffset.x = (float) column / (float) atlasSize.y;
		atlasOffset.y = (float) row / (float) atlasSize.y;
		column = index2 % atlasSize.y;
		row = index2 / atlasSize.y;
		nextAtlasOffset.x = (float) column / (float) atlasSize.y;
		nextAtlasOffset.y = (float) row / (float) atlasSize.y;
	}
	
	public String toString() {
		return position + " " + velocity + " " + gravityEffect + " " + lifeLength + " " + rotation + " " + scale + " " + elapsedTime + " " + GRAVITY; 
	}

	public Vector3f getPosition() {
		return position;
	}

	public Vector3f getVelocity() {
		return velocity;
	}

	public float getGravityEffect() {
		return gravityEffect;
	}

	public float getLifeLength() {
		return lifeLength;
	}

	public float getRotation() {
		return rotation;
	}

	public float getScale() {
		return scale;
	}

	public float getElapsedTime() {
		return elapsedTime;
	}
	
	public Vector2i getAtlasSize() {
		return atlasSize;
	}
	
	public Vector2f getAtlasOffset() {
		return atlasOffset;
	}

	public Vector2f getNextAtlasOffset() {
		return nextAtlasOffset;
	}
	
	public float getBlendFactor() {
		return blendFactor;
	}
	
	public float getDistanceSquared() {
		return distanceSquared;
	}
	
	public boolean update(float delta, Vector3f cameraPosition) {
		velocity.y += GRAVITY * gravityEffect * delta;
		position.fma(delta, velocity);
		elapsedTime += delta;
		computeAtlasOffset();
		distanceVector.set(cameraPosition).sub(position);
		distanceSquared = distanceVector.lengthSquared();
		return elapsedTime < lifeLength;
	}

	@Override
	public int compareTo(Particle o) {
		return Float.compare(distanceSquared, o.getDistanceSquared());
	}
	
}
