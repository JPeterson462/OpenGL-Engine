package backends.opengl;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;

public class InstancedVertexArrayObject {
	
	private int vaoId;
	
	private VertexBufferObject positions, instances;
	
	private VertexBufferObject indices;
	
	public InstancedVertexArrayObject(GLMemory memory, VertexBufferObject positions, FloatBuffer positionData, int dimensions, VertexBufferObject instances, 
			int instancedDataLength, VertexBufferObject indexData, IntBuffer indices) {
		this.positions = positions;
		this.instances = instances;
		this.indices = indexData;
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indexData.getID());
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indices, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		vaoId = GL30.glGenVertexArrays();
		memory.vaoSet.add(vaoId);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, positions.getID());
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, positionData, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, instances.getID());
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, instancedDataLength * 4, GL15.GL_STREAM_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(vaoId);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, positions.getID());
		GL20.glVertexAttribPointer(0, dimensions, GL11.GL_FLOAT, false, dimensions * 4, 0);
		addInstancedAttribute(1, 4, instancedDataLength, 0);
		addInstancedAttribute(2, 4, instancedDataLength, 4);
		addInstancedAttribute(3, 4, instancedDataLength, 8);
		addInstancedAttribute(4, 4, instancedDataLength, 12);
		addInstancedAttribute(5, 4, instancedDataLength, 16);
		addInstancedAttribute(6, 1, instancedDataLength, 20);
	}
	
	private void addInstancedAttribute(int attribute, int dataSize, int instancedDataLength, int offset) {
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, instances.getID());
		GL30.glBindVertexArray(vaoId);
		GL20.glVertexAttribPointer(attribute, dataSize, GL11.GL_FLOAT, false, instancedDataLength * 4, offset * 4);
		GL33.glVertexAttribDivisor(attribute, 1);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, instances.getID());
		GL30.glBindVertexArray(0);
	}
	
	public void bind() {
		GL30.glBindVertexArray(vaoId);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indices.getID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		GL20.glEnableVertexAttribArray(3);
		GL20.glEnableVertexAttribArray(4);
		GL20.glEnableVertexAttribArray(5);
		GL20.glEnableVertexAttribArray(6);
	}
	
	public void unbind() {
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL20.glDisableVertexAttribArray(3);
		GL20.glDisableVertexAttribArray(4);
		GL20.glDisableVertexAttribArray(5);
		GL20.glDisableVertexAttribArray(6);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
	}

	public VertexBufferObject getPositions() {
		return positions;
	}

	public VertexBufferObject getInstances() {
		return instances;
	}

}
