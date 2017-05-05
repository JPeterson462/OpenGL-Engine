package engine;

import java.io.InputStream;
import java.io.OutputStream;

public class ClasspathAsset implements Asset {
	
	private String fullPath;
	
	private ClasspathAsset(String fullPath) {
		this.fullPath = fullPath;
	}
	
	public ClasspathAsset(AssetNamespace namespace, String path) {
		fullPath = namespace.name().toLowerCase() + "/" + path;
	}

	@Override
	public InputStream read() {
		return Asset.class.getClassLoader().getResourceAsStream(fullPath);
	}

	@Override
	public OutputStream write() {
		throw new IllegalStateException("Cannot write to classpath resources");
	}

	@Override
	public Asset getRelative(String path) {
		return new ClasspathAsset(fullPath.substring(0, fullPath.lastIndexOf('/') + 1) + path);
	}

	@Override
	public Type getType() {
		return Type.LOCAL;
	}

	@Override
	public String getExtension() {
		return fullPath.substring(fullPath.lastIndexOf('.') + 1);
	}
	
	public String toString() {
		return "Asset [" + fullPath + "]";
	}

}
