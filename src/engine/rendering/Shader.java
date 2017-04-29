package engine.rendering;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import backends.opengl.GLShader;

public class Shader implements UniformSetter {
	
	private Object backendData;
	
	private UniformSetter setter;
	
	private static Shader currentlyBound = null;
	
	public Shader(Object backendData, UniformSetter setter) {
		this.backendData = backendData;
		this.setter = setter;
	}
	
	public Object getBackendData() {
		return backendData;
	}
	
	public void bind() {
		if (currentlyBound == this)
			return;
		currentlyBound = this;
		if (backendData instanceof GLShader)
			((GLShader) backendData).bind();
	}
	
	public void unbind() {
		currentlyBound = null;
		if (backendData instanceof GLShader)
			((GLShader) backendData).unbind();
	}

	@Override
	public void uploadMatrix(String uniform, Matrix4f matrix) {
		setter.uploadMatrix(uniform, matrix);
	}

	@Override
	public void uploadVector(String uniform, Vector3f vector) {
		setter.uploadVector(uniform, vector);
	}

	@Override
	public void uploadFloat(String uniform, float f) {
		setter.uploadFloat(uniform, f);
	}

	@Override
	public void uploadInt(String uniform, int i) {
		setter.uploadInt(uniform, i);
	}

	@Override
	public void uploadVector(String uniform, Vector2f vector) {
		setter.uploadVector(uniform, vector);
	}

	@Override
	public void uploadVector(String uniform, Vector4f vector) {
		setter.uploadVector(uniform, vector);
	}
	
}
