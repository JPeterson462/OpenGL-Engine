package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.BufferUtils;

import com.esotericsoftware.minlog.Log;

public class IOUtils {
	
	public static ByteBuffer readToBuffer(InputStream stream) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		copy(stream, bytes);
		ByteBuffer buffer = BufferUtils.createByteBuffer(bytes.size());
		buffer.put(bytes.toByteArray());
		buffer.flip();
		return buffer;
	}
	
	public static void copy(InputStream in, OutputStream out) {
		copy(in, out, 1024);
	}
	
	public static void copy(InputStream in, OutputStream out, int bufferSize) {
		byte[] chunk = new byte[bufferSize];
		int read;
		try {
			while ((read = in.read(chunk)) > 0) {
				out.write(chunk, 0, read);
			}
		} catch (IOException e) {
			Log.error("Failed to copy stream", e);
		}
	}
	
	public static <T> void setList(List<T> list, T[] elements) {
		list.clear();
		for (int i = 0; i < elements.length; i++) {
			list.add(elements[i]);
		}
	}
	
	public static void readProperties(InputStream stream, HashMap<String, String> properties) {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
			reader.lines().forEach(line -> {
				String key = line.substring(0, line.indexOf(':'));
				String value = line.substring(key.length() + 1);
				properties.put(key.trim(), value.trim());
			});
		} catch (IOException e) {
			Log.error("Failed to read properties", e);
		}
	}
	
	public static void writeProperties(OutputStream stream, HashMap<String, String> properties) {
		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream))) {
			for (Map.Entry<String, String> property : properties.entrySet()) {
				writer.write(MessageFormat.format("{0}: {1}\n", property.getKey(), property.getValue()));
			}
		} catch (IOException e) {
			Log.error("Failed to write properties", e);
		}
	}

}
