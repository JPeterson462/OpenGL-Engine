package engine;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.lwjgl.stb.STBImage;

import utils.IOUtils;

public class RawImage {
	
	private int[] pixels;
	
	private int width, height;
	
	public RawImage(InputStream stream) throws IOException {
		int[] width = new int[1], height = new int[1], components = new int[1];
		ByteBuffer bytes = STBImage.stbi_load_from_memory(IOUtils.readToBuffer(stream), width, height, components, 4);
		int pointer = 0;
		pixels = new int[width[0] * height[0]];
		for (int i = 0; i < bytes.capacity(); i += 4) {
			int red = bytes.get(i) & 0xFF; 
			int green = bytes.get(i + 1) & 0xFF; 
			int blue = bytes.get(i + 2) & 0xFF;
			pixels[pointer] = (red << 16) | (green << 8) | blue;
			pointer++;
		}
		this.width = width[0];
		this.height = height[0];
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	private int index(int x, int y) {
		return y * width + x;
	}
	
	public int getRGB(int x, int y) {
		return pixels[index(x, y)];
	}
	
}
