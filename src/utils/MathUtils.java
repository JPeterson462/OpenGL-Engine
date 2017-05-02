package utils;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class MathUtils {

	public static float getPitch(Vector3f direction) {
		return (float) Math.atan2(Math.sqrt(direction.x * direction.x + direction.z * direction.z), direction.y);
	}

	public static float getYaw(Vector3f direction) {
		return (float) Math.atan2(direction.x, -direction.y);
	}

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

	// From ThinMatrix's tutorials
	public static Quaternionf fromMatrix(Matrix4f matrix) {
		float w, x, y, z;
		float diagonal = matrix.m00() + matrix.m11() + matrix.m22();
		if (diagonal > 0) {
			float w4 = (float) (Math.sqrt(diagonal + 1f) * 2f);
			w = w4 / 4f;
			x = (matrix.m21() - matrix.m12()) / w4;
			y = (matrix.m02() - matrix.m20()) / w4;
			z = (matrix.m10() - matrix.m01()) / w4;
		} else if ((matrix.m00() > matrix.m11()) && (matrix.m00() > matrix.m22())) {
			float x4 = (float) (Math.sqrt(1f + matrix.m00() - matrix.m11() - matrix.m22()) * 2f);
			w = (matrix.m21() - matrix.m12()) / x4;
			x = x4 / 4f;
			y = (matrix.m01() + matrix.m10()) / x4;
			z = (matrix.m02() + matrix.m20()) / x4;
		} else if (matrix.m11() > matrix.m22()) {
			float y4 = (float) (Math.sqrt(1f + matrix.m11() - matrix.m00() - matrix.m22()) * 2f);
			w = (matrix.m02() - matrix.m20()) / y4;
			x = (matrix.m01() + matrix.m10()) / y4;
			y = y4 / 4f;
			z = (matrix.m12() + matrix.m21()) / y4;
		} else {
			float z4 = (float) (Math.sqrt(1f + matrix.m22() - matrix.m00() - matrix.m11()) * 2f);
			w = (matrix.m10() - matrix.m01()) / z4;
			x = (matrix.m02() + matrix.m20()) / z4;
			y = (matrix.m12() + matrix.m21()) / z4;
			z = z4 / 4f;
		}
		return new Quaternionf(x, y, z, w);
	}
	
	// From ThinMatrix's tutorials
	public static Matrix4f toRotationMatrix(Quaternionf rotation) {
		Matrix4f matrix = new Matrix4f();
		final float xy = rotation.x * rotation.y;
		final float xz = rotation.x * rotation.z;
		final float xw = rotation.x * rotation.w;
		final float yz = rotation.y * rotation.z;
		final float yw = rotation.y * rotation.w;
		final float zw = rotation.z * rotation.w;
		final float xSquared = rotation.x * rotation.x;
		final float ySquared = rotation.y * rotation.y;
		final float zSquared = rotation.z * rotation.z;
		matrix.m00(1 - 2 * (ySquared + zSquared));
		matrix.m01(2 * (xy - zw));
		matrix.m02(2 * (xz + yw));
		matrix.m03(0);
		matrix.m10(2 * (xy + zw));
		matrix.m11(1 - 2 * (xSquared + zSquared));
		matrix.m12(2 * (yz - xw));
		matrix.m13(0);
		matrix.m20(2 * (xz - yw));
		matrix.m21(2 * (yz + xw));
		matrix.m22(1 - 2 * (xSquared + ySquared));
		matrix.m23(0);
		matrix.m30(0);
		matrix.m31(0);
		matrix.m32(0);
		matrix.m33(1);
return matrix;
	}

}
