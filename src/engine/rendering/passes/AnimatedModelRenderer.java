package engine.rendering.passes;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector3f;

import engine.Camera;
import engine.Engine;
import engine.animation.AnimatedModel;
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
	
	private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
	
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
	public void render(AnimatedModel entity, Camera camera, Vector3f lightDir, Engine engine) {
		engine.getRenderingBackend().setBlending(false);
		engine.getRenderingBackend().setDepth(true);
		prepare(camera, lightDir);
		entity.getTexture().bind(0);
		entity.getModel().bind();
		org.lwjgl.util.vector.Matrix4f[] jointTransforms = entity.getJointTransforms();
		for (int i = 0; i < jointTransforms.length; i++) {
			uploadMatrix(i, jointTransforms[i]);
		}
		entity.getModel().renderGeometry();
		entity.getModel().unbind();
		finish();
		engine.getRenderingBackend().setBlending(true);
		engine.getRenderingBackend().setAdditiveBlending(false);
	}
	
	private void uploadMatrix(int i, org.lwjgl.util.vector.Matrix4f matrix) {
		matrixBuffer.rewind();
		matrix.store(matrixBuffer);
		matrixBuffer.flip();
		shader.uploadMatrix("jointTransforms[" + i + "]", matrixBuffer);
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
		shader.uploadVector("lightDirection", new org.joml.Vector3f(lightDir.x, lightDir.y, lightDir.z));
	}

	/**
	 * Stops the shader program after rendering the entity.
	 */
	private void finish() {
		shader.unbind();
	}

}
