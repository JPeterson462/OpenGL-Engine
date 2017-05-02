package collada.animation;

import java.util.ArrayList;

public class KeyFrameData {
	
	public final float time;
	
	public final ArrayList<JointTransformData> jointTransforms = new ArrayList<>();
	
	public KeyFrameData(float time) {
		this.time = time;
	}
	
	public void addJointTransform(JointTransformData transform) {
		jointTransforms.add(transform);
	}

}
