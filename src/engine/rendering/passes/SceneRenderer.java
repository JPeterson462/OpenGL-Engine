package engine.rendering.passes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import engine.Camera;
import engine.Engine;
import engine.Entity;
import engine.input.Mouse;
import engine.rendering.Geometry;
import engine.rendering.Light;
import engine.rendering.Material;
import engine.rendering.Shader;
import engine.shadows.ShadowBox;
import utils.MousePicker;

public class SceneRenderer {
	
	private Shader defaultShader, normalMappedShader;
	
	private Camera camera;

	private HashMap<Geometry, ArrayList<Entity>> defaultEntityMap = new HashMap<>();
	
	private HashMap<Geometry, ArrayList<Entity>> normalMappedEntityMap = new HashMap<>();

	private HashMap<Geometry, ArrayList<Entity>> defaultAnimatedEntityMap = new HashMap<>();

	private HashMap<Geometry, ArrayList<Entity>> normalMappedAnimatedEntityMap = new HashMap<>();
	
	private TerrainRenderer terrainRenderer;
	
	private SkyboxRenderer skyboxRenderer;
	
	private WaterRenderer waterRenderer;
	
	private ShadowRenderer shadowRenderer;

	private PostProcessingRenderer postProcessingRenderer;
	
	private AnimatedModelRenderer animatedModelRenderer;
	
	public static final float AMBIENT_LIGHT = 0.4f;
	
	public static final int MAX_LIGHTS = 4;
	
	private Light[] lights = new Light[MAX_LIGHTS];
	
	private int lightCount = 0;
	
	private MousePicker mousePicker;
	
	private Matrix4f emptyMatrix = new Matrix4f();
	
	public SceneRenderer(Shader defaultShader, Shader normalMappedShader, Camera camera, TerrainRenderer terrainRenderer, 
			SkyboxRenderer skyboxRenderer, WaterRenderer waterRenderer, ShadowRenderer shadowRenderer, 
			PostProcessingRenderer postProcessingRenderer, AnimatedModelRenderer animatedModelRenderer) {
		this.defaultShader = defaultShader;
		this.normalMappedShader = normalMappedShader;
		this.camera = camera;
		defaultEntityMap = new HashMap<>();
//		directionalLightPosition = new Vector3f(5, 50, -5);
//		directionalLightColor = new Vector3f(1, 1, 1);
		this.terrainRenderer = terrainRenderer;
		this.skyboxRenderer = skyboxRenderer;
		mousePicker = new MousePicker(camera);
		this.waterRenderer = waterRenderer;
		this.shadowRenderer = shadowRenderer;
		this.postProcessingRenderer = postProcessingRenderer;
		this.animatedModelRenderer = animatedModelRenderer;
	}
	
	public void addLight(Light light) {
		lights[lightCount] = light;
		lightCount++;
	}
	
	public void addEntity(Entity entity) {
		if (entity.isAnimated()) {
			System.out.println(entity + " " + entity.getMaterial());
			if (entity.getMaterial().hasNormalTexture()) {
				addEntity(entity, normalMappedAnimatedEntityMap);
			} else {
				addEntity(entity, defaultAnimatedEntityMap);
			}
		} else {
			if (entity.getMaterial().hasNormalTexture()) {
				addEntity(entity, normalMappedEntityMap);
			} else {
				addEntity(entity, defaultEntityMap);
			}
		}
	}
	
	private void addEntity(Entity entity, HashMap<Geometry, ArrayList<Entity>> entityMap) {
		Geometry geometry = entity.isAnimated() ? entity.getAnimatedModel().getModel() : entity.getGeometry();
		ArrayList<Entity> entities = entityMap.get(geometry);
		if (entities != null) {
			entities.add(entity);
		} else {
			entities = new ArrayList<>();
			entities.add(entity);
			entityMap.put(geometry, entities);
		}
	}
	
