package engine.animation;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import utils.MathUtils;

public class JointTransform {
	
	private final Vector3f position;
	
	private final Quaternionf rotation;
	
	public JointTransform(Vector3f position, Quaternionf rotation) {
		this.position = position;
		this.rotation = rotation;
	}
	
	public Matrix4f getLocalTransform() {
		Matrix4f matrix = new Matrix4f();
		matrix.translate(position);
		matrix.mul(MathUtils.toRotationMatrix(rotation));
		return matrix;
	}
	
	public static JointTransform interpolate(JointTransform frameA, JointTransform frameB, float t) {
		Vector3f newPosition = new Vector3f();
		Quaternionf newRotation = new Quaternionf();
		frameA.position.lerp(frameB.position, t, newPosition);
		frameA.rotation.slerp(frameB.rotation, t, newRotation);
		return new JointTransform(newPosition, newRotation);
	}

}
