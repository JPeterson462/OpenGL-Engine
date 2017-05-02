package engine.rendering.passes;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import engine.Camera;
import engine.Engine;
import engine.Entity;
import engine.rendering.Shader;

/**
 * 
 * This class deals with rendering an animated entity. Nothing particularly new
 * here. The only exciting part is that the joint transforms get loaded up to
 * the shader in a uniform array.
 * 
 * @author Karl
 *
 */
public class AnimatedModelRenderer {

	private Shader shader;
	
	/**
	 * Initializes the shader program used for rendering animated models.
	 */
	public AnimatedModelRenderer(Shader shader) {
		this.shader = shader;
		shader.bind();
		shader.uploadInt("diffuseMap", 0);
	}

	/**
	 * Renders an animated entity. The main thing to note here is that all the
	 * joint transforms are loaded up to the shader to a uniform array. Also 5
	 * attributes of the VAO are enabled before rendering, to include joint
	 * indices and weights.
	 * 
	 * @param entity
	 *            - the animated entity to be rendered.
	 * @param camera
	 *            - the camera used to render the entity.
	 * @param lightDir
	 *            - the direction of the light in the scene.
	 */
	public void render(Entity entity, Camera camera, Vector3f lightDir, Engine engine, Vector4f plane) {
		engine.getRenderingBackend().setBlending(false);
		engine.getRenderingBackend().setDepth(true);
		prepare(camera, lightDir);
		shader.uploadMatrix("modelMatrix", entity.getModelMatrix());
		shader.uploadVector("plane", plane);
		entity.getAnimatedModel().getTexture().bind(0);
		entity.getAnimatedModel().getModel().bind();
		Matrix4f[] jointTransforms = entity.getAnimatedModel().getJointTransforms();
		for (int i = 0; i < jointTransforms.length; i++) {
			uploadMatrix(i, jointTransforms[i]);
		}
		entity.getAnimatedModel().getModel().renderGeometry();
		entity.getAnimatedModel().getModel().unbind();
		finish();
		engine.getRenderingBackend().setBlending(true);
		engine.getRenderingBackend().setAdditiveBlending(false);
	}
	
	private void uploadMatrix(int i, Matrix4f matrix) {
		shader.uploadMatrix("jointTransforms[" + i + "]", matrix);
	}

	/**
	 * Starts the shader program and loads up the projection view matrix, as
	 * well as the light direction. Enables and disables a few settings which
	 * should be pretty self-explanatory.
	 * 
	 * @param camera
	 *            - the camera being used.
	 * @param lightDir
	 *            - the direction of the light in the scene.
	 */
	private void prepare(Camera camera, Vector3f lightDir) {
		shader.bind();
		shader.uploadMatrix("projectionViewMatrix", camera.getProjectionViewMatrix());
		shader.uploadVector("lightDirection", lightDir);
	}

	/**
	 * Stops the shader program after rendering the entity.
	 */
	private void finish() {
		shader.unbind();
	}

}
