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
			Vector3f position = new Vector3f(positions[i * 3 + 0], positions[i * 3 + 1], positions[i * 3 + 2]);
			Vector2f texCoord = new Vector2f(texCoords[i * 2 + 0], texCoords[i * 2 + 1]);
			Vector3f normal = new Vector3f(normals[i * 3 + 0], normals[i * 3 + 1], normals[i * 3 + 2]);
			Vector3i joints = new Vector3i(jointIDs[i * 3 + 0], jointIDs[i * 3 + 1], jointIDs[i * 3 + 2]);
			Vector3f weight = new Vector3f(weights[i * 3 + 0], weights[i * 3 + 1], weights[i * 3 + 2]);
			vertices.add(new Vertex(position, texCoord, normal, joints, weight));
		}
		int[] indexArray = data.getIndices();
		ArrayList<Integer> indices = new ArrayList<>();
		for (int i = 0; i < indexArray.length; i++) {
			indices.add(indexArray[i]);
		}
		return engine.getRenderingBackend().createGeometry(vertices, indices);
	}
	
	private Joint createJoints(JointData data) {
		Joint joint = new Joint(data.index, data.name, data.bindLocalTransform);
		for (JointData child : data.children) {
			joint.addChild(createJoints(child));
		}
		return joint;
	}
	
}
