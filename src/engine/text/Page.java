package engine.text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import engine.rendering.Texture;

public class Page {
	
	private Texture texture;
	
	private HashMap<Character, Letter> letters;
	
	public Page(Texture texture, ArrayList<Letter> letters) {
		this.texture = texture;
		this.letters = new HashMap<>();
		for (Letter letter : letters) {
			this.letters.put(letter.getCharacter(), letter);
		}
	}
	
	public Texture getTexture() {
		return texture;
	}
	
	public Letter getLetter(char c) {
		return letters.get(c);
	}
	
	public Collection<Letter> getLetters() {
		return letters.values();
	}

}
