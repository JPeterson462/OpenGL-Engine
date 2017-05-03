package backends.opengl;

import java.nio.Buffer;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class VertexArrayObject {
	
	private int vaoId;
	
	private VertexBufferObject[] vbos;
	
	private VertexBufferObject indexBuffer;
	
	private int size;
	
	private int hashCode;
	
	public VertexArrayObject(int buffers, GLMemory memory, int hashCode) {
		vaoId = GL30.glGenVertexArrays();
		memory.vaoSet.add(vaoId);
		vbos = new VertexBufferObject[buffers];
		this.hashCode = hashCode;
	}
	
	public void attach(int position, VertexBufferObject object, Buffer data, int elements, boolean isStatic) {
		vbos[position] = object;
		object.bind();
		object.upload(data, isStatic, elements);
		object.attach(position);
		object.unbind();
	}
	
	public void attach(VertexBufferObject object, Buffer data, boolean isStatic) {
		indexBuffer = object;
		object.bind();
		object.upload(data, isStatic, 0);
		object.unbind();
		size = data.limit();
	}
	
	public int getSize() {
		return size;
	}
	
	public void bind() {
		GL30.glBindVertexArray(vaoId);
		for (int i = 0; i < vbos.length; i++)
			GL20.glEnableVertexAttribArray(i);
		if (indexBuffer != null)
			indexBuffer.bind();
	}
	
	public void bind(int attributes) {
		GL30.glBindVertexArray(vaoId);
		for (int i = 0; i < attributes; i++)
			GL20.glEnableVertexAttribArray(i);
		if (indexBuffer != null)
			indexBuffer.bind();
	}
	
	public void unbind() {
		if (indexBuffer != null)
			indexBuffer.unbind();
		for (int i = 0; i < vbos.length; i++)
			GL20.glDisableVertexAttribArray(i);
		GL30.glBindVertexArray(0);
	}

	public void unbind(int attributes) {
		if (indexBuffer != null)
			indexBuffer.unbind();
		for (int i = 0; i < attributes; i++)
			GL20.glDisableVertexAttribArray(i);
		GL30.glBindVertexArray(0);
	}
	
	public VertexBufferObject getIndexBuffer() {
		return indexBuffer;
	}

	public VertexBufferObject[] getVBOs() {
		return vbos;
	}
	
	public int hashCode() {
		return hashCode;
	}
	
}
