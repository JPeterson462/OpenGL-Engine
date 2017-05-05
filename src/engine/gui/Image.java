package engine.gui;

import engine.Asset;
import engine.Assets;
import engine.Camera;
import engine.Engine;
import engine.rendering.Geometry;
import engine.rendering.Texture;
import engine.rendering.passes.DefaultGUIRenderer;
import engine.rendering.passes.WidgetRenderer;

public class Image extends Widget {

	private Geometry geometry;
	
	private Texture texture;
	
	private Asset path;
	
	public Image(String name, Asset path) {
		super(name);
		this.path = path;
	}
	
	public Image(String name, Texture texture) {
		super(name);
		this.texture = texture;
	}

	@Override
	public void initialize() {
		geometry = Assets.newQuad();
		if (texture == null) {
			texture = Assets.newTexture(path);
		}
	}

	@Override
	public void render(Engine engine, Camera camera) {
		geometry.bind();
		texture.bind(0);
		geometry.renderGeometry();
		texture.unbind();
		geometry.unbind();
	}

	@Override
	public Class<? extends WidgetRenderer> getRenderer() {
		return DefaultGUIRenderer.class;
	}

}
