package engine.text;

import org.joml.Vector2f;
import org.joml.Vector4f;

public class Font {
	
	private Page[] pages;
	
	private String fontFace;
	
	private int maxSize;
	
	private boolean bold, italics;
	
	private Vector4f padding;
	
	private Vector2f spacing;
	
	private float spaceWidth, lineHeight, baseline;
	
	public Font(Page[] pages, String fontFace, int maxSize, boolean bold, boolean italics, Vector4f padding, Vector2f spacing, float lineHeight, float baseline) {
		this.pages = pages;
		this.fontFace = fontFace;
		this.maxSize = maxSize;
		this.bold = bold;
		this.italics = italics;
		this.padding = padding;
		this.spacing = spacing;
		spaceWidth = 0;
		float count = 0;
		for (int i = 0; i < pages.length; i++) {
			Page page = pages[i];
			for (Letter letter : page.getLetters()) {
				if (letter != null) {
					count++;
					spaceWidth += letter.getAdvance().x;
				}
			}
		}
		spaceWidth /= count;
		this.lineHeight = lineHeight;
		this.baseline = baseline;
	}
	
	public float getSpaceWidth() {
		return spaceWidth;
	}
	
	public Page getPageFor(char c) {
		for (int i = 0; i < pages.length; i++) {
			if (pages[i].getLetter(c) != null) {
				return pages[i];
			}
		}
		return null;
	}
	
	public float getScaleFactor(float fontSize) {
		return fontSize / (float) maxSize;
	}
	
	public String getFontFace() {
		return fontFace;
	}

	public boolean isBold() {
		return bold;
	}

	public boolean isItalics() {
		return italics;
	}

	public Vector4f getPadding() {
		return padding;
	}

	public Vector2f getSpacing() {
		return spacing;
	}

	public float getLineHeight() {
		return lineHeight;
	}
	
	public float getBaseline() {
		return baseline;
	}

	public Page[] getPages() {
		return pages;
	}
	
	public int hashCode() {
		int hashCode = 0;
		hashCode = 31 * hashCode + fontFace.hashCode();
		hashCode = 31 * hashCode + Integer.hashCode(maxSize);
		hashCode = 31 * hashCode + Integer.hashCode(pages.length);
		return hashCode;
	}

}
