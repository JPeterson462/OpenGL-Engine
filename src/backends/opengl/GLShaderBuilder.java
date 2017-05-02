package backends.opengl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import com.esotericsoftware.minlog.Log;

import engine.rendering.Shader;
import engine.rendering.VertexTemplate;

public class GLShaderBuilder {

	public static Shader createShader(InputStream fragment, InputStream vertex, VertexTemplate vertices, GLMemory memory) {
		int fragmentShader = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
		int vertexShader = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
		GL20.glShaderSource(fragmentShader, readSource(fragment));
		GL20.glCompileShader(fragmentShader);
		if (GL20.glGetShaderi(fragmentShader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			Log.error("Could not compile shader:\n" + GL20.glGetShaderInfoLog(fragmentShader), new IllegalStateException());
		}
		GL20.glShaderSource(vertexShader, readSource(vertex));
		GL20.glCompileShader(vertexShader);
		if (GL20.glGetShaderi(vertexShader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			Log.error("Could not compile shader:\n" + GL20.glGetShaderInfoLog(vertexShader), new IllegalStateException());
		}
		int programId = GL20.glCreateProgram();
		GL20.glAttachShader(programId, vertexShader);
		GL20.glAttachShader(programId, fragmentShader);
		switch (vertices) {
			case POSITION:
				GL20.glBindAttribLocation(programId, 0, "in_Position");
				break;
			case POSITION_TEXCOORD:
				GL20.glBindAttribLocation(programId, 0, "in_Position");
				GL20.glBindAttribLocation(programId, 1, "in_TexCoord");
				break;
			case POSITION_TEXCOORD_NORMAL:
				GL20.glBindAttribLocation(programId, 0, "in_Position");
				GL20.glBindAttribLocation(programId, 1, "in_TexCoord");
				GL20.glBindAttribLocation(programId, 2, "in_Normal");
				break;
			case POSITION_TEXCOORD_NORMAL_TANGENT:
				GL20.glBindAttribLocation(programId, 0, "in_Position");
				GL20.glBindAttribLocation(programId, 1, "in_TexCoord");
				GL20.glBindAttribLocation(programId, 2, "in_Normal");
				GL20.glBindAttribLocation(programId, 3, "in_Tangent");
				break;
			case POSITION_TEXCOORD_NORMAL_JOINTID_WEIGHT:
				GL20.glBindAttribLocation(programId, 0, "in_Position");
				GL20.glBindAttribLocation(programId, 1, "in_TexCoord");
				GL20.glBindAttribLocation(programId, 2, "in_Normal");
				GL20.glBindAttribLocation(programId, 3, "in_Joints");
				GL20.glBindAttribLocation(programId, 4, "in_Weights");
				break;
			case POSITION_TEXCOORD_COLOR:
				GL20.glBindAttribLocation(programId, 0, "in_Position");
				GL20.glBindAttribLocation(programId, 1, "in_TexCoord");
				GL20.glBindAttribLocation(programId, 2, "in_Color");
				break;
			default:
				break;
		}
		GL20.glLinkProgram(programId);
		GL20.glValidateProgram(programId);
		GLShader shader = new GLShader(programId);
		memory.shaderSet.add(programId);
		return new Shader(shader, new GLUniformSetter(programId));
	}

	public static Shader createInstancedShader(InputStream fragment, InputStream vertex, GLMemory memory, int[] attributes, String[] names) {
		int fragmentShader = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
		int vertexShader = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
		GL20.glShaderSource(fragmentShader, readSource(fragment));
		GL20.glCompileShader(fragmentShader);
		if (GL20.glGetShaderi(fragmentShader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			Log.error("Could not compile shader:\n" + GL20.glGetShaderInfoLog(fragmentShader), new IllegalStateException());
		}
		GL20.glShaderSource(vertexShader, readSource(vertex));
		GL20.glCompileShader(vertexShader);
		if (GL20.glGetShaderi(vertexShader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			Log.error("Could not compile shader:\n" + GL20.glGetShaderInfoLog(vertexShader), new IllegalStateException());
		}
		int programId = GL20.glCreateProgram();
		GL20.glAttachShader(programId, vertexShader);
		GL20.glAttachShader(programId, fragmentShader);
		for (int i = 0; i < attributes.length; i++) {
			GL20.glBindAttribLocation(programId, attributes[i], names[i]);
		}
		GL20.glLinkProgram(programId);
		GL20.glValidateProgram(programId);
		GLShader shader = new GLShader(programId);
		memory.shaderSet.add(programId);
		return new Shader(shader, new GLUniformSetter(programId));
	}

	private static StringBuilder readSource(InputStream stream) {
		StringBuilder fileSource = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
			reader.lines().forEach(line -> fileSource.append(line).append('\n'));
		} catch (IOException e) {
			Log.error("Exception occurred while loading file from input stream", e);
		}
		return fileSource;
	}
	
}
