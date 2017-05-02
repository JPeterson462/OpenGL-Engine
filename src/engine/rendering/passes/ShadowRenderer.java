package engine.rendering.passes;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import engine.Camera;
import engine.Engine;
import engine.rendering.EntityRender;
import engine.rendering.Framebuffer;
import engine.rendering.Shader;
import engine.rendering.Texture;
import engine.shadows.ShadowBox;

public class ShadowRenderer {
	
	private static final int SHADOW_MAP_SIZE = 2048;
	
	private Framebuffer shadowBuffer;
	
	private Shader shader;
	
	private ShadowBox shadowBox;
	
	private Matrix4f projectionMatrix = new Matrix4f();
	
	private Matrix4f lightViewMatrix = new Matrix4f();
	
	private Matrix4f projectionViewMatrix = new Matrix4f();
	
	private Matrix4f offset = createOffset();
	
	private float nearPlane;
	
	public ShadowRenderer(Shader shader, Engine engine, Camera camera) {
		nearPlane = engine.getSettings().nearPlane;
		this.shader = shader;
		shadowBox = new ShadowBox(lightViewMatrix, camera, engine.getSettings().nearPlane, engine.getSettings().fov, engine.getSettings().aspectRatio);
		shadowBuffer = engine.getRenderingBackend().createFramebuffer(SHADOW_MAP_SIZE, SHADOW_MAP_SIZE, 1, false);
		shader.bind();
		shader.uploadInt("modelTexture", 0);
	}
	
	public Matrix4f getToShadowSpaceMatrix() {
		return new Matrix4f(offset).mul(projectionViewMatrix);
	}
	
	public void render(Vector3f sunPosition, EntityRender entityRenderCall) {
		shadowBox.update(nearPlane);
		Vector3f lightDirection = new Vector3f(-sunPosition.x, -sunPosition.y, -sunPosition.z);
		updateOrthoProjectionMatrix(shadowBox.getWidth(), shadowBox.getHeight(), shadowBox.getLength());
		updateLightViewMatrix(lightDirection, shadowBox.getCenter());
		projectionMatrix.mul(lightViewMatrix, projectionViewMatrix);
		shadowBuffer.bind();
		shader.bind();
		shader.uploadMatrix("projectionViewMatrix", projectionViewMatrix);
		entityRenderCall.renderScene(shader);
		shader.unbind();
		shadowBuffer.unbind();
	}
	private void updateLightViewMatrix(Vector3f direction, Vector3f center) {
		direction.normalize();
		center.negate();
		lightViewMatrix.identity();
		float pitch = (float) Math.acos(new Vector2f(direction.x, direction.z).length());
		lightViewMatrix.rotateX(pitch);
		float yaw = (float) Math.toDegrees(((float) Math.atan(direction.x / direction.z)));
		yaw = direction.z > 0 ? yaw - 180 : yaw;
		lightViewMatrix.rotateY(-(float) Math.toRadians(yaw));
		lightViewMatrix.translate(center);
	}

	private void updateOrthoProjectionMatrix(float width, float height, float length) {
		projectionMatrix.identity();
		projectionMatrix.m00(2f / width);
		projectionMatrix.m11(2f / height);
		projectionMatrix.m22(-2f / length);
		projectionMatrix.m33(1);
	}
	
	public Texture getShadowMap() {
		return shadowBuffer.getDepthTexture();
	}
	
	public Matrix4f getLightSpaceTransform() {
		return lightViewMatrix;
	}
	
	private Matrix4f createOffset() {
		Matrix4f offset = new Matrix4f();
		offset.translate(new Vector3f(0.5f, 0.5f, 0.5f));
		offset.scale(new Vector3f(0.5f, 0.5f, 0.5f));
		return offset;
	}

}
