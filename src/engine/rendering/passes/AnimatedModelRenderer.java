package engine.rendering.passes;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import engine.Camera;
import engine.Engine;
import engine.Entity;
import engine.rendering.Geometry;
import engine.rendering.Light;
import engine.rendering.Material;
import engine.rendering.Shader;

public class AnimatedModelRenderer {

	private Shader regularShader, normalShader;
	
	private Geometry lastGeometry;
	
	private Shader shader;
	
	public AnimatedModelRenderer(Shader regularShader, Shader normalShader) {
		this.regularShader = regularShader;
		regularShader.bind();
		regularShader.uploadInt("diffuseMap", 0);
		this.normalShader = normalShader;
		normalShader.bind();
		normalShader.uploadInt("diffuseMap", 0);
		normalShader.uploadInt("normalMap", 1);
	}
	
	public void bind(boolean normalMapped, Engine engine, Camera camera, Vector3f lightDir, Vector3f skyColor, Vector4f plane, float ambientLightFactor) {
		if (normalMapped) {
			bind(normalShader, engine, camera, lightDir, skyColor, plane, ambientLightFactor);
		} else {
			bind(regularShader, engine, camera, lightDir, skyColor, plane, ambientLightFactor);
		}
	}
	
	private void bind(Shader shader, Engine engine, Camera camera, Vector3f lightDir, Vector3f skyColor, Vector4f plane, float ambientLightFactor) {
		this.shader = shader;
		engine.getRenderingBackend().setBlending(false);
		engine.getRenderingBackend().setDepth(true);
		prepare(camera, lightDir);
		shader.uploadVector("plane", plane);
		shader.uploadVector("skyColor", skyColor);
		shader.uploadFloat("ambientLightFactor", ambientLightFactor);
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

	public void render(Entity entity, Light[] lights, int lightCount, boolean normalMapped) {
		for (int i = 0; i < SceneRenderer.MAX_LIGHTS; i++) {
			if (i < lightCount) {
				shader.uploadVector("lightPosition[" + i + "]", lights[i].getPosition());
				shader.uploadVector("lightColor[" + i + "]", lights[i].getColor());
				shader.uploadVector("attenuation[" + i + "]", lights[i].getAttenuation());
			} else {
				shader.uploadVector("lightPosition[" + i + "]", new Vector3f());
				shader.uploadVector("lightColor[" + i + "]", new Vector3f());
				shader.uploadVector("attenuation[" + i + "]", new Vector3f(1, 0, 0));
			}
		}
		
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
		shader.uploadVector("lightDirection", lightDir);
	}

	private void finish() {
		shader.unbind();
	}

}
