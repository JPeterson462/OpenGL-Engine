package engine;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.joml.Vector2f;
import org.joml.Vector3f;

import com.esotericsoftware.minlog.Log;

import engine.models.Model;
import engine.models.ModelImporter;
import engine.rendering.Geometry;
import engine.rendering.Material;
import engine.rendering.Shader;
import engine.rendering.Texture;
import engine.rendering.Vertex;
import engine.rendering.VertexTemplate;
import engine.terrain.TerrainTexturePack;

public class Assets {
	
	private static Engine engine;
	
	public static void attachTo(Engine engine) {
		Assets.engine = engine;
	}
	
	public static Model newModel(String path, boolean computeTangents) {
		return ModelImporter.loadModel("models/" + path, engine, computeTangents);
	}
	
	public static Texture newTexture(String path) {
		return engine.getRenderingBackend().createTexture(engine.getResource("textures/" + path), false, false);
	}
	
	public static Texture newCubemap(String... paths) {
		InputStream[] streams = new InputStream[paths.length];
		for (int i = 0; i < streams.length; i++) {
			streams[i] = engine.getResource("textures/" + paths[i]);
		}
		Texture cubemap = engine.getRenderingBackend().createCubemap(streams);
		for (int i = 0; i < streams.length; i++)
			try {
				streams[i].close();
			} catch (IOException e) {
				Log.error("Failed to close skybox texture streams", e);
			}
		return cubemap;
	}
	
	public static Material newMaterial(String path) {
		return new Material(newTexture(path));
	}
	
	public static Material newMaterial(String diffusePath, String normalPath) {
		return new Material(newTexture(diffusePath), newTexture(normalPath));
	}
	
	public static Shader newShader(String fragmentPath, String vertexPath, VertexTemplate template) {
		return engine.getRenderingBackend().createShader(engine.getResource("shaders/" + fragmentPath), 
				engine.getResource("shaders/" + vertexPath), template);
	}

	public static Shader newInstancedShader(String fragmentPath, String vertexPath, int[] attributes, String[] names) {
		return engine.getRenderingBackend().createInstancedShader(engine.getResource("shaders/" + fragmentPath), 
				engine.getResource("shaders/" + vertexPath), attributes, names);
	}
	
	public static TerrainTexturePack newTerrainTexturePack(String... paths) {
		Texture[] textures = new Texture[paths.length];
		for (int i = 0; i < paths.length; i++) {
			textures[i] = newTexture(paths[i]);
		}
		return new TerrainTexturePack(Material.NO_REFLECTIVITY, textures);
	}
	
	public static Geometry newQuad() {
		ArrayList<Vertex> vertices = new ArrayList<>();
		vertices.add(new Vertex(new Vector3f(0, 0, 0), new Vector2f(0, 0)));
		vertices.add(new Vertex(new Vector3f(1, 0, 0), new Vector2f(1, 0)));
		vertices.add(new Vertex(new Vector3f(1, 1, 0), new Vector2f(1, 1)));
		vertices.add(new Vertex(new Vector3f(0, 1, 0), new Vector2f(0, 1)));
		ArrayList<Integer> indices = new ArrayList<>();
		indices.add(0);
		indices.add(1);
		indices.add(2);
		indices.add(2);
		indices.add(3);
		indices.add(0);
		return engine.getRenderingBackend().createGeometry(vertices, indices);
	}

}
