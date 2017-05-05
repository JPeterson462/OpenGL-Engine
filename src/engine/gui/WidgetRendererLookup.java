package engine.gui;

import engine.rendering.gui.WidgetRenderer;

@FunctionalInterface
public interface WidgetRendererLookup {
	
	public WidgetRenderer getRenderer(Widget widget);

}
