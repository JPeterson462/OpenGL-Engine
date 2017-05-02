package engine.animation;

import java.util.ArrayList;

import org.joml.Matrix4f;

public class Joint {
	
	public final int index;
	
	public final String name;
	
	public final ArrayList<Joint> children = new ArrayList<>();
	
	private Matrix4f animatedTransform;
	
	private final Matrix4f localBindTransform;
	
	private Matrix4f inverseBindTransform = new Matrix4f();
	
	public Joint(int index, String name, Matrix4f bindLocalTransform) {
		this.index = index;
		this.name = name;
		this.localBindTransform = bindLocalTransform;
	}
	
	public void addChild(Joint child) {
		children.add(child);
	}
	
	public Matrix4f getAnimatedTransform() {
		return animatedTransform;
	}
	
	public void setAnimatedTransform(Matrix4f animatedTransform) {
		this.animatedTransform = animatedTransform;
	}
	
	public Matrix4f getInverseBindTransform() {
		return inverseBindTransform;
	}
	
	public void calcInverseBindTransform(Matrix4f parentBindTransform) {
		Matrix4f bindTransform = new Matrix4f();
		parentBindTransform.mul(localBindTransform, bindTransform);
		bindTransform.invert(inverseBindTransform);
		for (Joint child : children) {
			child.calcInverseBindTransform(bindTransform);
		}
	}

}
