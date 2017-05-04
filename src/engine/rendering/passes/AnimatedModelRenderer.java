package engine.rendering.passes;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import engine.Camera;
import engine.Engine;
import engine.Entity;
import engine.rendering.Geometry;
import engine.rendering.Material;
import engine.rendering.Shader;

public class AnimatedModelRenderer {

	private Shader regularShader, normalShader;
	
	private Geometry lastGeometry;
	
	private Shader shader;
	
	public AnimatedModelRenderer(Shader regularShader, Shader normalShader) {
		this.regularShader = regularShader;
		regularShader.bind();
		regularShader.uploadInt("diffuseTexture", 0);
		this.normalShader = normalShader;
		normalShader.bind();
		normalShader.uploadInt("diffuseTexture", 0);
		normalShader.uploadInt("normalTexture", 1);
	}
	
	public void bind(boolean normalMapped, Engine engine, Camera camera, Vector3f lightDir, Vector4f plane) {
		if (normalMapped) {
			bind(normalShader, engine, camera, lightDir, plane);
		} else {
			bind(regularShader, engine, camera, lightDir, plane);
		}
	}
	
	private void bind(Shader shader, Engine engine, Camera camera, Vector3f lightDir, Vector4f plane) {
		this.shader = shader;
		engine.getRenderingBackend().setBlending(false);
		engine.getRenderingBackend().setDepth(true);
		prepare(camera, lightDir);
		shader.uploadVector("plane", plane);
		lastMaterial = null;
		lastGeometry = null;
	}
	
	public void useGeometry(Geometry geometry) {
		if (lastGeometry != null) {
			lastGeometry.unbind();
		}
		if (geometry != null) {
			geometry.bind();
		}
		lastGeometry = geometry;
	}
	
	private Material lastMaterial;

	public void render(Entity entity, boolean normalMapped) {
		shader.uploadMatrix("modelMatrix", entity.getModelMatrix());
		Material material = entity.getAnimatedModel().getMaterial();
		if (lastMaterial == null || !material.equals(lastMaterial)) {
			material.getDiffuseTexture().bind(0);
			if (normalMapped) {
				material.getNormalTexture().bind(1);
			}
			shader.uploadFloat("materialShineDamper", material.getShineDamper());
			shader.uploadFloat("materialReflectivity", material.getReflectivity());
		}
		lastMaterial = material;		
		Matrix4f[] jointTransforms = entity.getAnimatedModel().getJointTransforms();
		for (int i = 0; i < jointTransforms.length; i++) {
			uploadMatrix(i, jointTransforms[i]);
		}
		entity.getAnimatedModel().getModel().renderGeometry();
	}
	
	public void unbind(Engine engine) {
		finish();
		engine.getRenderingBackend().setBlending(true);
		engine.getRenderingBackend().setAdditiveBlending(false);
	}
	
	private void uploadMatrix(int i, Matrix4f matrix) {
		shader.uploadMatrix("jointTransforms[" + i + "]", matrix);
	}

	private void prepare(Camera camera, Vector3f lightDir) {
		shader.bind();
		camera.uploadTo(shader);
	}

	private void finish() {
		shader.unbind();
	}

}
