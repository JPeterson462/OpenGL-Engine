package engine.animation;

import engine.Engine;
import engine.rendering.Texture;

public interface AnimatedModelImporter {
	
	static final ColladaAnimatedModelImporter colladaImporter = new ColladaAnimatedModelImporter();
	
	public static AnimatedModel loadAnimatedModel(String path, Engine engine, Texture texture) {
		if (path.endsWith(".dae")) {
			return colladaImporter.loadAnimatedModelImpl(path, engine, texture);
		}
		return null;
	}
	
	public AnimatedModel loadAnimatedModelImpl(String path, Engine engine, Texture texture);

}
