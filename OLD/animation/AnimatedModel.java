package engine.animation;

import org.joml.Matrix4f;

import engine.rendering.Geometry;
import engine.rendering.Texture;

public class AnimatedModel {
	
	public static final int MAX_WEIGHTS = 3;
	
	private final Geometry model;
	
	private final Texture texture;
	
	private final Joint rootJoint;
	
	private final int jointCount;
	
	private final Animator animator;
	
	public AnimatedModel(Geometry model, Texture texture, Joint rootJoint, int jointCount) {
		this.model = model;
		this.texture = texture;
		this.rootJoint = rootJoint;
		this.jointCount = jointCount;
		animator = new Animator(this);
		rootJoint.calcInverseBindTransform(new Matrix4f());
	}
	
	public Geometry getGeometry() {
		return model;
	}
	
	public Texture getTexture() {
		return texture;
	}
	
	public Joint getRootJoint() {
		return rootJoint;
	}
	
	public void doAnimation(Animation animation) {
		animator.doAnimation(animation);
	}
	
	public void update(float delta) {
		animator.update(delta);
	}
	
	public Matrix4f[] getJointTransforms() {
		Matrix4f[] jointMatrices = new Matrix4f[jointCount];
		addJointsToArray(rootJoint, jointMatrices);
		return jointMatrices;
	}

	private void addJointsToArray(Joint headJoint, Matrix4f[] jointMatrices) {
		jointMatrices[headJoint.index] = headJoint.getAnimatedTransform();
		for (Joint childJoint : headJoint.children) {
			addJointsToArray(childJoint, jointMatrices);
		}
	}

}
