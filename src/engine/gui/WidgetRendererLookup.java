package engine.gui;

import engine.rendering.passes.WidgetRenderer;

@FunctionalInterface
public interface WidgetRendererLookup {
	
	public WidgetRenderer getRenderer(Widget widget);

}
