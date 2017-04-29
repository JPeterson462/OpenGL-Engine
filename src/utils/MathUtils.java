package utils;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class MathUtils {

	public static Vector3f maxY(Vector3f... vectors) {
		float maxY = vectors[0].y;
		Vector3f max = vectors[0];
		for (int i = 1; i < vectors.length; i++) {
			if  (vectors[i].y > maxY) {
				max = vectors[i];
				maxY = max.y;
			}
		}
		return max;
	}

	public static float max(float... floats) {
		float max = floats[0];
		for (int i = 1; i < floats.length; i++) {
			max = Math.max(max, floats[i]);
		}
		return max;
	}

	public static float average(float... floats) {
		float count = 0, sum = 0;
		for (int i = 0; i < floats.length; i++) {
			sum += floats[i];
			count++;
		}
		return sum / count;
	}

	public static float clamp(float value, float min, float max) {
		return Math.max(min, Math.min(max, value));
	}
	
	public static int clamp(int value, int min, int max) {
		return Math.max(min, Math.min(max, value));
	}

	// From ThinMatrix's tutorials
	public static float barryCentric(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos) {
		float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
		float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
		float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
		float l3 = 1.0f - l1 - l2;
		return l1 * p1.y + l2 * p2.y + l3 * p3.y;
	}

}
