package collada.animation;

public class MeshData {
	
	private static final int DIMENSIONS = 3;
	
	private float[] vertices, textureCoords, normals, vertexWeights;
	
	private int[] indices, jointIDs;
	
	public MeshData(float[] vertices, float[] textureCoords, float[] normals, int[] indices, int[] jointIDs, float[] vertexWeights) {
		this.vertices = vertices;
		this.textureCoords = textureCoords;
		this.normals = normals;
		this.indices = indices;
		this.jointIDs = jointIDs;
		this.vertexWeights = vertexWeights;
	}
	
	public float[] getVertices() {
		return vertices;
	}
	
	public float[] getTextureCoords() {
		return textureCoords;
	}
	
	public float[] getNormals() {
		return normals;
	}
	
	public float[] getVertexWeights() {
		return vertexWeights;
	}
	
	public int[] getIndices() {
		return indices;
	}
	
	public int[] getJointIDs() {
		return jointIDs;
	}
	
	public int getVertexCount() {
		return vertices.length / DIMENSIONS;
	}

}
