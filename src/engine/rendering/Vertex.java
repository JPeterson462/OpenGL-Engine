package engine.rendering;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4f;

public class Vertex {
	
	private Vector3f position;
	
	private Vector2f textureCoord;
	
	private Vector3f normal;
	
	private Vector3f tangent;
	
	private Vector4f color;
	
	private Vector3i jointIDs;
	
	private Vector3f weights;
	
	private VertexTemplate template;

	public Vertex(Vector3f position) {
		this.position = position;
		template = VertexTemplate.POSITION;
	}

	public Vertex(Vector3f position, Vector2f textureCoord) {
		this.position = position;
		this.textureCoord = textureCoord;
		template = VertexTemplate.POSITION_TEXCOORD;
	}

	public Vertex(Vector3f position, Vector2f textureCoord, Vector3f normal) {
		this.position = position;
		this.textureCoord = textureCoord;
		this.normal = normal;
		template = VertexTemplate.POSITION_TEXCOORD_NORMAL;
	}
	
	public Vertex(Vector3f position, Vector2f textureCoord, Vector3f normal, Vector3f tangent) {
		this.position = position;
		this.textureCoord = textureCoord;
		this.normal = normal;
		this.tangent = tangent;
		template = VertexTemplate.POSITION_TEXCOORD_NORMAL_TANGENT;
	}
	
	public Vertex(Vector2f position, Vector2f textureCoord, Vector4f color) {
		this.position = new Vector3f(position, 0);
		this.textureCoord = textureCoord;
		this.color = color;
		template = VertexTemplate.POSITION_TEXCOORD_COLOR;
	}
	
	public Vertex(Vector3f position, Vector2f textureCoord, Vector3f normal, Vector3i jointIDs, Vector3f weights) {
		this.position = position;
		this.textureCoord = textureCoord;
		this.normal = normal;
		this.jointIDs = jointIDs;
		this.weights = weights;
		template = VertexTemplate.POSITION_TEXCOORD_NORMAL_JOINTID_WEIGHT;
	}
	
	public Vector3f getPosition() {
		return position;
	}
	
	public Vector2f getTextureCoord() {
		return textureCoord;
	}
	
	public Vector3f getNormal() {
		return normal;
	}
	
	public Vector3f getTangent() {
		return tangent;
	}
	
	public Vector4f getColor() {
		return color;
	}
	
	public Vector3i getJointIDs() {
		return jointIDs;
	}
	
	public Vector3f getWeights() {
		return weights;
	}
	
	public VertexTemplate getTemplate() {
		return template;
	}
	
	public void reset() {
		if (position != null)
			position.zero();
		if (textureCoord != null)
			textureCoord.zero();
		if (normal != null)
			normal.zero();
		if (tangent != null)
			tangent.zero();
		if (color != null)
			color.zero();
	}
	
	public String toString() {
		String string = "";
		if (position != null)
			string += position + " ";
		if (textureCoord != null)
			string += textureCoord + " ";
		if (normal != null)
			string += normal + " ";
		if (tangent != null)
			string += tangent + " ";
		if (color != null)
			string += color + " ";
		return string.trim();
	}

}
