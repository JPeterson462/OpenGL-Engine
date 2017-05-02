package engine.animation;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.xml.sax.SAXException;

import com.esotericsoftware.minlog.Log;

import collada.ColladaLoader;
import collada.animation.AnimationData;
import collada.animation.JointTransformData;
import collada.animation.KeyFrameData;
import engine.Engine;
import utils.MathUtils;

public class ColladaAnimationImporter implements AnimationImporter {

	@Override
	public Animation loadAnimationImpl(String path, Engine engine) {
		try {
			AnimationData animationData = ColladaLoader.loadColladaAnimation(engine.getResource("models/" + path));
			KeyFrame[] frames = new KeyFrame[animationData.keyFrames.length];
			for (int i = 0; i < frames.length; i++) {
				frames[i] = createKeyFrame(animationData.keyFrames[i]);
			}
			return new Animation(animationData.lengthSeconds, frames);
		} catch (SAXException | IOException | ParserConfigurationException e) {
			Log.error("Encountered error while loading animation: " + path, e);
			return null;
		}
	}
	
	private KeyFrame createKeyFrame(KeyFrameData data) {
		Map<String, JointTransform> map = new HashMap<>();
		for (JointTransformData jointData : data.jointTransforms) {
			JointTransform jointTransform = createTransform(jointData);
			map.put(jointData.jointNameId, jointTransform);
		}
		return new KeyFrame(data.time, map);
	}
	
	private JointTransform createTransform(JointTransformData data) {
		Matrix4f matrix = data.jointLocalTransform;
		Vector3f translation = new Vector3f(matrix.m30(), matrix.m31(), matrix.m32());
		Quaternionf rotation = MathUtils.fromMatrix(matrix);
		return new JointTransform(translation, rotation);
	}

}
