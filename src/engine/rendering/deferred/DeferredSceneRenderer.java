package engine.rendering.deferred;

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
import engine.lights.AmbientLight;
import engine.lights.Light;
import engine.rendering.Framebuffer;
import engine.rendering.Geometry;
import engine.rendering.Material;
import engine.rendering.Scene;
import engine.rendering.Shader;
import engine.rendering.VertexTemplate;
import utils.MousePicker;

public class DeferredSceneRenderer implements Scene.Rendering {
	
	private Shader defaultShader, normalMappedShader, waterShader;
	
	private Camera camera;
	
	private ArrayList<Light> lights = new ArrayList<>();
	
	private Engine engine;
	
	private Scene scene;
	
	private Framebuffer geometryPass1, geometryPass2, geometryPass3, geometryPass4, lightingPass;

	public static final float AMBIENT_LIGHT = 0.4f;
	
	private Matrix4f emptyMatrix = new Matrix4f();
	
	private WaterRenderer waterRenderer;
	
	private TerrainRenderer terrainRenderer;
	
	private SkyboxRenderer skyboxRenderer;
	
	private AnimatedModelRenderer animatedModelRenderer;
	
	public DeferredSceneRenderer(Camera camera, Scene scene, Engine engine, Vector3f ambientLight) {
		int width = engine.getSettings().width, height = engine.getSettings().height;
		this.camera = camera;
		this.scene = scene;
		this.engine = engine;
		scene.setMousePicker(new MousePicker(camera));
		lights.add(new AmbientLight(new Vector4f(ambientLight.x, ambientLight.y, ambientLight.z, 0.2f)));
		defaultShader = Assets.newShader(new ClasspathAsset(AssetNamespace.SHADERS, "deferred/fragmentDefault.glsl"), 
				new ClasspathAsset(AssetNamespace.SHADERS, "deferred/vertexDefault.glsl"), VertexTemplate.POSITION_TEXCOORD_NORMAL);
		normalMappedShader = Assets.newShader(new ClasspathAsset(AssetNamespace.SHADERS, "deferred/fragmentNormals.glsl"), 
				new ClasspathAsset(AssetNamespace.SHADERS, "deferred/vertexDefault.glsl"), VertexTemplate.POSITION_TEXCOORD_NORMAL);
		waterShader = Assets.newShader(new ClasspathAsset(AssetNamespace.SHADERS, "deferred/fragmentWater.glsl"), 
				new ClasspathAsset(AssetNamespace.SHADERS, "deferred/vertexWater.glsl"), VertexTemplate.POSITION);
		geometryPass1 = engine.getRenderingBackend().createFramebuffer(width, height, 3, false);
		geometryPass2 = engine.getRenderingBackend().createFramebuffer(width / 4, height / 4, 3, false);
		geometryPass3 = engine.getRenderingBackend().createFramebuffer(width, height, 3, false);
		geometryPass4 = engine.getRenderingBackend().createFramebuffer(width, height, 1, false);
		lightingPass = engine.getRenderingBackend().createFramebuffer(width, height, 1, false);
		waterRenderer = new WaterRenderer(engine, Assets.newTexture(new ClasspathAsset(AssetNamespace.TEXTURES, "waterDUDV.png")));
		terrainRenderer = new TerrainRenderer(Assets.newShader(new ClasspathAsset(AssetNamespace.SHADERS, "deferred/fragmentTerrain.glsl"), 
				new ClasspathAsset(AssetNamespace.SHADERS, "deferred/vertexDefault.glsl"), VertexTemplate.POSITION_TEXCOORD_NORMAL),
			scene.getTerrain());
		skyboxRenderer = new SkyboxRenderer(Assets.newShader(new ClasspathAsset(AssetNamespace.SHADERS, "deferred/fragmentSkybox.glsl"), 
				new ClasspathAsset(AssetNamespace.SHADERS, "deferred/vertexSkybox.glsl"), VertexTemplate.POSITION),
				engine, camera);
		animatedModelRenderer = new AnimatedModelRenderer(Assets.newShader(new ClasspathAsset(AssetNamespace.SHADERS, "deferred/fragmentDefault.glsl"),
				new ClasspathAsset(AssetNamespace.SHADERS, "deferred/vertexSkeletal.glsl"), VertexTemplate.POSITION_TEXCOORD_NORMAL_JOINTID_WEIGHT),
				Assets.newShader(new ClasspathAsset(AssetNamespace.SHADERS, "deferred/fragmentNormals.glsl"),
						new ClasspathAsset(AssetNamespace.SHADERS, "deferred/vertexSkeletal.glsl"), VertexTemplate.POSITION_TEXCOORD_NORMAL_JOINTID_WEIGHT));
	}
	
