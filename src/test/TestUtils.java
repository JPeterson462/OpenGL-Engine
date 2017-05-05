package test;

import engine.AssetNamespace;
import engine.ClasspathAsset;

public class TestUtils {
	
	public static ClasspathAsset font(String path) {
		return new ClasspathAsset(AssetNamespace.FONTS, path);
	}

	public static ClasspathAsset texture(String path) {
		return new ClasspathAsset(AssetNamespace.TEXTURES, path);
	}

	public static ClasspathAsset shader(String path) {
		return new ClasspathAsset(AssetNamespace.SHADERS, path);
	}

	public static ClasspathAsset model(String path) {
		return new ClasspathAsset(AssetNamespace.MODELS, path);
	}

	public static ClasspathAsset sound(String path) {
		return new ClasspathAsset(AssetNamespace.SOUNDS, path);
	}
	
}
