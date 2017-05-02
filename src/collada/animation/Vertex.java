package collada.animation;

import org.joml.Vector3f;

public class Vertex {

	private static final int NO_INDEX = -1;

	private Vector3f position;

	private int textureIndex = NO_INDEX;

	private int normalIndex = NO_INDEX;

	private Vertex duplicateVertex = null;

	private int index;

	private float length;

	private Vector3f tangent = new Vector3f();

	private Vector3f totalTangent = new Vector3f();

	private boolean needToUpdate = false;
	
	private VertexSkinData weightsData;

	public Vertex(int index, Vector3f position, VertexSkinData weightsData) {
		this.index = index;
		this.weightsData = weightsData;
		this.position = position;
		this.length = position.length();
	}
	
	public VertexSkinData getWeightsData() {
		return weightsData;
	}

	public void addTangent(Vector3f tangent) {
		totalTangent.add(tangent);
		needToUpdate = true;
	}

	public Vector3f getTangent() {
		if (needToUpdate) {
			if (totalTangent.lengthSquared() == 0) {
				tangent.zero();
			} else {
				totalTangent.normalize(tangent);
			}
			needToUpdate = false;
		}
		return tangent;
	}

	public int getIndex() {
		return index;
	}

	public float getLength() {
		return length;
	}

	public boolean isSet() {
		return textureIndex != NO_INDEX && normalIndex != NO_INDEX;
	}

	public boolean hasSameTextureAndNormal(int textureIndexOther, int normalIndexOther) {
		return textureIndexOther == textureIndex && normalIndexOther == normalIndex;
	}

	public void setTextureIndex(int textureIndex) {
		this.textureIndex = textureIndex;
	}

	public void setNormalIndex(int normalIndex) {
		this.normalIndex = normalIndex;
	}

	public Vector3f getPosition() {
		return position;
	}

	public int getTextureIndex() {
		return textureIndex;
	}

	public int getNormalIndex() {
		return normalIndex;
	}

	public Vertex getDuplicateVertex() {
		return duplicateVertex;
	}

	public void setDuplicateVertex(Vertex duplicateVertex) {
		this.duplicateVertex = duplicateVertex;
	}

}
