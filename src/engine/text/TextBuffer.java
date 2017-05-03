package engine.text;

import java.util.ArrayList;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import com.esotericsoftware.minlog.Log;

import engine.Engine;
import engine.rendering.Geometry;
import engine.rendering.Vertex;

public class TextBuffer {
	
	private Vector2f position, size;
	
	private Vector4f color;
	
	private float fontSize;
	
	private String text;
	
	private Geometry geometry;
	
	private Matrix4f viewMatrix;
	
	private Font font;
	
	private int maxLetters;
	
	private ArrayList<Vertex> vertices;
	
	private ArrayList<Integer> indices;
	
	private TextAlign align;
	
	public TextBuffer(String text, Vector2f position, Vector2f size, Vector4f color, float fontSize, Font font, Engine engine, int maxLetters, TextAlign align) {
		this.text = text;
		this.position = position;
		this.size = size;
		this.color = color;
		this.fontSize = fontSize;
		this.font = font;
		vertices = new ArrayList<>(maxLetters * 4);
		for (int i = 0; i < maxLetters * 4; i++) {
			vertices.add(new Vertex(new Vector2f(), new Vector2f(), new Vector4f()));
		}
		indices = new ArrayList<>(maxLetters * 6);
		for (int i = 0; i < maxLetters * 6; i++) {
			indices.add(0);
		}
		TextBufferBuilder.buildText(text, size, color, font, fontSize, vertices, indices, align);
		this.align = align;
		geometry = engine.getRenderingBackend().createGeometry(vertices, indices, false);
		viewMatrix = new Matrix4f();
		viewMatrix.translationRotateScale(position.x, position.y, 0, 0, 0, 0, 1, 1, 1, 1);
		this.maxLetters = maxLetters;
	}
	
	public Vector2f getPosition() {
		return position;
	}
	
	public Vector2f getSize() {
		return size;
	}
	
	public Vector4f getColor() {
		return color;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public String getText() {
		return text;
	}
	
	public void updateLayout(Engine engine) {
		if (text.length() > maxLetters) {
			throw new IllegalStateException("This TextBuffer can only hold " + maxLetters + " characters!");
		}
		if (text.length() > 0) {
			TextBufferBuilder.buildText(text, size, color, font, fontSize, vertices, indices, align);
//			engine.getRenderingBackend().updateGeometry(geometry, vertices, indices);
			geometry = engine.getRenderingBackend().createGeometry(vertices, indices, true);//TODO; remove this... awful workaround
			viewMatrix.translationRotateScale(position.x, position.y, 0, 0, 0, 0, 1, 1, 1, 1);	
		} else {
			Log.info("No text was supplied to this buffer");
		}
	}
	
	public Matrix4f getViewMatrix() {
		return viewMatrix;
	}
	
	public Geometry getGeometry() {
		return geometry;
	}
	
	public Font getFont() {
		return font;
	}
}
