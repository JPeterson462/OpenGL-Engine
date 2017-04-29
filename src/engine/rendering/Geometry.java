package engine.rendering;

import backends.opengl.VertexArrayObject;

public class Geometry {
	
	private Object backendData;
	
	public Geometry(Object backendData) {
		this.backendData = backendData;
	}
	
	public Object getBackendData() {
		return backendData;
	}
	
	public void bind() {
		if (backendData instanceof VertexArrayObject)
			((VertexArrayObject) backendData).bind();
	}
	
	public int getVertexCount() {
		if (backendData instanceof VertexArrayObject)
			return ((VertexArrayObject) backendData).getSize();
		return -1;
	}
	
	public void unbind() {
		if (backendData instanceof VertexArrayObject)
			((VertexArrayObject) backendData).unbind();
	}
	
	public int hashCode() {
		return backendData.hashCode();
	}
	
	public void renderGeometry() {
		
	}

}
