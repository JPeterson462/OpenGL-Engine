package backends.opengl;

import org.lwjgl.opengl.GL30;

public class GLRenderbuffer {
	
	private int renderbuffer;

	public GLRenderbuffer(int renderbuffer, GLMemory memory) {
		this.renderbuffer = renderbuffer;
		memory.renderbufferSet.add(renderbuffer);
	}

	public void bind() {
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, renderbuffer);
	}
	
	public void unbind() {
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, 0);
	}
	
}
