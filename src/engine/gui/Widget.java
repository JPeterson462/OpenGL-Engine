package engine.gui;

import org.joml.Vector2f;

import engine.rendering.Shader;

public abstract class Widget {
	
	private Vector2f position, size;
	
	private boolean visible, active, hovered;
	
	private String name;
	
	public Widget(String name) {
		position = new Vector2f();
		size = new Vector2f();
		visible = true;
		active = false;
		hovered = false;
		this.name = name;
	}
	
	public abstract void initialize();
	
	public abstract void render(Shader shader);

	public Vector2f getPosition() {
		return position;
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

	public String getName() {
		return name;
	}

}
