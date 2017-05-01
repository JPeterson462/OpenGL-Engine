package backends.opengl;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class GLFramebuffer {
	
	private int fboId;
	
	private int width, height, displayWidth, displayHeight;
	
	private int[] buffers;
	
	public GLFramebuffer(GLMemory memory, int colorBuffers, int width, int height, int displayWidth, int displayHeight) {
		fboId = GL30.glGenFramebuffers();
		memory.framebufferSet.add(fboId);
		buffers = new int[colorBuffers];
		for (int i = 0; i < colorBuffers; i++) {
			buffers[i] = GL30.GL_COLOR_ATTACHMENT0 + i;
		}
		this.width = width;
		this.height = height;
		this.displayWidth = displayWidth;
		this.displayHeight = displayHeight;
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fboId);
		if (buffers.length > 0) {
			GL20.glDrawBuffers(buffers);
		} else {
			System.out.println("No color buffer");
			GL11.glDrawBuffer(GL11.GL_NONE);
			GL11.glReadBuffer(GL11.GL_NONE);
		}
	}
	
	public void bind() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fboId);
		GL11.glViewport(0, 0, width, height);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		if (buffers.length > 0) {
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);			
		} else {
			GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		}
	}
	
	public void unbind() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		GL11.glViewport(0, 0, displayWidth, displayHeight);
	}
	
	public void checkStatus() {
		if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
			throw new IllegalStateException("Invalid Framebuffer");
		}
	}

}
