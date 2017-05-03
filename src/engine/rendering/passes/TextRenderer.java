package engine.rendering.passes;

import org.joml.Vector4f;

import engine.Camera;
import engine.Engine;
import engine.gui.TextWidget;
import engine.gui.Widget;
import engine.rendering.Shader;
import engine.rendering.Texture;
import engine.text.TextBuffer;

public class TextRenderer implements WidgetRenderer {
	
	private Shader shader;
	
	public TextRenderer(Shader shader) {
		this.shader = shader;
	}	
	
	@Override
	public void bind(Camera camera, Engine engine) {
		engine.getRenderingBackend().setDepth(false);
		shader.bind();
		shader.uploadMatrix("projectionMatrix", camera.getProjectionMatrix());
	}
	
	public void render(TextBuffer buffer, Engine engine) {
		shader.uploadMatrix("viewMatrix", buffer.getViewMatrix());
		shader.uploadVector("effectSharpness", new Vector4f(0.5f, 0.1f, 0, 0.4f));
		Texture texture = buffer.getFont().getPages()[0].getTexture();
		buffer.getGeometry().bind();
		texture.bind(0);
		buffer.getGeometry().renderGeometry();
		buffer.getGeometry().unbind();
		texture.unbind();
	}

	@Override
	public void render(Widget widget, Engine engine) {
		if (widget instanceof TextWidget) {
			TextBuffer buffer = ((TextWidget) widget).getBuffer();
			if (buffer.getText().length() == 0)
				return; // Don't render empty text buffers
			render(buffer, engine);
		}
	}

	@Override
	public void unbind(Engine engine) {
		shader.unbind();
		engine.getRenderingBackend().setDepth(true);
	}

}
