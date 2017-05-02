package engine.animation;

import engine.Engine;

public interface AnimationImporter {
	
	static final ColladaAnimationImporter colladaImporter = new ColladaAnimationImporter();
	
	public static Animation loadAnimation(String path, Engine engine) {
		if (path.endsWith(".dae")) {
			return colladaImporter.loadAnimationImpl(path, engine);
		}
		return null;
	}
	
	public Animation loadAnimationImpl(String path, Engine engine);

}
