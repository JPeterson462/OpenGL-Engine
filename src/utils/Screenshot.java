package utils;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBIWriteCallbackI;
import org.lwjgl.stb.STBImageWrite;
import org.lwjgl.system.MemoryUtil;

import com.esotericsoftware.minlog.Log;

public class Screenshot {
	
	public static final int FORMAT_PNG = 0, FORMAT_BMP = 1, FORMAT_TGA = 2, FORMAT_COUNT = 3;
	
	public static void takeScreenshot(int x, int y, int width, int height, OutputStream stream, int format) {
		if (format >= FORMAT_COUNT || format < 0) {
			Log.error("Cannot write screenshots to format " + format, new IllegalArgumentException());
			format = FORMAT_PNG;
		}
		GL11.glReadBuffer(GL11.GL_FRONT);
		int bpp = 4;
		ByteBuffer glBuffer = BufferUtils.createByteBuffer(width * height * bpp);
		GL11.glReadPixels(x, y, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, glBuffer);
		ScreenshotWriterThread thread = new ScreenshotWriterThread(glBuffer, width, height, stream, format);
		thread.start();
	}
	
	public static class ScreenshotWriterThread extends Thread {
		
		private ByteBuffer glBuffer;
		
		private int width, height;
		
		private OutputStream stream;
		
		private STBIWriteCallbackI function = (context, data, size) -> {
			ByteBuffer pngData = MemoryUtil.memByteBuffer(data, size);
			IOUtils.copy(new ByteBufferInputStream(pngData), stream);
			try {
				stream.flush();
				stream.close();
			} catch (IOException e) {
				Log.error("Encountered error while writing screenshot", e);
			}
		};
		
		private int format;
		
		private final int bpp = 4;
		
		public ScreenshotWriterThread(ByteBuffer glBuffer, int width, int height, OutputStream stream, int format) {
			this.glBuffer = glBuffer;
			this.width = width;
			this.height = height;
			this.stream = stream;
			this.format = format;
		}
		
		public void run() {
			ByteBuffer flippedBuffer = BufferUtils.createByteBuffer(glBuffer.capacity());
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					int i = (x + (width * y)) * bpp;
			        byte r = glBuffer.get(i + 0);
			        byte g = glBuffer.get(i + 1);
			        byte b = glBuffer.get(i + 2);
			        byte a = glBuffer.get(i + 3);
			        int i1 = (x + (width * (height - y - 1))) * bpp;
			        flippedBuffer.put(i1 + 0, r);
			        flippedBuffer.put(i1 + 1, g);
			        flippedBuffer.put(i1 + 2, b);
			        flippedBuffer.put(i1 + 3, a);
				}
			}
			switch (format) {
				case FORMAT_PNG:
					STBImageWrite.stbi_write_png_to_func(function, System.currentTimeMillis(), width, height, bpp, flippedBuffer, width * bpp);
					break;
				case FORMAT_BMP:
					STBImageWrite.stbi_write_bmp_to_func(function, System.currentTimeMillis(), width, height, bpp, flippedBuffer);
					break;
				case FORMAT_TGA:
					STBImageWrite.stbi_write_tga_to_func(function, System.currentTimeMillis(), width, height, bpp, flippedBuffer);
					break;
			}
		}
				
	}
	
}
