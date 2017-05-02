package backends.opengl;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import com.esotericsoftware.minlog.Log;

import engine.rendering.Geometry;
import engine.rendering.Vertex;
import engine.rendering.VertexTemplate;

public class GLGeometryBuilder {

	@FunctionalInterface
	public interface CheckError {
		public void checkError();
	}
	
	public static Geometry createGeometry(ArrayList<Vertex> vertices, ArrayList<Integer> indexList, GLMemory memory, CheckError c) {
		c.checkError();
		final int drawCall;
		if (indexList.size() % 3 == 0)
			drawCall = GL11.GL_TRIANGLES;
		else if (indexList.size() % 4 == 0)
			drawCall = GL11.GL_QUADS;
		else
			drawCall = 0;
		VertexTemplate template = vertices.get(0).getTemplate();
		switch (template) {
			case POSITION: {
				VertexBufferObject positionData = new VertexBufferObject(memory);
				VertexBufferObject indexData = new VertexBufferObject(memory);
				FloatBuffer positions = BufferUtils.createFloatBuffer(vertices.size() * 3);
				IntBuffer indices = BufferUtils.createIntBuffer(indexList.size());
				positions.limit(positions.capacity());
				indices.limit(indices.capacity());
				for (int i = 0; i < vertices.size(); i++) {
					Vertex vertex = vertices.get(i);
					positions.put(i * 3 + 0, vertex.getPosition().x);
					positions.put(i * 3 + 1, vertex.getPosition().y);
					positions.put(i * 3 + 2, vertex.getPosition().z);
				}
				for (int i = 0; i < indexList.size(); i++) {
					indices.put(i, indexList.get(i));
				}
				VertexArrayObject vao = new VertexArrayObject(1, memory, 31 * vertices.hashCode() + indexList.hashCode());
				vao.bind();
				vao.attach(0, positionData, positions, 3);
				vao.unbind();
				vao.attach(indexData, indices);
				c.checkError();
				return new Geometry(vao) {
					public void renderGeometry() {
						GL11.glDrawElements(drawCall, getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
					}
				};
			}
			case POSITION_TEXCOORD: {
				VertexBufferObject positionData = new VertexBufferObject(memory);
				VertexBufferObject indexData = new VertexBufferObject(memory);
				VertexBufferObject textureData = new VertexBufferObject(memory);
				FloatBuffer positions = BufferUtils.createFloatBuffer(vertices.size() * 3);
				IntBuffer indices = BufferUtils.createIntBuffer(indexList.size());
				FloatBuffer texCoords = BufferUtils.createFloatBuffer(vertices.size() * 2);
				positions.limit(positions.capacity());
				indices.limit(indices.capacity());
				texCoords.limit(texCoords.capacity());
				for (int i = 0; i < vertices.size(); i++) {
					Vertex vertex = vertices.get(i);
					positions.put(i * 3 + 0, vertex.getPosition().x);
					positions.put(i * 3 + 1, vertex.getPosition().y);
					positions.put(i * 3 + 2, vertex.getPosition().z);
					texCoords.put(i * 2 + 0, vertex.getTextureCoord().x);
					texCoords.put(i * 2 + 1, vertex.getTextureCoord().y);
				}
				for (int i = 0; i < indexList.size(); i++) {
					indices.put(i, indexList.get(i));
				}
				VertexArrayObject vao = new VertexArrayObject(2, memory, 31 * vertices.hashCode() + indexList.hashCode());
				vao.bind();
				vao.attach(0, positionData, positions, 3);
				vao.attach(1, textureData, texCoords, 2);
				vao.unbind();
				vao.attach(indexData, indices);
				c.checkError();
				return new Geometry(vao) {
					public void renderGeometry() {
						GL11.glDrawElements(drawCall, getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
					}
				}; 
			}
			case POSITION_TEXCOORD_NORMAL: {
				VertexBufferObject positionData = new VertexBufferObject(memory);
				VertexBufferObject indexData = new VertexBufferObject(memory);
				VertexBufferObject textureData = new VertexBufferObject(memory);
				VertexBufferObject normalData = new VertexBufferObject(memory);
				FloatBuffer positions = BufferUtils.createFloatBuffer(vertices.size() * 3);
				IntBuffer indices = BufferUtils.createIntBuffer(indexList.size());
				FloatBuffer texCoords = BufferUtils.createFloatBuffer(vertices.size() * 2);
				FloatBuffer normals = BufferUtils.createFloatBuffer(vertices.size() * 3);
				positions.limit(positions.capacity());
				indices.limit(indices.capacity());
				texCoords.limit(texCoords.capacity());
				normals.limit(normals.capacity());
				for (int i = 0; i < vertices.size(); i++) {
					Vertex vertex = vertices.get(i);
					positions.put(i * 3 + 0, vertex.getPosition().x);
					positions.put(i * 3 + 1, vertex.getPosition().y);
					positions.put(i * 3 + 2, vertex.getPosition().z);
					texCoords.put(i * 2 + 0, vertex.getTextureCoord().x);
					texCoords.put(i * 2 + 1, vertex.getTextureCoord().y);
					normals.put(i * 3 + 0, vertex.getNormal().x);
					normals.put(i * 3 + 1, vertex.getNormal().y);
					normals.put(i * 3 + 2, vertex.getNormal().z);
				}
				for (int i = 0; i < indexList.size(); i++) {
					indices.put(i, indexList.get(i));
				}
				VertexArrayObject vao = new VertexArrayObject(3, memory, 31 * vertices.hashCode() + indexList.hashCode());
				vao.bind();
				vao.attach(0, positionData, positions, 3);
				vao.attach(1, textureData, texCoords, 2);
				vao.attach(2, normalData, normals, 3);
				vao.unbind();
				vao.attach(indexData, indices);
				c.checkError();
				return new Geometry(vao) {
					public void renderGeometry() {
						GL11.glDrawElements(drawCall, getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
					}
				}; 
			}
			case POSITION_TEXCOORD_COLOR: {
				VertexBufferObject positionData = new VertexBufferObject(memory);
				VertexBufferObject indexData = new VertexBufferObject(memory);
				VertexBufferObject textureData = new VertexBufferObject(memory);
				VertexBufferObject colorData = new VertexBufferObject(memory);
				FloatBuffer positions = BufferUtils.createFloatBuffer(vertices.size() * 2);
				IntBuffer indices = BufferUtils.createIntBuffer(indexList.size());
				FloatBuffer texCoords = BufferUtils.createFloatBuffer(vertices.size() * 2);
				FloatBuffer colors = BufferUtils.createFloatBuffer(vertices.size() * 4);
				positions.limit(positions.capacity());
				indices.limit(indices.capacity());
				texCoords.limit(texCoords.capacity());
				colors.limit(colors.capacity());
				for (int i = 0; i < vertices.size(); i++) {
					Vertex vertex = vertices.get(i);
					positions.put(i * 2 + 0, vertex.getPosition().x);
					positions.put(i * 2 + 1, vertex.getPosition().y);
					texCoords.put(i * 2 + 0, vertex.getTextureCoord().x);
					texCoords.put(i * 2 + 1, vertex.getTextureCoord().y);
					colors.put(i * 4 + 0, vertex.getColor().x);
					colors.put(i * 4 + 1, vertex.getColor().y);
					colors.put(i * 4 + 2, vertex.getColor().z);
					colors.put(i * 4 + 3, vertex.getColor().w);
				}
				for (int i = 0; i < indexList.size(); i++) {
					indices.put(i, indexList.get(i));
				}
				VertexArrayObject vao = new VertexArrayObject(3, memory, 31 * vertices.hashCode() + indexList.hashCode());
				vao.bind();
				vao.attach(0, positionData, positions, 2);
				vao.attach(1, textureData, texCoords, 2);
				vao.attach(2, colorData, colors, 4);
				vao.unbind();
				vao.attach(indexData, indices);
				c.checkError();
				return new Geometry(vao) {
					public void renderGeometry() {
						GL11.glDrawElements(drawCall, getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
					}
				}; 
			}
			case POSITION_TEXCOORD_NORMAL_TANGENT: {
				VertexBufferObject positionData = new VertexBufferObject(memory);
				VertexBufferObject indexData = new VertexBufferObject(memory);
				VertexBufferObject textureData = new VertexBufferObject(memory);
				VertexBufferObject normalData = new VertexBufferObject(memory);
				VertexBufferObject tangentData = new VertexBufferObject(memory);
				FloatBuffer positions = BufferUtils.createFloatBuffer(vertices.size() * 3);
				IntBuffer indices = BufferUtils.createIntBuffer(indexList.size());
				FloatBuffer texCoords = BufferUtils.createFloatBuffer(vertices.size() * 2);
				FloatBuffer normals = BufferUtils.createFloatBuffer(vertices.size() * 3);
				FloatBuffer tangents = BufferUtils.createFloatBuffer(vertices.size() * 3);
				positions.limit(positions.capacity());
				indices.limit(indices.capacity());
				texCoords.limit(texCoords.capacity());
				normals.limit(normals.capacity());
				tangents.limit(tangents.capacity());
				for (int i = 0; i < vertices.size(); i++) {
					Vertex vertex = vertices.get(i);
					positions.put(i * 3 + 0, vertex.getPosition().x);
					positions.put(i * 3 + 1, vertex.getPosition().y);
					positions.put(i * 3 + 2, vertex.getPosition().z);
					texCoords.put(i * 2 + 0, vertex.getTextureCoord().x);
					texCoords.put(i * 2 + 1, vertex.getTextureCoord().y);
					normals.put(i * 3 + 0, vertex.getNormal().x);
					normals.put(i * 3 + 1, vertex.getNormal().y);
					normals.put(i * 3 + 2, vertex.getNormal().z);
					tangents.put(i * 3 + 0, vertex.getTangent().x);
					tangents.put(i * 3 + 1, vertex.getTangent().y);
					tangents.put(i * 3 + 2, vertex.getTangent().z);
				}
				for (int i = 0; i < indexList.size(); i++) {
					indices.put(i, indexList.get(i));
				}
				VertexArrayObject vao = new VertexArrayObject(4, memory, 31 * vertices.hashCode() + indexList.hashCode());
				vao.bind();
				vao.attach(0, positionData, positions, 3);
				vao.attach(1, textureData, texCoords, 2);
				vao.attach(2, normalData, normals, 3);
				vao.attach(3, tangentData, tangents, 3);
				vao.unbind();
				vao.attach(indexData, indices);
				c.checkError();
				return new Geometry(vao) {
					public void renderGeometry() {
						GL11.glDrawElements(drawCall, getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
					}
				}; 
			}
			case POSITION_TEXCOORD_NORMAL_JOINTID_WEIGHT: {
				VertexBufferObject positionData = new VertexBufferObject(memory);
				VertexBufferObject indexData = new VertexBufferObject(memory);
				VertexBufferObject textureData = new VertexBufferObject(memory);
				VertexBufferObject normalData = new VertexBufferObject(memory);
				VertexBufferObject jointData = new VertexBufferObject(memory);
				VertexBufferObject weightData = new VertexBufferObject(memory);
				FloatBuffer positions = BufferUtils.createFloatBuffer(vertices.size() * 3);
				IntBuffer indices = BufferUtils.createIntBuffer(indexList.size());
				FloatBuffer texCoords = BufferUtils.createFloatBuffer(vertices.size() * 2);
				FloatBuffer normals = BufferUtils.createFloatBuffer(vertices.size() * 3);
				IntBuffer joints = BufferUtils.createIntBuffer(vertices.size() * 3);
				FloatBuffer weights = BufferUtils.createFloatBuffer(vertices.size() * 3);
				positions.limit(positions.capacity());
				indices.limit(indices.capacity());
				texCoords.limit(texCoords.capacity());
				normals.limit(normals.capacity());
				joints.limit(joints.capacity());
				weights.limit(weights.capacity());
				for (int i = 0; i < vertices.size(); i++) {
					Vertex vertex = vertices.get(i);
					positions.put(i * 3 + 0, vertex.getPosition().x);
					positions.put(i * 3 + 1, vertex.getPosition().y);
					positions.put(i * 3 + 2, vertex.getPosition().z);
					texCoords.put(i * 2 + 0, vertex.getTextureCoord().x);
					texCoords.put(i * 2 + 1, vertex.getTextureCoord().y);
					normals.put(i * 3 + 0, vertex.getNormal().x);
					normals.put(i * 3 + 1, vertex.getNormal().y);
					normals.put(i * 3 + 2, vertex.getNormal().z);
					joints.put(i * 3 + 0, vertex.getJointIDs().x);
					joints.put(i * 3 + 1, vertex.getJointIDs().y);
					joints.put(i * 3 + 2, vertex.getJointIDs().z);
					weights.put(i * 3 + 0, vertex.getWeights().x);
					weights.put(i * 3 + 1, vertex.getWeights().y);
					weights.put(i * 3 + 2, vertex.getWeights().z);
				}
				for (int i = 0; i < indexList.size(); i++) {
					indices.put(i, indexList.get(i));
				}
				VertexArrayObject vao = new VertexArrayObject(5, memory, 31 * vertices.hashCode() + indexList.hashCode());
				vao.bind();
				vao.attach(0, positionData, positions, 3);
				vao.attach(1, textureData, texCoords, 2);
				vao.attach(2, normalData, normals, 3);
				vao.attach(3, jointData, joints, 3);
				vao.attach(4, weightData, weights, 3);
				vao.unbind();
				vao.attach(indexData, indices);
				c.checkError();
				return new Geometry(vao) {
					public void renderGeometry() {
						GL11.glDrawElements(drawCall, getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
					}
				}; 
			}
			default:
				Log.warn("Cannot handle vertices of type " + template.name());
				break;
		}
		return null;
	}
	
}
