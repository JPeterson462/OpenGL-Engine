package backends.opengl;

import org.lwjgl.opengl.GL20;

public class GLShader {

	private int programId;
	
	public GLShader(int programId) {
		this.programId = programId;
	}
	
	public void bind() {
		GL20.glUseProgram(programId);
	}
	
	public void unbind() {
		GL20.glUseProgram(0);
	}
	
}
