package collada.animation;

import java.util.ArrayList;

import org.joml.Matrix4f;

public class JointData {
	
	public final int index;
	
	public final String name;
	
	public final Matrix4f bindLocalTransform;
	
	public final ArrayList<JointData> children = new ArrayList<>();
	
	public JointData(int index, String nameId, Matrix4f bindLocalTransform) {
		this.index = index;
		this.name = nameId;
		this.bindLocalTransform = bindLocalTransform;
	}
	
	public void addChild(JointData child) {
		children.add(child);
	}

}
