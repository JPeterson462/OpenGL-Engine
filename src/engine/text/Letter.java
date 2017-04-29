package engine.text;

import org.joml.Vector2f;

public class Letter {
	
	private char c;
	
	private Vector2f advance, offset;
	
	private Vector2f texCoords0, texCoords1;
	
	private Vector2f size;

	public Letter(char c, Vector2f advance, Vector2f offset, Vector2f texCoords0, Vector2f texCoords1, Vector2f size) {
		this.c = c;
		this.advance = advance;
		this.offset = offset;
		this.texCoords0 = texCoords0;
		this.texCoords1 = texCoords1;
		this.size = size;
	}
	
	public char getCharacter() {
		return c;
	}

	public Vector2f getAdvance() {
		return advance;
	}

	public Vector2f getOffset() {
		return offset;
	}

	public Vector2f getTexCoords0() {
		return texCoords0;
	}

	public Vector2f getTexCoords1() {
		return texCoords1;
	}
	
	public Vector2f getSize() {
		return size;
	}

}
