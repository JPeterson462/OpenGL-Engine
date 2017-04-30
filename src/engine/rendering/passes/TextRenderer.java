package engine.rendering.passes;

import java.util.ArrayList;

import org.joml.Vector4f;

import engine.Camera;
import engine.Engine;
import engine.OrthographicCamera;
import engine.rendering.Geometry;
import engine.rendering.Shader;
import engine.rendering.Texture;
import engine.text.TextBuffer;
import engine.text.TextEffect;

public class TextRenderer {
	
	private Shader shader;
	
	private ArrayList<TextBuffer> textBuffers;

	private Camera camera;
	
	public TextRenderer(Shader shader, float width, float height) {
		this.shader = shader;
		textBuffers = new ArrayList<>();
		camera = new OrthographicCamera(width, height);
		shader.bind();
		shader.uploadMatrix("projectionMatrix", camera.getProjectionMatrix());
		shader.unbind();
	}	
	
	public void addText(TextBuffer buffer) {
		textBuffers.add(buffer);
	}
	
	public void render(Engine engine) {
		camera.update();
		engine.getRenderingBackend().setDepth(false);
		shader.bind();
		Geometry lastGeometry = null;
		Texture lastTexture = null;
		for (int i = 0; i < textBuffers.size(); i++) {
			TextBuffer buffer = textBuffers.get(i);
			if (buffer.getText().length() == 0)
				continue; // Don't render empty text buffers
			shader.uploadMatrix("viewMatrix", buffer.getViewMatrix());
			TextEffect effect = buffer.getEffect();
			shader.uploadVector("textEffect", new Vector4f(effect.getLineWidth(), effect.getSharpness(), effect.getBorderWidth(), effect.getOffset().x));
			shader.uploadVector("effectColor", effect.getColor());
			Texture texture = buffer.getFont().getPages()[0].getTexture();
			buffer.getGeometry().bind();
			texture.bind(0);
			buffer.getGeometry().renderGeometry();
			lastGeometry = buffer.getGeometry();
			lastTexture = texture;
		}
		if (lastGeometry != null) {
			lastGeometry.unbind();
			lastTexture.unbind();
		}
		shader.unbind();
		engine.getRenderingBackend().setDepth(true);
	}

}
