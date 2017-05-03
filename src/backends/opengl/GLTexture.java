package backends.opengl;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public class GLTexture {
	
	private int textureId, target;

	public GLTexture(int textureId, int target, GLMemory memory) {
		this.textureId = textureId;
		this.target = target;
		memory.textureSet.add(textureId);
	}
	
	public void bind(int unit) {
		GL13.glActiveTexture(GL13.GL_TEXTURE0 + unit);
		GL11.glBindTexture(target, textureId);
	}
	
	public void unbind() {
		GL11.glBindTexture(target, 0);
	}
	
	public boolean equals(Object o) {
		return o instanceof GLTexture && ((GLTexture) o).textureId == textureId;
	}

}
