package utils;

import java.util.ArrayList;

import org.joml.Vector2f;
import org.joml.Vector3f;

import engine.Engine;
import engine.rendering.Geometry;
import engine.rendering.Vertex;

public class Polygons {
	
	public static Geometry newSphere(Engine engine) {
		ArrayList<Vertex> vertices = new ArrayList<>();
		ArrayList<Integer> indices = new ArrayList<>();
		float delta = 20;
		float radius = 1;
		int index = 0;
		for (float theta = 0; theta < 360; theta += delta) {
			for (float phi = 0; phi < 360; phi += delta, index += 4) {
				vertices.add(getSphericalVertex(radius, theta, phi));
				vertices.add(getSphericalVertex(radius, theta + delta, phi));
				vertices.add(getSphericalVertex(radius, theta + delta, phi + delta));
				vertices.add(getSphericalVertex(radius, theta, phi + delta));
				indices.add(index + 0);
				indices.add(index + 1);
				indices.add(index + 2);
				indices.add(index + 2);
				indices.add(index + 3);
				indices.add(index + 0);
			}
		}
		return engine.getRenderingBackend().createGeometry(vertices, indices, true);
	}
	
	public static Geometry newCone(Engine engine, float innerAngle) {
		ArrayList<Vertex> vertices = new ArrayList<>();
		ArrayList<Integer> indices = new ArrayList<>();
		float delta = 20;
		float radius = 1;
		int index = 0;
		float phi = innerAngle;
		for (float theta = 0; theta < 360; theta += delta, index += 3) {
			vertices.add(getSphericalVertex(0, 0, 0));
			vertices.add(getSphericalVertex(radius, theta, phi));
			vertices.add(getSphericalVertex(radius, theta + delta, phi));
			indices.add(index + 0);
			indices.add(index + 1);
			indices.add(index + 2);
		}
		return engine.getRenderingBackend().createGeometry(vertices, indices, true);
	}
	
	private static Vertex getSphericalVertex(float radius, float theta, float phi) {
		Vector3f position = getSpherical(radius, theta, phi);
		return new Vertex(position, new Vector2f(0, 0), position);
	}
	
	private static Vector3f getSpherical(float radius, float theta, float phi) {
		double x = radius * Math.sin(Math.toRadians(phi)) * Math.cos(Math.toRadians(theta));
		double y = radius * Math.cos(Math.toRadians(phi));
		double z = radius * Math.sin(Math.toRadians(phi)) * Math.sin(Math.toRadians(theta));
		return new Vector3f((float) x, (float) y, (float) z);
	}

}
