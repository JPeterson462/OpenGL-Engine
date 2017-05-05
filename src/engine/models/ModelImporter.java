package engine.models;

import engine.Asset;
import engine.Engine;
import utils.BitFlags;
import utils.BitFlags8;

public interface ModelImporter {
	
	static final OBJImporter objImporter = new OBJImporter();
	
	static final BitFlags flags = new BitFlags8();
	
	public static Model loadModel(Asset path, Engine engine, boolean computeTangents) {
		String extension = path.getExtension();
		if (extension.equalsIgnoreCase("obj"))
			return objImporter.loadModelImpl(path, engine, computeTangents);
		return null;
	}
	
	public Model loadModelImpl(Asset path, Engine engine, boolean computeTangents);

}
