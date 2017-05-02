package backends.opengl;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import com.esotericsoftware.minlog.Log;

public class VertexBufferObject {
	
	private int vboId;
	
	private Class<?> type;
	
	private int elements;
	
	private boolean uploaded = false;
	
	public VertexBufferObject(GLMemory memory) {
		vboId = GL15.glGenBuffers();
		memory.vboSet.add(vboId);
	}
	
	public void bind(int target) {
		GL15.glBindBuffer(target, vboId);
	}
	
	public void upload(int target, Buffer data, boolean isStatic, int elements) {
		this.elements = elements;
		if (data instanceof FloatBuffer) {
			if (!uploaded)
				GL15.glBufferData(target, (FloatBuffer) data, isStatic ? GL15.GL_STATIC_DRAW : GL15.GL_DYNAMIC_DRAW);
			else
				GL15.glBufferSubData(target, 0, (FloatBuffer) data);
			type = Float.class;
			uploaded = true;
		}
		else if (data instanceof ShortBuffer) {
			if (!uploaded)
				GL15.glBufferData(target, (ShortBuffer) data, isStatic ? GL15.GL_STATIC_DRAW : GL15.GL_DYNAMIC_DRAW);
			else
				GL15.glBufferSubData(target, 0, (ShortBuffer) data);
			type = Short.class;
			uploaded = true;
		}
		else if (data instanceof IntBuffer) {
			if (!uploaded)
				GL15.glBufferData(target, (IntBuffer) data, isStatic ? GL15.GL_STATIC_DRAW : GL15.GL_DYNAMIC_DRAW);
			else
				GL15.glBufferSubData(target, 0, (IntBuffer) data);
			type = Integer.class;
			uploaded = true;
		}
		else
			Log.warn("Unknown buffer type: " + data.getClass().getName());
	}
	
	public void attach(int attribute) {
		if (type.equals(Float.class))
			GL20.glVertexAttribPointer(attribute, elements, GL11.GL_FLOAT, false, elements << 2, 0);
		else if (type.equals(Short.class))
			GL20.glVertexAttribPointer(attribute, elements, GL11.GL_UNSIGNED_SHORT, false, elements << 1, 0);
		else if (type.equals(Integer.class))
			GL30.glVertexAttribIPointer(attribute, elements, GL11.GL_INT, elements << 2, 0);
		else
			Log.warn("Unknown buffer type: " + type.getName());
	}
	
	public void unbind(int target) {
		GL15.glBindBuffer(target, 0);
	}

	public int getID() {
		return vboId;
	}

}
