package engine.text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.joml.Vector2f;
import org.joml.Vector4f;

import com.esotericsoftware.minlog.Log;

import engine.Engine;
import engine.rendering.Texture;
import engine.text.Angelcode.FontData;
import engine.text.Angelcode.Info;

public class AngelcodeFontImporter implements FontImporter {

	private HashMap<String, String> properties = new HashMap<>();
	
	private String path;
	
	@Override
	public Font loadFontImpl(String path, Engine engine) {
		String dirPath = path.contains("/") ? path.substring(path.lastIndexOf('/')) : "";
		String fontPath = getFontPath(dirPath + path);
		String fontDirPath = getFontPath(dirPath);
		this.path = path;
		ArrayList<Angelcode.Letter> letters = new ArrayList<>();
		ArrayList<Angelcode.Page> pageList = new ArrayList<>();
		FontData[] dataPointer = new FontData[1];
		Info[] infoPointer = new Info[1];
		if (engine.getResource(fontPath) == null) {
			return null;
		}
		// 1. Load the file into digestable data structures
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(engine.getResource(fontPath)))) {
			reader.lines().forEach(line -> {
				String[] parts = line.split(" +");
				switch (parts[0].toLowerCase()) {
					case "char":
						letters.add(parseLetter(parts));
						break;
					case "page":
						pageList.add(parsePage(parts));
						break;
					case "chars":
						// Ignore
						break;
					case "common":
						dataPointer[0] = parseFontData(parts);
						break;
					case "info":
						infoPointer[0] = parseInfo(parts);
						break;
					case "kernings":
						// Ignore
						break;
					default:
						// Ignore
						break;
				}
			});
		} catch (IOException e) {
			Log.error("Failed to load angelcode font: " + path, e);
		}
		// 2. Convert the data to a Font instance
		FontData data = dataPointer[0];
		Info info = infoPointer[0];
		Page[] pages = new Page[data.pages];
		for (int i = 0; i < pageList.size(); i++) {
			Angelcode.Page pageData = pageList.get(i);
			Texture texture = engine.getRenderingBackend().createTexture(engine.getResource(fontDirPath + pageData.file), true, false);
			ArrayList<Letter> pageLetters = new ArrayList<>();
			for (int j = 0; j < letters.size(); j++) {
				Angelcode.Letter letterData = letters.get(j);
				if (letterData.page == pageData.id) {
					float s0 = (float) letterData.x / (float) texture.getWidth();
					float t0 = (float) letterData.y / (float) texture.getHeight();
					float s1 = (float) (letterData.x + letterData.width) / (float) texture.getWidth();
					float t1 = (float) (letterData.y + letterData.height) / (float) texture.getHeight();
					pageLetters.add(new Letter(letterData.c, new Vector2f(letterData.xAdvance, 0), new Vector2f(letterData.xOffset, letterData.yOffset), new Vector2f(s0, t0), 
							new Vector2f(s1, t1), new Vector2f(letterData.width, letterData.height)));
				}
			}
			pages[pageData.id] = new Page(texture, pageLetters);
		}
		return new Font(pages, info.face, info.size, info.bold, info.italic, info.padding, info.spacing, data.lineHeight, data.baseline);
	}
	
	private int integer(String property) {
		return Integer.parseInt(properties.getOrDefault(property, "0"));
	}
	
	private String string(String property) {
		String str = properties.getOrDefault(property, "");
		while (str.length() > 0 && str.charAt(0) == '"' && str.charAt(str.length() - 1) == '"') {
			str = str.substring(1, str.length() - 1);
		}
		return str;
	}
	
	private boolean bool(String property) {
		return integer(property) > 0;
	}
	
	private Vector4f vec4(String property) {
		String[] values = string(property).split(",");
		return new Vector4f(Integer.parseInt(values[0]), Integer.parseInt(values[1]), 
				Integer.parseInt(values[2]), Integer.parseInt(values[3]));
	}
	
	private Vector2f vec2(String property) {
		String[] values = string(property).split(",");
		return new Vector2f(Integer.parseInt(values[0]), Integer.parseInt(values[1]));
	}
	
	private void loadProperties(String[] parts) {
		properties.clear();
		for (int i = 0; i < parts.length; i++) {
			String propertyValue = parts[i];
			if (propertyValue.contains("=")) {
				String[] propertyAndValue = propertyValue.split("=");
				if (propertyAndValue.length != 2)
					throw new IllegalStateException("Invalid font: " + path);
				properties.put(propertyAndValue[0], propertyAndValue[1]);
			}
		}
	}
	
	private Angelcode.Letter parseLetter(String[] parts) {
		loadProperties(parts);
		return new Angelcode.Letter((char) integer("id"), integer("x"), integer("y"), integer("width"), integer("height"), integer("xoffset"),
				integer("yoffset"), integer("xadvance"), integer("page"), integer("chnl"));
	}
	
	private Angelcode.Page parsePage(String[] parts) {
		loadProperties(parts);
		return new Angelcode.Page(integer("id"), string("file"));
	}
	
	private FontData parseFontData(String[] parts) {
		loadProperties(parts);
		return new FontData(integer("lineHeight"), integer("base"), integer("scaleW"), integer("scaleH"), integer("pages"), bool("packed"));
	}
	
	private Info parseInfo(String[] parts) {
		loadProperties(parts);
		return new Info(string("face"), integer("size"), bool("bold"), bool("italic"), string("charset"), bool("unicode"), 
				integer("stretchH"), bool("smooth"), bool("aa"), vec4("padding"), vec2("spacing"));
	}

}
