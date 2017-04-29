package backends.opengl;

import java.nio.ByteBuffer;

import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryUtil;

import engine.Settings;
import engine.input.Modifiers;
import utils.IOUtils;

public class GLFWUtils {

	public static Modifiers getModifiers(int glfw) {
		return new Modifiers((glfw & GLFW.GLFW_MOD_SHIFT) != 0, 
				(glfw & GLFW.GLFW_MOD_CONTROL) != 0, (glfw & GLFW.GLFW_MOD_ALT) != 0);
	}
	
	public static GLFWImage.Buffer loadIcons(Settings settings) {
		String[] windowIconPaths = settings.windowIcons;
		GLFWImage.Buffer windowIcons = GLFWImage.calloc(windowIconPaths.length);
		int[] width = new int[1], height = new int[1], channels = new int[1];
		for (int i = 0; i < windowIconPaths.length; i++) {
			ByteBuffer pixels = STBImage.stbi_load_from_memory(IOUtils.readToBuffer(GLFWUtils.class.getClassLoader().getResourceAsStream("textures/" + windowIconPaths[i])), width, height, channels, 0);
			windowIcons.put(i, GLFWImage.malloc().set(width[0], height[0], pixels));
		}
		return windowIcons;
	}
	
	public static String[] getPaths(long address, int count) {
		PointerBuffer nameBuffer = MemoryUtil.memPointerBuffer(address, count);
		String[] paths = new String[count];
		for (int i = 0; i < count; i++) {
			paths[i] = MemoryUtil.memUTF8(MemoryUtil.memByteBufferNT1(nameBuffer.get(i)));
		}
		return paths;
	}

}
