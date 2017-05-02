package engine.rendering.passes;

import org.joml.Matrix4f;

import engine.Camera;
import engine.Engine;
import engine.gui.Widget;
import engine.rendering.Shader;

public class DefaultGUIRenderer implements WidgetRenderer {
	
	private Shader shader;
	
	private Matrix4f modelMatrix;
	
	public DefaultGUIRenderer(Shader shader) {
		this.shader = shader;
		modelMatrix = new Matrix4f();
	}
	
	public void bind(Camera camera, Engine engine) {
		shader.bind();
		camera.uploadTo(shader);
	}

	@Override
	public void render(Widget widget, Engine engine) {
		modelMatrix.identity();
		modelMatrix.translationRotateScale(widget.getAbsolutePosition().x, widget.getAbsolutePosition().y, 0, 0, 0, 0, 1, widget.getSize().x, widget.getSize().y, 1);
		shader.uploadMatrix("modelMatrix", modelMatrix);
		widget.render(engine);
	}

	@Override
	public void unbind(Engine engine) {
		// Ignore
	}

}
