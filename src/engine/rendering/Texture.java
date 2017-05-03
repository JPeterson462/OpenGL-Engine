package engine.rendering;

import backends.opengl.GLTexture;

public class Texture {
	
	private Object backendData;
	
	private int width;
	
	private int height;
	
	private int type;
	
	public static final int TEXTURE_RGBA = 0x10, TEXTURE_RGB = 0x20, TEXTURE_ALPHA = 0x30;
	
	public Texture(Object backendData, int width, int height, int type) {
		this.backendData = backendData;
		this.width = width;
		this.height = height;
		this.type = type;
	}
	
	public Object getBackendData() {
		return backendData;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getType() {
		return type;
	}
	
	public void bind(int unit) {
		if (backendData instanceof GLTexture)
			((GLTexture) backendData).bind(unit);
	}
	
	public void unbind() {
		if (backendData instanceof GLTexture)
			((GLTexture) backendData).unbind();
	}
	
	public boolean equals(Object o) {
		return o instanceof Texture && ((Texture) o).getBackendData().equals(backendData);
	}

}
