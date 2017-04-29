package engine.text;

import org.joml.Vector2f;
import org.joml.Vector3f;

public interface TextEffect {
	
	public static TextEffect newBorder(Vector3f color) {
		return newBorder(color, 1.4f);
	}
	
	public static TextEffect newBorder(Vector3f color, float borderWidth) {
		return new TextEffectImplementation(0.1f, 0.5f, 0.5f * borderWidth, new Vector2f(), color);
	}
	
	public static TextEffect newDropShadow(Vector3f color) {
		return newDropShadow(color, 0.006f);
	}
	
	public static TextEffect newDropShadow(Vector3f color, float offset) {
		return new TextEffectImplementation(0.1f, 0.5f, 0.5f, new Vector2f(offset, offset), color);
	}
	
	public static TextEffect newSharpnessEffect(float sharpness) {
		return new TextEffectImplementation(sharpness * 0.5f, 0.5f, 0.5f, new Vector2f(), new Vector3f(0, 0, 0));
	}
	
	class TextEffectImplementation implements TextEffect {
		
		private float sharpness, lineWidth, borderWidth;
		
		private Vector2f offset;
		
		private Vector3f color;

		public TextEffectImplementation(float sharpness, float lineWidth, float borderWidth, Vector2f offset, Vector3f color) {
			this.sharpness = sharpness;
			this.lineWidth = lineWidth;
			this.borderWidth = borderWidth;
			this.offset = offset;
			this.color = color;
		}

		public float getSharpness() {
			return sharpness;
		}

		public float getLineWidth() {
			return lineWidth;
		}

		public float getBorderWidth() {
			return borderWidth;
		}

		public Vector2f getOffset() {
			return offset;
		}

		public Vector3f getColor() {
			return color;
		}

	}
	
	public float getSharpness();
	
	public float getLineWidth();
	
	public float getBorderWidth();
	
	public Vector2f getOffset();
	
	public Vector3f getColor();

}
