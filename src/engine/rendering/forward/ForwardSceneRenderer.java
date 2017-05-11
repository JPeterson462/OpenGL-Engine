package engine.rendering.forward;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import engine.AssetNamespace;
import engine.Assets;
import engine.Camera;
import engine.ClasspathAsset;
import engine.Engine;
import engine.Entity;
import engine.input.Mouse;
import engine.rendering.Geometry;
import engine.rendering.Light;
import engine.rendering.Material;
import engine.rendering.Scene;
import engine.rendering.Shader;
import engine.rendering.VertexTemplate;
import engine.shadows.ShadowBox;
import utils.MousePicker;

public class ForwardSceneRenderer implements Scene.Rendering {

	private Shader defaultShader, normalMappedShader;

	private Camera camera;

	private TerrainRenderer terrainRenderer;
	
	private SkyboxRenderer skyboxRenderer;
	
	private WaterRenderer waterRenderer;
	
	private ShadowRenderer shadowRenderer;

//	private PostProcessingRenderer postProcessingRenderer;
	
	private AnimatedModelRenderer animatedModelRenderer;

	public static final float AMBIENT_LIGHT = 0.4f;
	
	public static final int MAX_LIGHTS = 4;
	
	private Light[] lights = new Light[MAX_LIGHTS];
	
	private int lightCount;
	
	private Engine engine;
	
	private Scene scene;
	
	private Matrix4f emptyMatrix = new Matrix4f();
	
	public ForwardSceneRenderer(Camera camera, Scene scene, Engine engine, Light[] lights) {
		this.engine = engine;
		this.camera = camera;
		this.scene = scene;
		scene.setMousePicker(new MousePicker(camera));
		System.arraycopy(lights, 0, this.lights, 0, Math.min(MAX_LIGHTS, lights.length));
		lightCount = Math.min(MAX_LIGHTS, lights.length);
		defaultShader = Assets.newShader(new ClasspathAsset(AssetNamespace.SHADERS, "forward/fragmentDefault.glsl"), 
				new ClasspathAsset(AssetNamespace.SHADERS, "forward/vertexDefault.glsl"), VertexTemplate.POSITION_TEXCOORD_NORMAL);
		normalMappedShader = Assets.newShader(new ClasspathAsset(AssetNamespace.SHADERS, "forward/fragmentNormals.glsl"), 
				new ClasspathAsset(AssetNamespace.SHADERS, "forward/vertexNormals.glsl"), VertexTemplate.POSITION_TEXCOORD_NORMAL_TANGENT);
		this.camera = camera;
		terrainRenderer = new TerrainRenderer(Assets.newShader(new ClasspathAsset(AssetNamespace.SHADERS, "forward/fragmentTerrain.glsl"), 
				new ClasspathAsset(AssetNamespace.SHADERS, "forward/vertexTerrain.glsl"), VertexTemplate.POSITION_TEXCOORD_NORMAL), scene.getTerrain());
		skyboxRenderer = new SkyboxRenderer(Assets.newShader(new ClasspathAsset(AssetNamespace.SHADERS, "forward/fragmentSkybox.glsl"), 
				new ClasspathAsset(AssetNamespace.SHADERS, "forward/vertexSkybox.glsl"), VertexTemplate.POSITION), 
			engine, camera);
		waterRenderer = new WaterRenderer(Assets.newShader(new ClasspathAsset(AssetNamespace.SHADERS, "forward/fragmentWater.glsl"), 
				new ClasspathAsset(AssetNamespace.SHADERS, "forward/vertexWater.glsl"), VertexTemplate.POSITION),
				engine, scene.getWater(), Assets.newTexture(new ClasspathAsset(AssetNamespace.TEXTURES, "waterDUDV.png")), 
				Assets.newTexture(new ClasspathAsset(AssetNamespace.TEXTURES, "waterNormal.png")), lights[0]);
		shadowRenderer = new ShadowRenderer(Assets.newShader(new ClasspathAsset(AssetNamespace.SHADERS, "forward/fragmentShadow.glsl"), 
				new ClasspathAsset(AssetNamespace.SHADERS, "forward/vertexShadow.glsl"), VertexTemplate.POSITION_TEXCOORD),
				engine, camera);
//		postProcessingRenderer = new PostProcessingRenderer();
		animatedModelRenderer = new AnimatedModelRenderer(Assets.newShader(new ClasspathAsset(AssetNamespace.SHADERS, "forward/fragmentSkeletal.glsl"),
				new ClasspathAsset(AssetNamespace.SHADERS, "forward/vertexSkeletal.glsl"), VertexTemplate.POSITION_TEXCOORD_NORMAL_JOINTID_WEIGHT),
				Assets.newShader(new ClasspathAsset(AssetNamespace.SHADERS, "forward/fragmentSkeletalNormals.glsl"),
						new ClasspathAsset(AssetNamespace.SHADERS, "forward/vertexSkeletalNormals.glsl"), VertexTemplate.POSITION_TEXCOORD_NORMAL_JOINTID_WEIGHT));
	}

