package engine;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import engine.animation.AnimatedModel;
import engine.models.Model;
import engine.models.ModelImporter;
import engine.rendering.Geometry;
import engine.rendering.Material;
import engine.rendering.VertexTemplate;

public class Entity {
	
	private Vector3f position = new Vector3f();
	
	private Quaternionf orientation = new Quaternionf();
	
	private float scale = 1;
	
	private Model model;
	
	private AnimatedModel animatedModel;
	
	private Material material;
	
	private Geometry geometry;
	
	private VertexTemplate template = VertexTemplate.POSITION_TEXCOORD_NORMAL;
	
	private Matrix4f modelMatrix = new Matrix4f();
	
	private boolean transparency = false;
	
	private int atlasColumns, atlasRows, atlasX, atlasY;

	public Entity(String modelPath, String materialPath, Engine engine, int atlasColumns, int atlasRows, int atlasX, int atlasY) {
		model = ModelImporter.loadModel(modelPath, engine, false);
		material = new Material(engine.getRenderingBackend().createTexture(engine.getResource(materialPath), false, false));
		geometry = engine.getRenderingBackend().createGeometry(model.getVertices(), model.getIndices(), true);
		this.atlasColumns = atlasColumns;
		this.atlasRows = atlasRows;
		this.atlasX = atlasX;
		this.atlasY = atlasY;
	}
	
	public Entity(Model model, Material material, Geometry geometry, int atlasColumns, int atlasRows, int atlasX, int atlasY) {
		this.model = model;
		this.material = material;
		this.geometry = geometry;
		this.atlasColumns = atlasColumns;
		this.atlasRows = atlasRows;
		this.atlasX = atlasX;
		this.atlasY = atlasY;
	}
	
	public Entity(AnimatedModel animatedModel) {
		this.animatedModel = animatedModel;
	}
	
	public void setTransparency(boolean hasTransparency) {
		transparency = hasTransparency;
	}
	
	public boolean hasTransparency() {
		return transparency;
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public Quaternionf getOrientation() {
		return orientation;
	}

	public void setOrientation(Quaternionf orientation) {
		this.orientation = orientation;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}
	
	public boolean isAnimated() {
		return animatedModel != null;
	}

	public Model getModel() {
		return model;
	}
	
	public AnimatedModel getAnimatedModel() {
		return animatedModel;
	}
	
	public Material getMaterial() {
		return material;
	}

	public Geometry getGeometry() {
		return geometry;
	}

	public VertexTemplate getTemplate() {
		return template;
	}

	public Matrix4f getModelMatrix() {
		modelMatrix.translationRotateScale(position, orientation, scale);
		return modelMatrix;
	}

	public int getAtlasX() {
		return atlasX;
	}

	public void setAtlasX(int atlasX) {
		this.atlasX = atlasX;
	}

	public int getAtlasY() {
		return atlasY;
	}

	public void setAtlasY(int atlasY) {
		this.atlasY = atlasY;
	}

	public int getAtlasColumns() {
		return atlasColumns;
	}

	public int getAtlasRows() {
		return atlasRows;
	}
	
}
