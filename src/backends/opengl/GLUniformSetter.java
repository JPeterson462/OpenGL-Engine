package backends.opengl;

import java.nio.FloatBuffer;
import java.util.HashMap;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

import engine.rendering.UniformSetter;

public class GLUniformSetter implements UniformSetter {

	private int programId;
	
	private FloatBuffer buffer;
	
	private HashMap<String, Integer> uniforms = new HashMap<>();
	
	public GLUniformSetter(int programId) {
		this.programId = programId;
		buffer = BufferUtils.createFloatBuffer(16);
	}
	
	private int locate(String uniform) {
		if (uniforms.containsKey(uniform)) {
			return uniforms.get(uniform);
		}
		int location = GL20.glGetUniformLocation(programId, uniform);
		uniforms.put(uniform, location);
		return location;
	}

	@Override
	public void uploadMatrix(String uniform, Matrix4f matrix) {
		matrix.get(buffer);
		GL20.glUniformMatrix4fv(locate(uniform), false, buffer);
	}

	@Override
	public void uploadVector(String uniform, Vector3f vector) {
		GL20.glUniform3f(locate(uniform), vector.x, vector.y, vector.z);
	}

	@Override
	public void uploadFloat(String uniform, float f) {
		GL20.glUniform1f(locate(uniform), f);
	}

	@Override
	public void uploadInt(String uniform, int i) {
		GL20.glUniform1i(locate(uniform), i);
	}

	@Override
	public void uploadVector(String uniform, Vector2f vector) {
		GL20.glUniform2f(locate(uniform), vector.x, vector.y);
	}

	@Override
	public void uploadVector(String uniform, Vector4f vector) {
		GL20.glUniform4f(locate(uniform), vector.x, vector.y, vector.z, vector.w);
	}

}
