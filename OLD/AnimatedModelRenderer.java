package engine.rendering.passes;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import engine.Camera;
import engine.Engine;
import engine.animation.AnimatedModel;
import engine.rendering.Shader;

public class AnimatedModelRenderer {
	
	private Shader shader;
	
	public AnimatedModelRenderer(Shader shader) {
		this.shader = shader;
		shader.bind();
		shader.uploadInt("diffuseMap", 0);
	}
	
	public void render(AnimatedModel model, Camera camera, Engine engine, Vector3f lightDirection, float delta) {
		model.update(delta);
		shader.bind();
		camera.uploadTo(shader);
		shader.uploadVector("lightDirection", lightDirection);
		engine.getRenderingBackend().setDepth(true);
		model.getTexture().bind(0);
		model.getGeometry().bind();
		Matrix4f[] jointTransforms = model.getJointTransforms();
		for (int i = 0; i < jointTransforms.length; i++) {
			shader.uploadMatrix("boneMatrices[" + i + "]", jointTransforms[i]);
		}
		model.getGeometry().renderGeometry();
		model.getGeometry().unbind();
		shader.unbind();
	}

}
