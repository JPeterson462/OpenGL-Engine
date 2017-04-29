package engine.models;

import org.joml.Vector3f;

public class OBJVertex {

	private static final int NO_INDEX = -1;

	private Vector3f position;

	private int textureIndex = NO_INDEX;

	private int normalIndex = NO_INDEX;

	private OBJVertex duplicateVertex = null;

	private int index;

	private float length;
	
	private Vector3f tangent = new Vector3f();
	
	private Vector3f totalTangent = new Vector3f();
	
	private boolean needToUpdate = false;

	public OBJVertex(int index, Vector3f position) {
		this.index = index;
		this.position = position;
		this.length = position.length();
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

	public OBJVertex getDuplicateVertex() {
		return duplicateVertex;
	}

	public void setDuplicateVertex(OBJVertex duplicateVertex) {
		this.duplicateVertex = duplicateVertex;
	}


}
