package engine.particles;

import java.util.ArrayList;
import java.util.Random;

import org.joml.Vector3f;

import engine.rendering.Texture;
import utils.InsertionSort;

public class BasicParticleEmitter implements ParticleEmitter {
	
	private ArrayList<Particle> particles = new ArrayList<>();
	
	private Texture texture;
	
	private Vector3f position;
	
	private float emissionRate;
	
	private float time = 0, speed, gravityEffect, size, lifeLength;
	
	private float lifeError, speedError, scaleError, maxAngle;
	
	private Random random;
	
	private int[] frames;
	
	public BasicParticleEmitter(Texture texture, Vector3f position, float emissionRate, float speed, float gravityEffect, float size, float lifeLength, 
			float lifeError, float speedError, float scaleError, float maxAngle, int[] frames) {
		this.texture = texture;
		this.position = position;
		this.emissionRate = emissionRate;
		this.speed = speed;
		this.gravityEffect = gravityEffect;
		this.size = size;
		this.lifeLength = lifeLength;
		this.lifeError = lifeError;
		this.speedError = speedError;
		this.scaleError = scaleError;
		this.maxAngle = maxAngle;
		random = new Random();
		this.frames = frames;
	}

	@Override
	public Texture getTexture() {
		return texture;
	}

	@Override
	public void update(float delta, Vector3f cameraPosition) {
		for (int i = particles.size() - 1; i >= 0; i--) {
			if (!particles.get(i).update(delta, cameraPosition)) {
				particles.remove(i);
			}
		}
		time += delta;
		while (time > emissionRate) {
			Vector3f velocity = new Vector3f((float) Math.random() * 2f - 1f, 1, (float) Math.random() * 2f - 1f);
			velocity.normalize();
			velocity.mul(randomize(speed, speedError));
			particles.add(new Particle(new Vector3f(position), velocity, gravityEffect, randomize(lifeLength, lifeError), random.nextFloat() * maxAngle, randomize(size, scaleError), frames));
			time -= emissionRate;
		}
		InsertionSort.sort(particles);
	}
	
	private float randomize(float value, float error) {
		return value * (1f + error * (random.nextFloat() * 2f - 1f));
	}

	@Override
	public ArrayList<Particle> getParticles() {
		return particles;
	}

	@Override
	public boolean useAdditiveBlending() {
		return true;
	}

}
