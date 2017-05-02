package engine.gui;

import org.joml.Vector2f;

import engine.Engine;
import engine.rendering.passes.WidgetRenderer;

public abstract class Widget {
	
	private Vector2f position, absolutePosition, size;
	
	private Widget parent;
	
	private boolean visible, active, hovered, initialized;
	
	private String name;
	
	public Widget(String name) {
		position = new Vector2f();
		absolutePosition = new Vector2f();
		size = new Vector2f();
		visible = true;
		active = false;
		hovered = false;
		this.name = name;
	}
	
	public void setParent(Widget parent) {
		this.parent = parent;
	}
	
	public void tryInitialize() {
		if (!initialized) {
			initialize();
			initialized = true;
		}
	}
	
	public abstract void initialize();
	
	public abstract void render(Engine engine);
	
	public void layout() {
		absolutePosition.set(position);
		if (parent != null) {
			absolutePosition.add(parent.getAbsolutePosition());
		}
	}

	public Vector2f getPosition() {
		return position;
	}
	
	public Vector2f getAbsolutePosition() {
		return absolutePosition;
	}

	public Vector2f getSize() {
		return size;
	}

	public boolean isVisible() {
		return visible;
	}

	public boolean isActive() {
		return active;
	}

	public boolean isHovered() {
		return hovered;
	}
	
	public boolean isInitialized() {
		return initialized;
	}

	public String getName() {
		return name;
	}
	
	public abstract Class<? extends WidgetRenderer> getRenderer();

}