	private void renderAnimated(boolean normalMapped, HashMap<Geometry, ArrayList<Entity>> entityMap, Vector3f lightDir, Vector4f plane, Vector3f skyColor, float ambientLightFactor) {
		animatedModelRenderer.bind(normalMapped, engine, camera, skyColor, plane, ambientLightFactor);
		for (Map.Entry<Geometry, ArrayList<Entity>> entry : entityMap.entrySet()) {
			animatedModelRenderer.useGeometry(entry.getKey());
			for (Entity entity : entry.getValue()) {
				animatedModelRenderer.render(entity, normalMapped);
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
			shader.uploadInt("diffuseTexture", 0);
		}
		shader.uploadVector("plane", clipPlane);
		if (sendViewMatrix) {
			camera.uploadTo(shader);
		} else {
			shader.uploadMatrix("projectionMatrix", camera.getProjectionMatrix());
			emptyMatrix.set(camera.getViewMatrix());
			shader.uploadMatrix("viewMatrix", emptyMatrix);
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
		terrainRenderer.render(camera, skyColor, plane, ambientLightFactor);
		renderDefault(scene.getDefaultEntityMap(), defaultShader, false, plane, sendViewMatrix, skyColor, ambientLightFactor);
		renderDefault(scene.getNormalMappedEntityMap(), normalMappedShader, true, plane, sendViewMatrix, skyColor, ambientLightFactor);
		renderAnimated(false, scene.getDefaultAnimatedEntityMap(), new Vector3f(0.2f, -0.3f, -0.8f), plane, skyColor, ambientLightFactor);
		renderAnimated(true, scene.getNormalMappedAnimatedEntityMap(), new Vector3f(0.2f, -0.3f, -0.8f), plane, skyColor, ambientLightFactor);
		skyboxRenderer.render(camera, skyColor);
	}
	
	@Override
	public void renderScene(float delta) {
		float waterHeight = WaterRenderer.WATER_HEIGHT;
		float softEdgeFix = 0f;
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
		float distanceFromWater = camera.getCenter().y - waterHeight;
		camera.getCenter().y -= 2 * distanceFromWater;
		camera.setPitch(-camera.getPitch());
		camera.update();
		geometryPass1.bind();
		renderScene(new Vector4f(0, 1, 0, -waterHeight + softEdgeFix), false, fogColor, AMBIENT_LIGHT);
		geometryPass1.unbind();
		camera.getCenter().y += 2 * distanceFromWater;
		camera.setPitch(-camera.getPitch());
		camera.update();
		geometryPass2.bind();
		renderScene(new Vector4f(0, -1, 0, waterHeight), false, fogColor, AMBIENT_LIGHT);
		geometryPass2.unbind();
		geometryPass3.bind();
		renderScene(new Vector4f(0, 0, 0, 0), true, fogColor, AMBIENT_LIGHT);
		geometryPass3.unbind();
		geometryPass4.bind();
		waterRenderer.renderWater(waterShader, camera, scene.getWater(), delta, engine, 
				geometryPass1, geometryPass2);
		geometryPass4.unbind();
		// TODO light and shadows
	}

}
