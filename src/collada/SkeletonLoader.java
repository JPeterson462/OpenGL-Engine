package collada;

import java.nio.FloatBuffer;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import collada.animation.JointData;
import collada.animation.SkeletonData;

public class SkeletonLoader {

	private XMLNode armatureData;

	private List<String> boneOrder;

	private int jointCount = 0;

	private static final Matrix4f CORRECTION = new Matrix4f().rotate((float) Math.toRadians(-90), new Vector3f(1, 0, 0));

	public SkeletonLoader(XMLNode visualSceneNode, List<String> boneOrder) {
		this.armatureData = visualSceneNode.getChild("visual_scene").getChildWithAttribute("node", "id", "Armature");
		this.boneOrder = boneOrder;
	}

	public SkeletonData extractBoneData() {
		XMLNode headNode = armatureData.getChild("node");
		JointData headJoint = loadJointData(headNode, true);
		return new SkeletonData(jointCount, headJoint);
	}

	private JointData loadJointData(XMLNode jointNode, boolean isRoot) {
		JointData joint = extractMainJointData(jointNode, isRoot);
		for (XMLNode childNode : jointNode.getChildren("node")) {
			joint.addChild(loadJointData(childNode, false));
		}
		return joint;
	}

	private JointData extractMainJointData(XMLNode jointNode, boolean isRoot) {
		String nameId = jointNode.getAttribute("id");
		int index = boneOrder.indexOf(nameId);
		String[] matrixData = jointNode.getChild("matrix").getData().split(" ");
		Matrix4f matrix = new Matrix4f(convertData(matrixData));
		matrix.transpose();
		if (isRoot) {
			//because in Blender z is up, but in our game y is up.
			CORRECTION.mul(matrix, matrix);
		}
		jointCount++;
		return new JointData(index, nameId, matrix);
	}

	private FloatBuffer convertData(String[] rawData) {
		float[] matrixData = new float[16];
		for (int i = 0; i < matrixData.length; i++) {
			matrixData[i] = Float.parseFloat(rawData[i]);
		}
		FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
		buffer.put(matrixData);
		buffer.flip();
		return buffer;
	}

}
