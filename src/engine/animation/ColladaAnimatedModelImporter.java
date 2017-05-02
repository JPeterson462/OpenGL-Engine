package engine.animation;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.xml.sax.SAXException;

import com.esotericsoftware.minlog.Log;

import collada.ColladaLoader;
import collada.animation.AnimatedModelData;
import collada.animation.JointData;
import collada.animation.MeshData;
import engine.Engine;
import engine.rendering.Geometry;
import engine.rendering.Texture;
import engine.rendering.Vertex;
import utils.ArrayUtils;

public class ColladaAnimatedModelImporter implements AnimatedModelImporter {

	@Override
	public AnimatedModel loadAnimatedModelImpl(String path, Engine engine, Texture texture) {
		try {
			AnimatedModelData entityData = ColladaLoader.loadColladaModel(engine.getResource("models/" + path), AnimatedModel.MAX_WEIGHTS);
			Geometry geometry = createGeometry(entityData.getMeshData(), engine);
			int jointCount = entityData.getJointsData().jointCount;
			Joint headJoint = createJoints(entityData.getJointsData().headJoint);
			return new AnimatedModel(geometry, texture, headJoint, jointCount);
		} catch (SAXException | IOException | ParserConfigurationException e) {
			Log.error("Encountered error while loading animated model: " + path, e);
			return null;
		}
	}
	
	private Geometry createGeometry(MeshData data, Engine engine) {
		ArrayList<Vertex> vertices = new ArrayList<>();
		float[] positions = data.getVertices();
		float[] texCoords = data.getTextureCoords();
		float[] normals = data.getNormals();
		int[] jointIDs = data.getJointIDs();
		float[] weights = data.getVertexWeights();
		for (int i = 0; i < data.getVertexCount(); i++) {
			vertices.add(new Vertex(vec3(positions, i * 3), vec2(texCoords, i * 2), vec3(normals, i * 3), vec3(jointIDs, i * 3), vec3(weights, i * 3)));
		}
		return engine.getRenderingBackend().createGeometry(vertices, ArrayUtils.toList(data.getIndices()));
	}
	
	private Joint createJoints(JointData data) {
		Joint joint = new Joint(data.index, data.name, data.bindLocalTransform);
		for (JointData child : data.children) {
			joint.addChild(createJoints(child));
		}
		return joint;
	}
	
	private Vector3i vec3(int[] data, int offset) {
		return new Vector3i(data[offset + 0], data[offset + 1], data[offset + 2]);
	}
	
	private Vector3f vec3(float[] data, int offset) {
		return new Vector3f(data[offset + 0], data[offset + 1], data[offset + 2]);
	}
	
	private Vector2f vec2(float[] data, int offset) {
		return new Vector2f(data[offset + 0], data[offset + 1]);
	}

}
