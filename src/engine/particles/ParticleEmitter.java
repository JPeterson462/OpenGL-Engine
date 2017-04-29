package engine.particles;

import java.util.ArrayList;

import org.joml.Vector3f;

import engine.rendering.Texture;

public interface ParticleEmitter {
	
	public Texture getTexture();
	
	public void update(float delta, Vector3f cameraPosition);
	
	public ArrayList<Particle> getParticles();
	
	public boolean useAdditiveBlending();
	
}
