package engine.rendering.passes;

import engine.Camera;
import engine.Engine;
import engine.OrthographicCamera;
import engine.gui.GUI;
import engine.gui.Widget;
import engine.rendering.Shader;

public class GUIRenderer {
	
	private GUI gui;
	
	private Shader shader;
	
	private Camera camera;
	
	public GUIRenderer(GUI gui, Shader shader, float width, float height) {
		this.gui = gui;
		this.shader = shader;
		for (int i = 0; i < gui.getWidgets().size(); i++) {
			Widget widget = gui.getWidgets().get(i);
			widget.initialize();
		}
		camera = new OrthographicCamera(width, height);
	}
	
	public Camera getCamera() {
		return camera;
	}
	
	public void render(Engine engine) {
		camera.update();
		engine.getRenderingBackend().setDepth(false);
		shader.bind();
		camera.uploadTo(shader);
		for (int i = 0; i < gui.getWidgets().size(); i++) {
			Widget widget = gui.getWidgets().get(i);
			widget.render(shader);
		}
		shader.unbind();
		engine.getRenderingBackend().setDepth(true);
	}

}
