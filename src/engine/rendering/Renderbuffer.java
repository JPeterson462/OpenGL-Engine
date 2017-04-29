package engine.rendering;

import backends.opengl.GLRenderbuffer;

public class Renderbuffer {
	
	private Object backendData;
	
	public Renderbuffer(Object backendData) {
		this.backendData = backendData;
	}
	
	public void bind() {
		if (backendData instanceof GLRenderbuffer) {
			((GLRenderbuffer) backendData).bind();
		}
	}
	
	public void unbind() {
		if (backendData instanceof GLRenderbuffer) {
			((GLRenderbuffer) backendData).unbind();
		}
	}

}
