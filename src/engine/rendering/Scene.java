package engine.rendering;

import java.util.ArrayList;
import java.util.HashMap;

import engine.Entity;
import engine.terrain.Terrain;
import engine.water.Water;
import utils.MousePicker;

public class Scene {

	private HashMap<Geometry, ArrayList<Entity>> defaultEntityMap = new HashMap<>();
	
	private HashMap<Geometry, ArrayList<Entity>> normalMappedEntityMap = new HashMap<>();

	private HashMap<Geometry, ArrayList<Entity>> defaultAnimatedEntityMap = new HashMap<>();

	private HashMap<Geometry, ArrayList<Entity>> normalMappedAnimatedEntityMap = new HashMap<>();

	private Terrain terrain;
	
	private Rendering rendering;

	private Water water;

	private MousePicker mousePicker;
	
	public void setRendering(Rendering rendering) {
		this.rendering = rendering;
	}
	
	public void setTerrain(Terrain terrain) {
		this.terrain = terrain;
	}
	
	public Terrain getTerrain() {
		return terrain;
	}
	
	public void setWater(Water water) {
		this.water = water;
	}
	
	public Water getWater() {
		return water;
	}
	
	public void setMousePicker(MousePicker mousePicker) {
		this.mousePicker = mousePicker;
	}
	
	public MousePicker getMousePicker() {
		return mousePicker;
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
	
	public HashMap<Geometry, ArrayList<Entity>> getDefaultEntityMap() {
		return defaultEntityMap;
	}

	public HashMap<Geometry, ArrayList<Entity>> getNormalMappedEntityMap() {
		return normalMappedEntityMap;
	}

	public HashMap<Geometry, ArrayList<Entity>> getDefaultAnimatedEntityMap() {
		return defaultAnimatedEntityMap;
	}

	public HashMap<Geometry, ArrayList<Entity>> getNormalMappedAnimatedEntityMap() {
		return normalMappedAnimatedEntityMap;
	}

	public interface Rendering {
		
		public void renderScene(float delta);
		
	}
	
	public void renderScene(float delta) {
		rendering.renderScene(delta);
	}
	
}
