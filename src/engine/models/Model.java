package engine.models;

import java.util.ArrayList;

import engine.rendering.Vertex;

public class Model {

	private ArrayList<Vertex> vertices;
	
	private ArrayList<Integer> indices;
	
	private int hashCode;
	
	public Model(ArrayList<Vertex> vertices, ArrayList<Integer> indices) {
		this.vertices = vertices;
		this.indices = indices;
		hashCode = 31 * vertices.hashCode() + indices.hashCode();
	}
	
	public ArrayList<Vertex> getVertices() {
		return vertices;
	}
	
	public ArrayList<Integer> getIndices() {
		return indices;
	}
	
	public int hashCode() {
		return hashCode;
	}
	
}
