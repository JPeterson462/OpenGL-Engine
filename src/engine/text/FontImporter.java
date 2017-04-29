package engine.text;

import engine.Engine;

public interface FontImporter {
	
	static final AngelcodeFontImporter angelcodeImporter = new AngelcodeFontImporter();
	
	public static Font loadFont(String path, Engine engine) {
		if (path.endsWith(".fnt"))
			return angelcodeImporter.loadFontImpl(path, engine);
		return null;
	}
	
	public default String getFontPath(String path) {
		return "fonts/" + path;
	}
	
	public Font loadFontImpl(String path, Engine engine);

}
