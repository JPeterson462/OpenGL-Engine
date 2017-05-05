package engine.rendering;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import engine.Camera;
import engine.Engine;
import engine.particles.Particle;
import engine.particles.ParticleEmitter;

public class ParticleRenderer {
	
	private ArrayList<ParticleEmitter> particleEmitters = new ArrayList<>();
	
	public static final int MAX_INSTANCES = 1_000, DATA_PER_INSTANCE = 21;
	
	private Shader shader;
	
	private Camera camera;
	
	private InstancedGeometry geometry;
	
	private Matrix4f modelMatrix = new Matrix4f();
	
	private Matrix4f modelViewMatrix = new Matrix4f();
	
	private FloatBuffer buffer = BufferUtils.createFloatBuffer(MAX_INSTANCES * DATA_PER_INSTANCE);
	
	public ParticleRenderer(Shader shader, Camera camera, Engine engine) {
		this.shader = shader;
		this.camera = camera;
		buffer.limit(buffer.capacity());
		ArrayList<Vertex> vertices = new ArrayList<>();
		vertices.add(new Vertex(new Vector3f(-0.5f, -0.5f, 0)));
		vertices.add(new Vertex(new Vector3f(0.5f, -0.5f, 0)));
		vertices.add(new Vertex(new Vector3f(0.5f, 0.5f, 0)));
		vertices.add(new Vertex(new Vector3f(-0.5f, 0.5f, 0)));
		ArrayList<Integer> indices = new ArrayList<>();
		indices.add(0);
		indices.add(1);
		indices.add(2);
		indices.add(2);
		indices.add(3);
		indices.add(0);
		geometry = engine.getRenderingBackend().createInstancedGeometry(vertices, indices, DATA_PER_INSTANCE, 2);
	}
	
	public void addEmitter(ParticleEmitter emitter) {
		particleEmitters.add(emitter);
	}
	
	public void update(float delta) {
		for (int i = 0; i < particleEmitters.size(); i++) {
			particleEmitters.get(i).update(delta, camera.getCenter());
		}
	}
	
	public void render(Engine engine) {
		engine.getRenderingBackend().setDepthBuffer(false);
		shader.bind();
		shader.uploadMatrix("projectionMatrix", camera.getProjectionMatrix());
		Texture texture = null;
		geometry.bind();
		for (int i = 0 ; i < particleEmitters.size(); i++) {
			ParticleEmitter emitter = particleEmitters.get(i);
			engine.getRenderingBackend().setAdditiveBlending(emitter.useAdditiveBlending());
			emitter.getTexture().bind(0);
			if (emitter.getParticles().size() == 0) {
				continue;
			}
			shader.uploadVector("textureAtlasSize", new Vector2f(emitter.getParticles().get(0).getAtlasSize().x, emitter.getParticles().get(0).getAtlasSize().y));
			ArrayList<Particle> particles = emitter.getParticles();
			int position = 0;
			buffer.limit(buffer.capacity());
			for (int j = 0; j < particles.size() && j < MAX_INSTANCES; j++) {
				Particle particle = particles.get(j);
				computeModelViewMatrix(particle);
				modelViewMatrix.get(position, buffer);
				buffer.put(position + 16, particle.getAtlasOffset().x);
				buffer.put(position + 17, particle.getAtlasOffset().y);
				buffer.put(position + 18, particle.getNextAtlasOffset().x);
				buffer.put(position + 19, particle.getNextAtlasOffset().y);
				buffer.put(position + 20, particle.getBlendFactor());
				position += DATA_PER_INSTANCE;
			}
			buffer.limit(position);
			geometry.updateInstances(buffer);
			geometry.renderGeometry(particles.size());			
			texture = emitter.getTexture();		
		}
		geometry.unbind();
		if (texture != null) {
			texture.unbind();
		}
		shader.unbind();
		engine.getRenderingBackend().setAdditiveBlending(false);
		engine.getRenderingBackend().setDepthBuffer(true);
	}
	
	private void computeModelViewMatrix(Particle particle) {
		modelViewMatrix.identity();
		Matrix4f viewMatrix = camera.getViewMatrix();
		modelMatrix.identity();
		modelMatrix.m00(viewMatrix.m00());
		modelMatrix.m01(viewMatrix.m10());
		modelMatrix.m02(viewMatrix.m20());
		modelMatrix.m10(viewMatrix.m01());
		modelMatrix.m11(viewMatrix.m11());
		modelMatrix.m12(viewMatrix.m21());
		modelMatrix.m20(viewMatrix.m02());
		modelMatrix.m21(viewMatrix.m12());
		modelMatrix.m22(viewMatrix.m22());
		modelMatrix.translate(particle.getPosition());
		modelMatrix.rotate((float) particle.getRotation(), new Vector3f(0, 0, 1));
		modelMatrix.scale(particle.getScale(), particle.getScale(), particle.getScale());
		viewMatrix.mul(modelMatrix, modelViewMatrix);
	}

}