	private void renderAnimated(boolean normalMapped, HashMap<Geometry, ArrayList<Entity>> entityMap, Vector3f lightDir, Vector4f plane, Vector3f skyColor, float ambientLightFactor) {
		animatedModelRenderer.bind(normalMapped, engine, camera, lightDir, skyColor, plane, ambientLightFactor);
		for (Map.Entry<Geometry, ArrayList<Entity>> entry : entityMap.entrySet()) {
			animatedModelRenderer.useGeometry(entry.getKey());
			for (Entity entity : entry.getValue()) {
				animatedModelRenderer.render(entity, lights, lightCount, normalMapped);
			}			
		}
		animatedModelRenderer.useGeometry(null);
		animatedModelRenderer.unbind(engine);
	}
	
	private void renderDefault(HashMap<Geometry, ArrayList<Entity>> entityMap, Shader shader, boolean normalMaps, Vector4f clipPlane, boolean sendViewMatrix, Vector3f skyColor, float ambientLightFactor) {
		shader.bind();
		if (normalMaps) {
			shader.uploadInt("diffuseTexture", 0);
			shader.uploadInt("normalTexture", 1);
		} else {
			shader.uploadInt("texture", 0);
		}
		shader.uploadVector("plane", clipPlane);
		if (sendViewMatrix) {
			camera.uploadTo(shader);
		} else {
			shader.uploadMatrix("projectionMatrix", camera.getProjectionMatrix());
			emptyMatrix.set(camera.getViewMatrix());
			shader.uploadMatrix("viewMatrix", emptyMatrix);
		}
		for (int i = 0; i < MAX_LIGHTS; i++) {
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
		shader.uploadVector("skyColor", skyColor);
		shader.uploadFloat("ambientLightFactor", ambientLightFactor);
		Geometry lastGeometry = null;
		Material lastMaterial = null;
		for (Map.Entry<Geometry, ArrayList<Entity>> entry : entityMap.entrySet()) {
			entry.getKey().bind();
			for (Entity entity : entry.getValue()) {
				if (lastMaterial == null || !lastMaterial.equals(entity.getMaterial())) {
					Material material = entity.getMaterial();
					shader.uploadFloat("materialShineDamper", material.getShineDamper());
					shader.uploadFloat("materialReflectivity", material.getReflectivity());
					material.getDiffuseTexture().bind(0);
					if (normalMaps) {
						material.getNormalTexture().bind(1);
					}
				}
				shader.uploadMatrix("modelMatrix", entity.getModelMatrix());
				shader.uploadVector("textureAtlasSize", new Vector2f(entity.getAtlasColumns(), entity.getAtlasRows()));
				shader.uploadVector("textureAtlasOffset", new Vector2f((float) entity.getAtlasX() / (float) entity.getAtlasColumns(), 
						(float) entity.getAtlasY() / (float) entity.getAtlasColumns()));
				entity.getGeometry().renderGeometry();
				lastMaterial = entity.getMaterial();
			}
			lastGeometry = entry.getKey();
		}
		if (lastMaterial != null) {
			lastMaterial.getDiffuseTexture().unbind();
			lastGeometry.unbind();
		}
		shader.unbind();
	}
	
	private void renderScene(Vector4f plane, boolean sendViewMatrix, Vector3f skyColor, float ambientLightFactor) {
		terrainRenderer.render(camera, lights, lightCount, MAX_LIGHTS, skyColor, plane, shadowRenderer.getToShadowSpaceMatrix(), shadowRenderer.getShadowMap(), ambientLightFactor);
		renderDefault(scene.getDefaultEntityMap(), defaultShader, false, plane, sendViewMatrix, skyColor, ambientLightFactor);
		renderDefault(scene.getNormalMappedEntityMap(), normalMappedShader, true, plane, sendViewMatrix, skyColor, ambientLightFactor);
		renderAnimated(false, scene.getDefaultAnimatedEntityMap(), new Vector3f(0.2f, -0.3f, -0.8f), plane, skyColor, ambientLightFactor);
		renderAnimated(true, scene.getNormalMappedAnimatedEntityMap(), new Vector3f(0.2f, -0.3f, -0.8f), plane, skyColor, ambientLightFactor);
		skyboxRenderer.render(camera, skyColor);
	}
	
	@Override
	public void renderScene(float delta) {
		int width = engine.getSettings().width;
		int height = engine.getSettings().height;
		Mouse mouse = engine.getMouse();
		Vector3f fogColor = skyboxRenderer.getSmoothFogColor();
		engine.getRenderingBackend().setBackgroundColor(fogColor.x, fogColor.y, fogColor.z);
		for (ArrayList<Entity> entities : scene.getDefaultAnimatedEntityMap().values()) {
			for (Entity entity : entities) {
				if (entity.isAnimated()) {
					entity.getAnimatedModel().update(delta);
				}
			}
		}
		camera.update();
		scene.getMousePicker().update(mouse, width, height);
		engine.getRenderingBackend().setDepth(true);
		shadowRenderer.render(lights[0].getPosition(), (shader) -> {
			for (Geometry geometry : scene.getDefaultEntityMap().keySet()) {
				geometry.bind();
				for (Entity entity : scene.getDefaultEntityMap().get(geometry)) {
					shader.uploadMatrix("modelMatrix", entity.getModelMatrix());
					entity.getMaterial().getDiffuseTexture().bind(0);
					geometry.renderGeometry();
				}
				geometry.unbind();
			}
			for (Geometry geometry : scene.getNormalMappedEntityMap().keySet()) {
				geometry.bind();
				for (Entity entity : scene.getNormalMappedEntityMap().get(geometry)) {
					shader.uploadMatrix("modelMatrix", entity.getModelMatrix());
					entity.getMaterial().getDiffuseTexture().bind(0);
					geometry.renderGeometry();
				}
				geometry.unbind();
			}
			for (Geometry geometry : scene.getDefaultAnimatedEntityMap().keySet()) {
				geometry.bind();
				for (Entity entity : scene.getDefaultAnimatedEntityMap().get(geometry)) {
					shader.uploadMatrix("modelMatrix", entity.getModelMatrix());
					entity.getAnimatedModel().getMaterial().getDiffuseTexture().bind(0);
					geometry.renderGeometry();
				}
				geometry.unbind();
			}
			for (Geometry geometry : scene.getNormalMappedAnimatedEntityMap().keySet()) {
				geometry.bind();
				for (Entity entity : scene.getNormalMappedAnimatedEntityMap().get(geometry)) {
					shader.uploadMatrix("modelMatrix", entity.getModelMatrix());
					entity.getAnimatedModel().getMaterial().getDiffuseTexture().bind(0);
					geometry.renderGeometry();
				}
				geometry.unbind();
			}
		});
		shadowRenderer.computeShadowSpaceMatrix();
		skyboxRenderer.update(camera, delta);
		waterRenderer.render(camera, engine, (plane, sendViewMatrix) -> 
				renderScene(plane, sendViewMatrix, fogColor, (1 - skyboxRenderer.getBlendFactor()) * AMBIENT_LIGHT), 
			delta, shadowRenderer.getShadowMap(), shadowRenderer.getToShadowSpaceMatrix(), ShadowBox.SHADOW_DISTANCE, ShadowRenderer.SHADOW_MAP_SIZE);
	}
	
}
