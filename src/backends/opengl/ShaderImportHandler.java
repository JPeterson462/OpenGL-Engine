package backends.opengl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.esotericsoftware.minlog.Log;

import engine.Asset;

public class ShaderImportHandler {
	
	public static StringBuilder readShader(Asset stream) {
		StringBuilder builder = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream.read()))) {
			reader.lines().forEach(line -> {
				String rawLine = line.trim();
				if (rawLine.startsWith("#include")) {
					// Format: #include "name.frag"
					String[] parts = rawLine.split(" ");
					String fragmentName = parts[1];
					fragmentName = fragmentName.substring(1, fragmentName.length() - 1);
					if (fragmentName.endsWith(".frag")) {
						builder.append(readShader(stream.getRelative(fragmentName))).append('\n');
					} else {
						Log.error("Only .frag files can be loaded into shaders");
					}
				} else {
					builder.append(line).append('\n');
				}
			});
		} catch (IOException e) {
			Log.error("Exception occurred while loading file from input stream", e);
		}
		return builder;
	}

}
