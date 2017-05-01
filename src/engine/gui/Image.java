package engine.gui;

import org.joml.Matrix4f;

import engine.Assets;
import engine.rendering.Geometry;
import engine.rendering.Shader;
import engine.rendering.Texture;

public class Image extends Widget {

	private Geometry geometry;
	
	private Texture texture;
	
	private String path;
	
	private Matrix4f modelMatrix;
	
	public Image(String name, String path) {
		super(name);
		this.path = path;
		modelMatrix = new Matrix4f();
	}
	
	public Image(String name, Texture texture) {
		super(name);
		this.texture = texture;
		modelMatrix = new Matrix4f();
	}

	@Override
	public void initialize() {
		geometry = Assets.newQuad();
		if (texture == null) {
			texture = Assets.newTexture(path);
		}
	}

	@Override
	public void render(Shader shader) {
		modelMatrix.translationRotateScale(getPosition().x, getPosition().y, 0, 0, 0, 0, 1, getSize().x, getSize().y, 1);
		shader.uploadMatrix("modelMatrix", modelMatrix);
		geometry.bind();
		texture.bind(0);
		geometry.renderGeometry();
		texture.unbind();
		geometry.unbind();
	}

}
