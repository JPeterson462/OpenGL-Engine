package engine.rendering.passes;

import java.util.HashMap;

import engine.Camera;
import engine.Engine;
import engine.OrthographicCamera;
import engine.gui.GUI;

public class GUIRenderer {
	
	private GUI gui;
	
	private Camera camera;
	
	private HashMap<Class<?>, WidgetRenderer> renderers = new HashMap<>();
	
	public GUIRenderer(GUI gui, HashMap<Class<?>, WidgetRenderer> renderers, float width, float height) {
		this.gui = gui;
		this.renderers = renderers;
		gui.getContainer().tryInitialize();
		gui.getContainer().connectLookup(widget -> this.renderers.get(widget.getRenderer()));
		camera = new OrthographicCamera(width, height);
	}
	
	public Camera getCamera() {
		return camera;
	}
	
	public void render(Engine engine) {
		gui.getContainer().tryInitialize();
		camera.update();
		gui.getContainer().render(engine, camera);
		engine.getRenderingBackend().setDepth(true);
	}

}
