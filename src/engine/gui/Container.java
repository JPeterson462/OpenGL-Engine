package engine.gui;

import java.util.ArrayList;

import engine.Camera;
import engine.Engine;
import engine.rendering.passes.WidgetRenderer;
import utils.Visitor;

public class Container extends Widget {
	
	private ArrayList<Widget> widgets = new ArrayList<>();
	
	private WidgetRendererLookup rendererLookup;
	
	public Container(String name) {
		super(name);
	}
	
	public void addWidget(Widget widget) {
		widgets.add(widget);
		widget.setParent(this);
		widget.layout();
	}
	
	public void getWidgets(Visitor<Widget> visitor) {
		widgets.forEach(widget -> visitor.visit(widget));
	}
	
	public void removeWidget(Widget widget) {
		widgets.remove(widget);
		widget.setParent(null);
		widget.layout();
	}
	
	public void connectLookup(WidgetRendererLookup rendererLookup) {
		this.rendererLookup = rendererLookup;
		widgets.forEach(widget -> {
			if (widget instanceof Container) ((Container) widget).connectLookup(rendererLookup);
		});
	}
	
	public void layout() {
		super.layout();
		widgets.forEach(widget -> widget.layout());
	}
	
	public void tryInitialize() {
		widgets.forEach(widget -> widget.tryInitialize());
	}

	@Override
	public void initialize() {
		// Ignore
	}

	@Override
	public void render(Engine engine, Camera camera) {
		Class<?> lastRendererClass = null;
		WidgetRenderer renderer = null;
		engine.getRenderingBackend().setDepth(false);
		for (int i = 0; i < widgets.size(); i++) {
			Widget widget = widgets.get(i);
			Class<?> rendererClass = widget.getRenderer();
			if (lastRendererClass == null || lastRendererClass != rendererClass) {
				if (renderer != null) {
					renderer.unbind(engine);
				}
				renderer = rendererLookup.getRenderer(widget);
				renderer.bind(camera, engine);
			}
			renderer.render(widget, engine);
			lastRendererClass = rendererClass;
		}
		renderer.unbind(engine);
	}

	@Override
	public Class<? extends WidgetRenderer> getRenderer() {
		return null;
	}

}
