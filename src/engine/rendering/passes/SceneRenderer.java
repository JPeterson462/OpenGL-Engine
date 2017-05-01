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
import utils.MousePicker;

public class SceneRenderer {
	
	private Shader defaultShader, normalMappedShader;
	
	private Camera camera;

	private HashMap<Geometry, ArrayList<Entity>> defaultEntityMap = new HashMap<>();
	
	private HashMap<Geometry, ArrayList<Entity>> normalMappedEntityMap = new HashMap<>();
	
	private Vector3f skyColor;
	
	private TerrainRenderer terrainRenderer;
	
	private SkyboxRenderer skyboxRenderer;
	
	private WaterRenderer waterRenderer;
	
	private ShadowRenderer shadowRenderer;
	
	private static final int MAX_LIGHTS = 4;
	
	private Light[] lights = new Light[MAX_LIGHTS];
	
	private int lightCount = 0;
	
	private MousePicker mousePicker;
	
	private Matrix4f emptyMatrix = new Matrix4f();
	
	public SceneRenderer(Shader defaultShader, Shader normalMappedShader, Camera camera, TerrainRenderer terrainRenderer, 
			Vector3f skyColor, SkyboxRenderer skyboxRenderer, WaterRenderer waterRenderer, ShadowRenderer shadowRenderer) {
		this.defaultShader = defaultShader;
		this.normalMappedShader = normalMappedShader;
		this.camera = camera;
		defaultEntityMap = new HashMap<>();
//		directionalLightPosition = new Vector3f(5, 50, -5);
//		directionalLightColor = new Vector3f(1, 1, 1);
		this.terrainRenderer = terrainRenderer;
		this.skyColor = skyColor;
		this.skyboxRenderer = skyboxRenderer;
		mousePicker = new MousePicker(camera);
		this.waterRenderer = waterRenderer;
		this.shadowRenderer = shadowRenderer;
	}
	
	public void addLight(Light light) {
		lights[lightCount] = light;
		lightCount++;
	}
	
	public void addEntity(Entity entity) {
		if (entity.getMaterial().hasNormalTexture()) {
			addEntity(entity, normalMappedEntityMap);
		} else {
			addEntity(entity, defaultEntityMap);
		}
	}
	
	private void addEntity(Entity entity, HashMap<Geometry, ArrayList<Entity>> entityMap) {
		ArrayList<Entity> entities = entityMap.get(entity.getGeometry());
		if (entities != null) {
			entities.add(entity);
		} else {
			entities = new ArrayList<>();
			entities.add(entity);
			entityMap.put(entity.getGeometry(), entities);
		}
	}
	
	public Vector3f getSkyColor() {
		return skyColor;
	}
	
	private void render(HashMap<Geometry, ArrayList<Entity>> entityMap, Shader shader, boolean normalMaps, Vector4f clipPlane, boolean sendViewMatrix) {
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
	
	private void renderScene(Vector4f plane, boolean sendViewMatrix) {
		terrainRenderer.render(camera, lights, lightCount, MAX_LIGHTS, skyColor, plane, shadowRenderer.getToShadowSpaceMatrix(), shadowRenderer.getShadowMap());
		render(defaultEntityMap, defaultShader, false, plane, sendViewMatrix);
		render(normalMappedEntityMap, normalMappedShader, true, plane, sendViewMatrix);
		skyboxRenderer.render(camera);
	}
	
	public void render(float delta, Mouse mouse, int width, int height, Engine engine) {
		camera.update();
		mousePicker.update(mouse, width, height);
		shadowRenderer.render(lights[0].getPosition(), (shader) -> {
			for (Geometry geometry : defaultEntityMap.keySet()) {
				geometry.bind(1);
				for (Entity entity : defaultEntityMap.get(geometry)) {
					shader.uploadMatrix("modelMatrix", entity.getModelMatrix());
					geometry.renderGeometry();
				}
				geometry.unbind(1);
			}
			for (Geometry geometry : normalMappedEntityMap.keySet()) {
				geometry.bind(1);
				for (Entity entity : normalMappedEntityMap.get(geometry)) {
					shader.uploadMatrix("modelMatrix", entity.getModelMatrix());
					geometry.renderGeometry();
				}
				geometry.unbind(1);
			}
		});
		skyboxRenderer.update(delta);
		waterRenderer.render(camera, engine, (plane, sendViewMatrix) -> renderScene(plane, sendViewMatrix), delta);
	}
	
}
