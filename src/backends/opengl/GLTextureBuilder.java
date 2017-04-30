package backends.opengl;

import java.io.InputStream;
import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;

import com.esotericsoftware.minlog.Log;

import engine.rendering.Texture;
import utils.IOUtils;

public class GLTextureBuilder {
	
	public static int createEmptyTexture(int width, int height) {
		int texture = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, width, height, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
		return texture;
	}
	
	public static int createEmptyRenderbuffer(int width, int height) {
		int renderbuffer = GL30.glGenRenderbuffers();
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, renderbuffer);
		GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL11.GL_DEPTH_COMPONENT, width, height);
		return renderbuffer;
	}
	
	public static int createEmptyDepthTexture(int width, int height) {
		int texture = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL14.GL_DEPTH_COMPONENT32, width, height, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (ByteBuffer) null);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		return texture;
	}
	
	public static Texture createTexture(InputStream stream, boolean mipmapAlways, boolean clampEdges, GLMemory memory) {
		int[] width = new int[1], height = new int[1], channels = new int[1];
		ByteBuffer pixels = STBImage.stbi_load_from_memory(IOUtils.readToBuffer(stream), width, height, channels, 0);
		int textureId = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
		if (clampEdges) {
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		} else {
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		}
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, mipmapAlways ? 0 : -0.4f);
		int format = 0, type = 0;
		switch (channels[0]) {
			case 4: 
				format = GL11.GL_RGBA; 
				type = Texture.TEXTURE_RGBA;
				break;
			case 3: 
				format = GL11.GL_RGB;
				type = Texture.TEXTURE_RGB;
				break;
			case 1: 
				format = GL11.GL_ALPHA;
				type = Texture.TEXTURE_ALPHA;
				break;
			default: 
				Log.error("Error while loading texture", new IllegalStateException("Invalid texture format"));
				break;
		}
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, format, width[0], height[0], 0, format, GL11.GL_UNSIGNED_BYTE, pixels);
		GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GLTexture texture = new GLTexture(textureId, GL11.GL_TEXTURE_2D, memory);
		return new Texture(texture, width[0], height[0], type);
	}
	
	public static Texture createCubemap(InputStream[] stream, GLMemory memory) {
		if (stream.length != 6)
			Log.error("Cubemaps must contain 6 textures");
		int[] width = new int[1], height = new int[1], channels = new int[1];
		int textureId = GL11.glGenTextures();
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, textureId);
		int type = 0;
		for (int i = 0; i < 6; i++) {
			ByteBuffer pixels = STBImage.stbi_load_from_memory(IOUtils.readToBuffer(stream[i]), width, height, channels, 0);
			int format = 0;
			switch (channels[0]) {
				case 4: 
					format = GL11.GL_RGBA; 
					type = Texture.TEXTURE_RGBA;
					break;
				case 3: 
					format = GL11.GL_RGB;
					type = Texture.TEXTURE_RGB;
					break;
				case 1: 
					format = GL11.GL_ALPHA;
					type = Texture.TEXTURE_ALPHA;
					break;
				default: 
					Log.error("Error while loading texture", new IllegalStateException("Invalid texture format"));
					break;
			}
			GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, format, width[0], height[0], 0, format, GL11.GL_UNSIGNED_BYTE, pixels);
		}
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, 0);
		GLTexture texture = new GLTexture(textureId, GL13.GL_TEXTURE_CUBE_MAP, memory);
		return new Texture(texture, width[0], height[0], type);
	}

}
