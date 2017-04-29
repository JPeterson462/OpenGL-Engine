package engine.rendering;

import java.nio.FloatBuffer;

import backends.opengl.InstancedVertexArrayObject;

public class InstancedGeometry {

	private Object backendData;
	
	public InstancedGeometry(Object backendData) {
		this.backendData = backendData;
	}
	
	public Object getBackendData() {
		return backendData;
	}
	
	public void bind() {
		if (backendData instanceof InstancedVertexArrayObject)
			((InstancedVertexArrayObject) backendData).bind();
	}
	
	public void unbind() {
		if (backendData instanceof InstancedVertexArrayObject)
			((InstancedVertexArrayObject) backendData).unbind();
	}
	
	public int hashCode() {
		return backendData.hashCode();
	}
	
	public void updateInstances(FloatBuffer instanceData) {
		
	}
	
	public void renderGeometry(int count) {
		
	}

}
