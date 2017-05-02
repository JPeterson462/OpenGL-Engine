package engine.animation;

import java.util.Map;

public class KeyFrame {
	
	private final float timestamp;
	
	private final Map<String, JointTransform> pose;
	
	public KeyFrame(float timestamp, Map<String, JointTransform> pose) {
		this.timestamp = timestamp;
		this.pose = pose;
	}
	
	public float getTimestamp() {
		return timestamp;
	}
	
	public Map<String, JointTransform> getJointKeyFrames() {
		return pose;
	}

}
