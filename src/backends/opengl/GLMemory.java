package backends.opengl;

import java.util.HashSet;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class GLMemory {
	
	public HashSet<Integer> vaoSet = new HashSet<>();
	
	public HashSet<Integer> vboSet = new HashSet<>();
	
	public HashSet<Integer> shaderSet = new HashSet<>();
	
	public HashSet<Integer> textureSet = new HashSet<>();
	
	public HashSet<Integer> framebufferSet = new HashSet<>();
	
	public HashSet<Integer> renderbufferSet = new HashSet<>();
	
	public void destroy() {
		vaoSet.forEach(vao -> GL30.glDeleteVertexArrays(vao));
		vboSet.forEach(vbo -> GL15.glDeleteBuffers(vbo));
		shaderSet.forEach(shader -> GL20.glDeleteProgram(shader));
		textureSet.forEach(texture -> GL11.glDeleteTextures(texture));
		framebufferSet.forEach(fbo -> GL30.glDeleteFramebuffers(fbo));
		renderbufferSet.forEach(rbo -> GL30.glDeleteRenderbuffers(rbo));
		vaoSet.clear();
		vboSet.clear();
		shaderSet.clear();
		textureSet.clear();
		framebufferSet.clear();
		renderbufferSet.clear();
	}

}
