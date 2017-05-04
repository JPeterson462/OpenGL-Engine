package engine.rendering.passes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import engine.Assets;
import engine.Camera;
import engine.Engine;
import engine.Entity;
import engine.input.Mouse;
import engine.lights.Light;
import engine.rendering.Framebuffer;
import engine.rendering.Geometry;
import engine.rendering.Material;
import engine.rendering.Shader;
import engine.rendering.Texture;
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
	
	private ArrayList<Light> lights = new ArrayList<Light>();
	
	private MousePicker mousePicker;
	
	private Matrix4f emptyMatrix = new Matrix4f();
	
	private Framebuffer defaultFramebuffer, lightFramebuffer;
	
	private Shader lightShader, shadowShader;
	
	private Geometry fullscreenQuad;
	
	public SceneRenderer(Shader defaultShader, Shader normalMappedShader, Camera camera, TerrainRenderer terrainRenderer, 
			SkyboxRenderer skyboxRenderer, WaterRenderer waterRenderer, ShadowRenderer shadowRenderer, 
			PostProcessingRenderer postProcessingRenderer, AnimatedModelRenderer animatedModelRenderer, 
			Engine engine, Shader lightShader, Shader shadowShader) {
		this.defaultShader = defaultShader;
		this.normalMappedShader = normalMappedShader;
		this.camera = camera;
		defaultEntityMap = new HashMap<>();
		this.terrainRenderer = terrainRenderer;
		this.skyboxRenderer = skyboxRenderer;
		mousePicker = new MousePicker(camera);
		this.waterRenderer = waterRenderer;
		this.shadowRenderer = shadowRenderer;
		this.postProcessingRenderer = postProcessingRenderer;
		this.animatedModelRenderer = animatedModelRenderer;
		defaultFramebuffer = engine.getRenderingBackend().createFramebuffer(engine.getSettings().width, engine.getSettings().height, 3, false);
		lightFramebuffer = engine.getRenderingBackend().createFramebuffer(engine.getSettings().width, engine.getSettings().height, 3, false);
		this.lightShader = lightShader;
		this.shadowShader = shadowShader;
		fullscreenQuad = Assets.newFullscreenQuad();
		shadowShader.bind();
		shadowShader.uploadInt("sceneDepthMap", 0);
		shadowShader.uploadInt("normalMap", 1);
		shadowShader.uploadInt("diffuseMap", 2);
		shadowShader.uploadInt("materialMap", 3);
		shadowShader.uploadInt("lightDiffuseMap", 4);
		shadowShader.uploadInt("lightDirectionMap0", 5);
		shadowShader.uploadInt("lightDirectionMap1", 6);
	}
	
	public void addLight(Light light) {
		lights.add(light);
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
	
	private void renderAnimated(boolean normalMapped, HashMap<Geometry, ArrayList<Entity>> entityMap, Vector3f lightDir, Engine engine, Vector4f plane) {
		animatedModelRenderer.bind(normalMapped, engine, camera, lightDir, plane);
		for (Map.Entry<Geometry, ArrayList<Entity>> entry : entityMap.entrySet()) {
			animatedModelRenderer.useGeometry(entry.getKey());
			for (Entity entity : entry.getValue()) {
				animatedModelRenderer.render(entity, normalMapped);
			}			
		}
		animatedModelRenderer.useGeometry(null);
		animatedModelRenderer.unbind(engine);
	}
	
	private void render(HashMap<Geometry, ArrayList<Entity>> entityMap, Shader shader, boolean normalMaps, Vector4f clipPlane, boolean sendViewMatrix) {
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
	
	private void renderScene(Vector4f plane, boolean sendViewMatrix, Engine engine, float delta) {
		terrainRenderer.render(camera, plane);
		render(defaultEntityMap, defaultShader, false, plane, sendViewMatrix);
		render(normalMappedEntityMap, normalMappedShader, true, plane, sendViewMatrix);
		renderAnimated(false, defaultAnimatedEntityMap, new Vector3f(0.2f, -0.3f, -0.8f), engine, plane);
		renderAnimated(true, normalMappedAnimatedEntityMap, new Vector3f(0.2f, -0.3f, -0.8f), engine, plane);
		skyboxRenderer.render(camera, skyboxRenderer.getSmoothFogColor());
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
		engine.getRenderingBackend().setBlending(false);
//		shadowRenderer.render(lights[0].getPosition(), (shader) -> {
//			for (Geometry geometry : defaultEntityMap.keySet()) {
//				geometry.bind();
//				for (Entity entity : defaultEntityMap.get(geometry)) {
//					shader.uploadMatrix("modelMatrix", entity.getModelMatrix());
//					entity.getMaterial().getDiffuseTexture().bind(0);
//					geometry.renderGeometry();
//				}
//				geometry.unbind();
//			}
//			for (Geometry geometry : normalMappedEntityMap.keySet()) {
//				geometry.bind();
//				for (Entity entity : normalMappedEntityMap.get(geometry)) {
//					shader.uploadMatrix("modelMatrix", entity.getModelMatrix());
//					entity.getMaterial().getDiffuseTexture().bind(0);
//					geometry.renderGeometry();
//				}
//				geometry.unbind();
//			}
//		});
//		shadowRenderer.computeShadowSpaceMatrix();
		defaultFramebuffer.bind();
//		postProcessingRenderer.render(engine, () -> {
//			skyboxRenderer.update(camera, delta);
//			waterRenderer.render(camera, engine, (plane, sendViewMatrix) -> 
		
					Vector4f plane = new Vector4f(0, 0, 0, 0);
					boolean sendViewMatrix = false;
		
					renderScene(plane, sendViewMatrix, engine, delta);//, 
//			delta);
//		});
		defaultFramebuffer.unbind();
		Texture defaultDepthMap = defaultFramebuffer.getDepthTexture();
		Texture normalMap = defaultFramebuffer.getColorTexture(0);
		Texture diffuseMap = defaultFramebuffer.getColorTexture(1);
		Texture materialMap = defaultFramebuffer.getColorTexture(2);
		lightFramebuffer.bind();
		lightShader.bind();
		camera.uploadTo(lightShader);
		lights.forEach(light -> {
			light.uploadTo(lightShader);
			light.render();
		});
		lightShader.unbind();
		lightFramebuffer.unbind();
		Texture lightDiffuseMap = lightFramebuffer.getColorTexture(0);
		Texture lightDirectionMap0 = lightFramebuffer.getColorTexture(1);
		Texture lightDirectionMap1 = lightFramebuffer.getColorTexture(2);
		shadowShader.bind();
		camera.uploadTo(shadowShader);
		shadowShader.uploadVector("cameraPosition", camera.getCenter());
		defaultDepthMap.bind(0);
		normalMap.bind(1);
		diffuseMap.bind(2);
		materialMap.bind(3);
		lightDiffuseMap.bind(4);
		lightDirectionMap0.bind(5);
		lightDirectionMap1.bind(6);
		fullscreenQuad.bind();
		fullscreenQuad.renderGeometry();
		fullscreenQuad.unbind();
		shadowShader.unbind();
//		engine.getRenderingBackend().setAdditiveBlending(false);
	}
	
}
