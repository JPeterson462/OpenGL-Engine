package engine.gui;

import org.joml.Vector2f;
import org.joml.Vector4f;

import engine.Assets;
import engine.Camera;
import engine.Engine;
import engine.rendering.gui.TextRenderer;
import engine.rendering.gui.WidgetRenderer;
import engine.text.Font;
import engine.text.TextAlign;
import engine.text.TextBuffer;

public class Label extends Widget implements TextWidget {
	
	private TextBuffer textBuffer;
	
	public Label(String name, Font font, String text, Vector4f color, float fontSize, Vector2f size, TextAlign align) {
		super(name);
		getSize().set(size);
		textBuffer = new TextBuffer(text, new Vector2f(), size, color, fontSize, font, Assets.getEngine(), text.length(), align);
	}

	@Override
	public void initialize() {
		textBuffer.updateLayout(Assets.getEngine());
	}
	
	public void layout() {
		super.layout();
		textBuffer.getPosition().set(getAbsolutePosition());
		textBuffer.getSize().set(getSize());
		textBuffer.updateLayout(Assets.getEngine());
	}

	@Override
	public void render(Engine engine, Camera camera) {
		// IGNORE, TextRenderer handles this
	}

	@Override
	public Class<? extends WidgetRenderer> getRenderer() {
		return TextRenderer.class;
	}

	@Override
	public TextBuffer getBuffer() {
		return textBuffer;
	}

}
