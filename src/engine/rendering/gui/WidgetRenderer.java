package engine.rendering.gui;

import engine.Camera;
import engine.Engine;
import engine.gui.Widget;

public interface WidgetRenderer {
	
	public void bind(Camera camera, Engine engine);
	
	public void unbind(Engine engine);
	
	public void render(Widget widget, Engine engine);

}
