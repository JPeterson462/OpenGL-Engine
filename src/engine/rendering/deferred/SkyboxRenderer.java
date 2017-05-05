package engine.rendering.deferred;

import engine.Asset;
import engine.AssetNamespace;
import engine.Assets;
import engine.Camera;
import engine.ClasspathAsset;
import engine.Engine;
import engine.rendering.Geometry;
import engine.rendering.Shader;
import engine.rendering.Texture;
import engine.rendering.Vertex;
import utils.StringUtils;

import java.util.ArrayList;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class SkyboxRenderer {

	private Shader shader;

	private float rotation = 0;
	
	private float blendFactor = 0;
	
	private float time;
	
	private Texture daySkybox, nightSkybox;
	
	private Geometry skybox;
	
	private Vector3f nightFogColor = new Vector3f(0, 0, 0);
	
	private Vector3f smoothFogColor = new Vector3f();
	
	private Matrix4f modelMatrix = new Matrix4f();
	
	public SkyboxRenderer(Shader shader, Engine engine, Camera camera) {
		this.shader = shader;
		skybox = createGeometry(engine);
		daySkybox = Assets.newCubemap(getSkyboxPaths(""));
		nightSkybox = Assets.newCubemap(getSkyboxPaths("night"));
		shader.bind();
		shader.uploadInt("cubeMap1", 0);
		shader.uploadInt("cubeMap2", 1);
		shader.uploadMatrix("projectionMatrix", camera.getProjectionMatrix());
	}

	private Asset[] getSkyboxPaths(String prefix) {
		Asset[] paths = new Asset[6];
		paths[0] = getSkyboxPath(prefix, "right");
		paths[1] = getSkyboxPath(prefix, "left");
		paths[2] = getSkyboxPath(prefix, /*"top"*/ "bottom");//TODO fix
		paths[3] = getSkyboxPath(prefix, "bottom");
		paths[4] = getSkyboxPath(prefix, "back");
		paths[5] = getSkyboxPath(prefix, "front");
		return paths;
	}
	
	private Asset getSkyboxPath(String prefix, String path) {
		if (prefix.length() > 0) {
			return new ClasspathAsset(AssetNamespace.TEXTURES, prefix + StringUtils.capitalize(path) + ".png");
		}
		return new ClasspathAsset(AssetNamespace.TEXTURES, path + ".png");
	}
	
	public void update(Camera camera, float delta) {
		rotation += Math.toRadians(1) * delta;
		time += delta * 1000;
		time %= 24000;
		if (time >= 0 && time < 5000) {
			blendFactor = (time - 0) / (5000 - 0) * 0.25f;
		} else if (time >= 5000 && time < 8000) {
			blendFactor = (time - 5000) / (8000 - 5000) * 0.25f + 0.25f;
		} else if (time >= 8000 && time < 21000) {
			blendFactor = (time - 8000) / (21000 - 8000) * 0.25f + 0.5f;
		} else {
			blendFactor = (time - 21000) / (24000 - 21000) * 0.25f + 0.75f;
		}
		modelMatrix.identity();
		modelMatrix.translate(camera.getCenter());
		modelMatrix.rotateY(rotation);
	}
	
	public Vector3f getSmoothFogColor() {
		return smoothFogColor;
	}
	
	public float getBlendFactor() {
		return blendFactor;
	}
	
	public void render(Camera camera, Vector3f fogColor) {
		fogColor.lerp(nightFogColor, blendFactor, smoothFogColor);
		shader.bind();
		shader.uploadMatrix("viewMatrix", camera.getViewMatrix());
		shader.uploadMatrix("modelMatrix", modelMatrix);
		shader.uploadVector("fogColor", smoothFogColor);
		shader.uploadFloat("blendFactor", blendFactor);
		skybox.bind();
		daySkybox.bind(0);
		nightSkybox.bind(1);
		skybox.renderGeometry();
		nightSkybox.unbind();
		daySkybox.unbind();
		skybox.unbind();
	}
	
	// Skybox Geometry
	
	private Geometry createGeometry(Engine engine) {
		ArrayList<Vertex> vertices = new ArrayList<>();
		ArrayList<Integer> indices = new ArrayList<>();
		for (int i = 0; i < VERTICES.length; i += 3) {
			vertices.add(new Vertex(new Vector3f(VERTICES[i], VERTICES[i + 1], VERTICES[i + 2])));
		}
		for (int i = 0; i < vertices.size(); i++) {
			indices.add(i);
		}
		return engine.getRenderingBackend().createGeometry(vertices, indices, true);
	}
	
	private static final float SIZE = 500f;

	private static final float[] VERTICES = { //
			-SIZE, SIZE, -SIZE, //
			-SIZE, -SIZE, -SIZE, //
			SIZE, -SIZE, -SIZE, //
			SIZE, -SIZE, -SIZE, //
			SIZE, SIZE, -SIZE, //
			-SIZE, SIZE, -SIZE, //

			-SIZE, -SIZE, SIZE, //
			-SIZE, -SIZE, -SIZE, //
			-SIZE, SIZE, -SIZE, //
			-SIZE, SIZE, -SIZE, //
			-SIZE, SIZE, SIZE, //
			-SIZE, -SIZE, SIZE, //

			SIZE, -SIZE, -SIZE, //
			SIZE, -SIZE, SIZE, //
			SIZE, SIZE, SIZE, //
			SIZE, SIZE, SIZE, //
			SIZE, SIZE, -SIZE, //
			SIZE, -SIZE, -SIZE, //

			-SIZE, -SIZE, SIZE, //
			-SIZE, SIZE, SIZE, //
			SIZE, SIZE, SIZE, //
			SIZE, SIZE, SIZE, //
			SIZE, -SIZE, SIZE, //
			-SIZE, -SIZE, SIZE, //

			-SIZE, SIZE, -SIZE, //
			SIZE, SIZE, -SIZE, //
			SIZE, SIZE, SIZE, //
			SIZE, SIZE, SIZE, //
			-SIZE, SIZE, SIZE, //
			-SIZE, SIZE, -SIZE, //

			-SIZE, -SIZE, -SIZE, //
			-SIZE, -SIZE, SIZE, //
			SIZE, -SIZE, -SIZE, //
			SIZE, -SIZE, -SIZE, //
			-SIZE, -SIZE, SIZE, //
			SIZE, -SIZE, SIZE //
	};
	
}
