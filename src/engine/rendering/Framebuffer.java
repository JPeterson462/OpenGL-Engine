package engine.rendering;

import backends.opengl.GLFramebuffer;

public class Framebuffer {
	
	private Texture depthTexture, colorTextures[];
	
	private Renderbuffer depthBuffer;
	
	private Object backendData;
	
	public Framebuffer(Object backendData, Renderbuffer depthBuffer, Texture... colorTextures) {
		this.backendData = backendData;
		this.depthBuffer = depthBuffer;
		this.colorTextures = colorTextures;
	}
	
	public Framebuffer(Object backendData, Texture depthTexture, Texture... colorTextures) {
		this.backendData = backendData;
		this.depthTexture = depthTexture;
		this.colorTextures = colorTextures;
	}
	
	public Texture getDepthTexture() {
		return depthTexture;
	}
	
	public Renderbuffer getDepthBuffer() {
		return depthBuffer;
	}
	
	public Texture getColorTexture(int texture) {
		return colorTextures[texture];
	}
	
	public int getColorTextureCount() {
		return colorTextures.length;
	}
	
	public void bind() {
		if (backendData instanceof GLFramebuffer) {
			((GLFramebuffer) backendData).bind();
		}
	}
	
	public void unbind() {
		if (backendData instanceof GLFramebuffer) {
			((GLFramebuffer) backendData).unbind();
		}
	}

}