	private void renderAnimated(boolean normalMapped, HashMap<Geometry, ArrayList<Entity>> entityMap, Vector3f lightDir, Engine engine, Vector4f plane, Vector3f skyColor, float ambientLightFactor) {
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
	
	private void render(HashMap<Geometry, ArrayList<Entity>> entityMap, Shader shader, boolean normalMaps, Vector4f clipPlane, boolean sendViewMatrix, Vector3f skyColor, float ambientLightFactor, Engine engine) {
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
//		boolean hasTransparency = false;
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
//				if (hasTransparency != entity.hasTransparency()) {
//					if (entity.hasTransparency()) {
//						engine.setCulling(false);
//					} else {
//						engine.setCulling(true);
//					}
//					hasTransparency = entity.hasTransparency();
//				}
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
	
	private void renderScene(Vector4f plane, boolean sendViewMatrix, Engine engine, float delta, Vector3f skyColor, float ambientLightFactor) {
		terrainRenderer.render(camera, lights, lightCount, MAX_LIGHTS, skyColor, plane, shadowRenderer.getToShadowSpaceMatrix(), shadowRenderer.getShadowMap(), ambientLightFactor);
		render(defaultEntityMap, defaultShader, false, plane, sendViewMatrix, skyColor, ambientLightFactor, engine);
		render(normalMappedEntityMap, normalMappedShader, true, plane, sendViewMatrix, skyColor, ambientLightFactor, engine);
		renderAnimated(false, defaultAnimatedEntityMap, new Vector3f(0.2f, -0.3f, -0.8f), engine, plane, skyColor, ambientLightFactor);
		renderAnimated(true, normalMappedAnimatedEntityMap, new Vector3f(0.2f, -0.3f, -0.8f), engine, plane, skyColor, ambientLightFactor);
		skyboxRenderer.render(camera, skyColor);
	}
	
	public void render(float delta, Mouse mouse, int width, int height, Engine engine) {
		Vector3f fogColor = skyboxRenderer.getSmoothFogColor();
		engine.getRenderingBackend().setBackgroundColor(fogColor.x, fogColor.y, fogColor.z);
		for (ArrayList<Entity> entities : defaultAnimatedEntityMap.values()) {
			for (Entity entity : entities) {
				if (entity.isAnimated()) {
					entity.getAnimatedModel().update(delta);
				}
			}
		}
		camera.update();
		mousePicker.update(mouse, width, height);
		engine.getRenderingBackend().setDepth(true);
		shadowRenderer.render(lights[0].getPosition(), (shader) -> {
			for (Geometry geometry : defaultEntityMap.keySet()) {
				geometry.bind();
				for (Entity entity : defaultEntityMap.get(geometry)) {
					shader.uploadMatrix("modelMatrix", entity.getModelMatrix());
					entity.getMaterial().getDiffuseTexture().bind(0);
					geometry.renderGeometry();
				}
				geometry.unbind();
			}
			for (Geometry geometry : normalMappedEntityMap.keySet()) {
				geometry.bind();
				for (Entity entity : normalMappedEntityMap.get(geometry)) {
					shader.uploadMatrix("modelMatrix", entity.getModelMatrix());
					entity.getMaterial().getDiffuseTexture().bind(0);
					geometry.renderGeometry();
				}
				geometry.unbind();
			}
			// TODO animated models
		});
		shadowRenderer.computeShadowSpaceMatrix();
		skyboxRenderer.update(camera, delta);
		waterRenderer.render(camera, engine, (plane, sendViewMatrix) -> 
				renderScene(plane, sendViewMatrix, engine, delta, fogColor, (1 - skyboxRenderer.getBlendFactor()) * AMBIENT_LIGHT), 
			delta, shadowRenderer.getShadowMap(), shadowRenderer.getToShadowSpaceMatrix(), ShadowBox.SHADOW_DISTANCE, ShadowRenderer.SHADOW_MAP_SIZE);
	}
	
}
