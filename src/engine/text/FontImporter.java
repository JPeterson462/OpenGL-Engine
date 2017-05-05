package engine.text;

import engine.Asset;
import engine.Engine;

public interface FontImporter {
	
	static final AngelcodeFontImporter angelcodeImporter = new AngelcodeFontImporter();
	
	public static Font loadFont(Asset path, Engine engine) {
		String extension = path.getExtension();
		if (extension.equalsIgnoreCase("fnt"))
			return angelcodeImporter.loadFontImpl(path, engine);
		return null;
	}
	
	public default String getFontPath(String path) {
		return "fonts/" + path;
	}
	
	public Font loadFontImpl(Asset path, Engine engine);

}
