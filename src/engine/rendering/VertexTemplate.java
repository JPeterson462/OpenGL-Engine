package engine.rendering;

public enum VertexTemplate {
	
	POSITION_TEXCOORD_COLOR,
	
	POSITION,
	
	POSITION_TEXCOORD,
	
	POSITION_TEXCOORD_NORMAL, 
	
	POSITION_TEXCOORD_NORMAL_TANGENT,
	
	POSITION_TEXCOORD_NORMAL_JOINTID_WEIGHT;
	
	public boolean hasPosition() {
		return ordinal() >= POSITION.ordinal();
	}

	public boolean hasTexCoord() {
		return ordinal() >= POSITION_TEXCOORD.ordinal();
	}

	public boolean hasNormal() {
		return ordinal() >= POSITION_TEXCOORD_NORMAL.ordinal();
	}
	
	public boolean hasTangent() {
		return ordinal() >= POSITION_TEXCOORD_NORMAL_TANGENT.ordinal();
	}

}
