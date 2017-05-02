package engine.animation;

import java.io.InputStream;
import java.util.ArrayList;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;

import collada.ColladaLoader;
import collada.data.AnimatedModelData;
import collada.data.JointData;
import collada.data.MeshData;
import collada.data.SkeletonData;
import engine.Assets;
import engine.rendering.Geometry;
import engine.rendering.Texture;
import engine.rendering.Vertex;

public class AnimatedModelLoader {

	/**
	 * Creates an AnimatedEntity from the data in an entity file. It loads up
	 * the collada model data, stores the extracted data in a VAO, sets up the
	 * joint heirarchy, and loads up the entity's texture.
	 * 
	 * @param entityFile
	 *            - the file containing the data for the entity.
	 * @return The animated entity (no animation applied though)
	 */
	public static AnimatedModel loadEntity(InputStream modelFile, InputStream textureFile) {
		AnimatedModelData entityData = ColladaLoader.loadColladaModel(modelFile, AnimatedModel.MAX_WEIGHTS);
		Geometry model = createGeometry(entityData.getMeshData());
		Texture texture = loadTexture(textureFile);
		SkeletonData skeletonData = entityData.getJointsData();
		Joint headJoint = createJoints(skeletonData.headJoint);
		return new AnimatedModel(model, texture, headJoint, skeletonData.jointCount);
	}

	/**
	 * Loads up the diffuse texture for the model.
	 * 
	 * @param textureFile
	 *            - the texture file.
	 * @return The diffuse texture.
	 */
	private static Texture loadTexture(InputStream textureFile) {
		return Assets.newTexture(textureFile);
	}

	/**
	 * Constructs the joint-hierarchy skeleton from the data extracted from the
	 * collada file.
	 * 
	 * @param data
	 *            - the joints data from the collada file for the head joint.
	 * @return The created joint, with all its descendants added.
	 */
	private static Joint createJoints(JointData data) {
		Joint joint = new Joint(data.index, data.nameId, data.bindLocalTransform);
		for (JointData child : data.children) {
			joint.addChild(createJoints(child));
		}
		return joint;
	}

	/**
	 * Stores the mesh data in a VAO.
	 * 
	 * @param data
	 *            - all the data about the mesh that needs to be stored in the
	 *            VAO.
	 * @return The VAO containing all the mesh data for the model.
	 */
	private static Geometry createGeometry(MeshData data) {
		ArrayList<Vertex> vertices = new ArrayList<>();
		ArrayList<Integer> indices = new ArrayList<>();
		float[] positions = data.getVertices();
		float[] texCoords = data.getTextureCoords();
		float[] normals = data.getNormals();
		int[] jointIDs = data.getJointIds();
		float[] weights = data.getVertexWeights();
		int[] indexArray = data.getIndices();
		for (int v = 0; v < data.getVertexCount(); v++) {
			Vector3f position = new Vector3f(positions[v * 3 + 0], positions[v * 3 + 1], positions[v * 3 + 2]);
			Vector2f texCoord = new Vector2f(texCoords[v * 2 + 0], texCoords[v * 2 + 1]);
			Vector3f normal = new Vector3f(normals[v * 3 + 0], normals[v * 3 + 1], normals[v * 3 + 2]);
			Vector3i jointID = new Vector3i(jointIDs[v * 3 + 0], jointIDs[v * 3 + 1], jointIDs[v * 3 + 2]);
			Vector3f weight = new Vector3f(weights[v * 3 + 0], weights[v * 3 + 1], weights[v * 3 + 2]);
			vertices.add(new Vertex(position, texCoord, normal, jointID, weight));
		}
		for (int i = 0; i < indexArray.length; i++) {
			indices.add(indexArray[i]);
		}
		return Assets.newGeometry(vertices, indices);
//		Vao vao = Vao.create();
//		vao.bind();
//		vao.createIndexBuffer(data.getIndices());
//		vao.createAttribute(0, data.getVertices(), 3);
//		vao.createAttribute(1, data.getTextureCoords(), 2);
//		vao.createAttribute(2, data.getNormals(), 3);
//		vao.createIntAttribute(3, data.getJointIds(), 3);
//		vao.createAttribute(4, data.getVertexWeights(), 3);
//		vao.unbind();
//		return vao;
	}

}
