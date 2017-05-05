package engine.models;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.joml.Vector2f;
import org.joml.Vector3f;

import com.esotericsoftware.minlog.Log;

import engine.Asset;
import engine.Engine;
import engine.rendering.Vertex;

public class OBJImporter implements ModelImporter {

	private boolean parsedFaces = false;

	@Override
	public Model loadModelImpl(Asset path, Engine engine, boolean computeTangents) {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(path.read()))) {
			ArrayList<OBJVertex> positions = new ArrayList<>();
			ArrayList<Vector2f> texCoords = new ArrayList<>();
			ArrayList<Vector3f> normals = new ArrayList<>();
			ArrayList<Vertex> vertices = new ArrayList<>();
			ArrayList<Integer> indices = new ArrayList<>();
			parsedFaces = false;
			reader.lines().forEach(line -> {
				String[] parts = line.split(" ");
				if (line.startsWith("v ") && !parsedFaces) {
					positions.add(new OBJVertex(positions.size(), new Vector3f(Float.parseFloat(parts[1]), Float.parseFloat(parts[2]), Float.parseFloat(parts[3]))));
				}
				else if (line.startsWith("vt ") && !parsedFaces) {
					texCoords.add(new Vector2f(Float.parseFloat(parts[1]), Float.parseFloat(parts[2])));
				}
				else if (line.startsWith("vn ") && !parsedFaces) {
					normals.add(new Vector3f(Float.parseFloat(parts[1]), Float.parseFloat(parts[2]), Float.parseFloat(parts[3])));
				}
				else if (line.startsWith("f ")) {
					parsedFaces = true;
					String[] v1 = parts[1].split("/");
					String[] v2 = parts[2].split("/");
					String[] v3 = parts[3].split("/");
					OBJVertex vertex0 = process(v1, indices, texCoords, normals, positions);
					OBJVertex vertex1 = process(v2, indices, texCoords, normals, positions);
					OBJVertex vertex2 = process(v3, indices, texCoords, normals, positions);
					if (computeTangents) {
						calculateTangents(vertex0, vertex1, vertex2, texCoords);
					}
				}
				else {
					// Ignore
				}
			});
			for (int i = 0; i < positions.size(); i++) {
				Vector2f texCoord = texCoords.get(positions.get(i).getTextureIndex());
				if (flags.bitSet(0))
					texCoord.y = 1 - texCoord.y;
				if (flags.bitSet(1))
					texCoord.x = 1 - texCoord.x;
				if (computeTangents) {
					vertices.add(new Vertex(positions.get(i).getPosition(), texCoord, normals.get(positions.get(i).getNormalIndex()), positions.get(i).getTangent()));
				} else {
					vertices.add(new Vertex(positions.get(i).getPosition(), texCoord, normals.get(positions.get(i).getNormalIndex())));
				}
			}
			return new Model(vertices, indices);
		} catch (IOException e) {
			Log.error("Failed to load model: " + path, e);
		}
		return null;
	}
	
	private Vector3f sub(Vector3f v0, Vector3f v1) {
		return new Vector3f(v0).sub(v1);
	}
	
	private Vector2f sub(Vector2f v0, Vector2f v1) {
		return new Vector2f(v0).sub(v1);
	}
	
	private void calculateTangents(OBJVertex v0, OBJVertex v1, OBJVertex v2, ArrayList<Vector2f> texCoords) {
		Vector3f deltaPos1 = sub(v1.getPosition(), v0.getPosition());
		Vector3f deltaPos2 = sub(v2.getPosition(), v0.getPosition());
		Vector2f uv0 = texCoords.get(v0.getTextureIndex());
		Vector2f uv1 = texCoords.get(v1.getTextureIndex());
		Vector2f uv2 = texCoords.get(v2.getTextureIndex());
		Vector2f deltaUv1 = sub(uv1, uv0);
		Vector2f deltaUv2 = sub(uv2, uv0);
		float r = 1.0f / (deltaUv1.x * deltaUv2.y - deltaUv1.y * deltaUv2.x);
		deltaPos1.mul(deltaUv2.y);
		deltaPos2.mul(deltaUv1.y);
		Vector3f tangent = deltaPos1.sub(deltaPos2);
		tangent.mul(r);
		v0.addTangent(tangent);
		v1.addTangent(tangent);
		v2.addTangent(tangent);
	}
	
	private OBJVertex process(String[] data, ArrayList<Integer> indices, ArrayList<Vector2f> texCoords, ArrayList<Vector3f> normals, ArrayList<OBJVertex> vertices) {
		int vertexPointer = Integer.parseInt(data[0]) - 1;
		OBJVertex currentVertex = vertices.get(vertexPointer);
		int textureIndex = Integer.parseInt(data[1]) - 1, normalIndex = Integer.parseInt(data[2]) - 1;
		if (!currentVertex.isSet()) {
			currentVertex.setTextureIndex(textureIndex);
			currentVertex.setNormalIndex(normalIndex);
			indices.add(vertexPointer);
			return currentVertex;
		} else {
			return dealWithAlreadyProcessedVertex(currentVertex, textureIndex, normalIndex, indices, vertices);
		}
	}

	private OBJVertex dealWithAlreadyProcessedVertex(OBJVertex previousVertex, int newTextureIndex,
			int newNormalIndex, ArrayList<Integer> indices, ArrayList<OBJVertex> vertices) {
		if (previousVertex.hasSameTextureAndNormal(newTextureIndex, newNormalIndex)) {
			indices.add(previousVertex.getIndex());
			return previousVertex;
		} else {
			OBJVertex anotherVertex = previousVertex.getDuplicateVertex();
			if (anotherVertex != null) {
				return dealWithAlreadyProcessedVertex(anotherVertex, newTextureIndex, newNormalIndex,
						indices, vertices);
			} else {
				OBJVertex duplicateVertex = new OBJVertex(vertices.size(), previousVertex.getPosition());
				duplicateVertex.setTextureIndex(newTextureIndex);
				duplicateVertex.setNormalIndex(newNormalIndex);
				previousVertex.setDuplicateVertex(duplicateVertex);
				vertices.add(duplicateVertex);
				indices.add(duplicateVertex.getIndex());
				return duplicateVertex;
			}

		}
	}
	
}
