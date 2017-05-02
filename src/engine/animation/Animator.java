package engine.animation;

import java.util.HashMap;
import java.util.Map;

import org.joml.Matrix4f;

public class Animator {

	private final AnimatedModel entity;

	private Animation animation;

	private float animationTime = 0;

	public Animator(AnimatedModel entity) {
		this.entity = entity;
	}

	public void doAnimation(Animation animation) {
		animationTime = 0;
		this.animation = animation;
	}

	public void update(float delta) {
		if (animation == null) {
			return;
		}
		increaseAnimationTime(delta);
		Map<String, Matrix4f> currentPose = calculateCurrentAnimationPose();
		applyPoseToJoints(currentPose, entity.getRootJoint(), new Matrix4f());
	}

	private void increaseAnimationTime(float delta) {
		animationTime += delta;
		if (animationTime > animation.getLength()) {
			animationTime %= animation.getLength();
		}
	}

	private Map<String, Matrix4f> calculateCurrentAnimationPose() {
		KeyFrame[] frames = getPreviousAndNextFrames();
		float progression = calculateProgression(frames[0], frames[1]);
		return interpolatePoses(frames[0], frames[1], progression);
	}

	private void applyPoseToJoints(Map<String, Matrix4f> currentPose, Joint joint, Matrix4f parentTransform) {
		Matrix4f currentLocalTransform = currentPose.get(joint.name);
		Matrix4f currentTransform = new Matrix4f();
		parentTransform.mul(currentLocalTransform, currentTransform);
		for (Joint childJoint : joint.children) {
			applyPoseToJoints(currentPose, childJoint, currentTransform);
		}
		currentTransform.mul(joint.getInverseBindTransform(), currentTransform);
		joint.setAnimatedTransform(currentTransform);
	}

	private KeyFrame[] getPreviousAndNextFrames() {
		KeyFrame[] allFrames = animation.getKeyFrames();
		KeyFrame previousFrame = allFrames[0];
		KeyFrame nextFrame = allFrames[0];
		for (int i = 1; i < allFrames.length; i++) {
			nextFrame = allFrames[i];
			if (nextFrame.getTimestamp() > animationTime) {
				break;
			}
			previousFrame = allFrames[i];
		}
		return new KeyFrame[] { previousFrame, nextFrame };
	}

	private float calculateProgression(KeyFrame previousFrame, KeyFrame nextFrame) {
		float totalTime = nextFrame.getTimestamp() - previousFrame.getTimestamp();
		float currentTime = animationTime - previousFrame.getTimestamp();
		return currentTime / totalTime;
	}

	private Map<String, Matrix4f> interpolatePoses(KeyFrame previousFrame, KeyFrame nextFrame, float progression) {
		Map<String, Matrix4f> currentPose = new HashMap<String, Matrix4f>();
		for (String jointName : previousFrame.getJointKeyFrames().keySet()) {
			JointTransform previousTransform = previousFrame.getJointKeyFrames().get(jointName);
			JointTransform nextTransform = nextFrame.getJointKeyFrames().get(jointName);
			JointTransform currentTransform = JointTransform.interpolate(previousTransform, nextTransform, progression);
			currentPose.put(jointName, currentTransform.getLocalTransform());
		}
		return currentPose;
	}

}