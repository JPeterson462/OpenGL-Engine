package engine.models;

import engine.Engine;
import utils.BitFlags;
import utils.BitFlags8;

public interface ModelImporter {
	
	static final OBJImporter objImporter = new OBJImporter();
	
	static final BitFlags flags = new BitFlags8();
	
	public static Model loadModel(String path, Engine engine, boolean computeTangents) {
		if (path.endsWith(".obj"))
			return objImporter.loadModelImpl(path, engine, computeTangents);
		return null;
	}
	
	public Model loadModelImpl(String path, Engine engine, boolean computeTangents);

}
