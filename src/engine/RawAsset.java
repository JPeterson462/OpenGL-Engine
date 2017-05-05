package engine;

import java.io.InputStream;
import java.io.OutputStream;

public class RawAsset implements Asset {
	
	private InputStream stream;
	
	public RawAsset(InputStream stream) {
		this.stream = stream;
	}

	@Override
	public InputStream read() {
		return stream;
	}

	@Override
	public OutputStream write() {
		throw new IllegalStateException("Cannot write to raw resources");
	}

	@Override
	public Asset getRelative(String path) {
		throw new IllegalStateException("Cannot get relative paths to raw resources");
	}

	@Override
	public Type getType() {
		return Type.UNKNOWN;
	}

	@Override
	public String getExtension() {
		throw new IllegalStateException("Cannot get extensions of raw resources");
	}

}
