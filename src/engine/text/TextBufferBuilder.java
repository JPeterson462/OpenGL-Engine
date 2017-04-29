package engine.text;

import java.util.ArrayList;

import org.joml.Vector2f;
import org.joml.Vector4f;

import engine.rendering.Vertex;

public class TextBufferBuilder {
	
	public static void buildText(String text, Vector2f size, Vector4f color, Font font, float fontSize, ArrayList<Vertex> vertices, ArrayList<Integer> indices) {
		if (text.length() == 0)
			return; // TextRenderer passes over this, no need to waste CPU cycles
		Page page = font.getPages()[0];
		String[] linesPass0 = text.split("\n");
		ArrayList<String> linesPass1 = new ArrayList<>();
		for (int i = 0; i < linesPass0.length; i++) {
			String line = linesPass0[i];
			if (line.contains(" ")) {
				String[] words = line.split(" ");
				float width = 0;
				String subline = "";
				for (int j = 0; j < words.length; j++) {
					float wordWidth = getWidth(words[j], font, fontSize, page);
					if (wordWidth + width > size.x) {
						linesPass1.add(subline.trim());
						subline = words[j];
						width = wordWidth;
					} else {
						subline += words[j] + " ";
						width += wordWidth + font.getSpaceWidth();
					}
				}
				if (subline.length() > 0) {
					linesPass1.add(subline);
				}
			} else {
				linesPass1.add(line);
			}
		}
		float startX = 0, startY = 0, lineHeight = font.getLineHeight();
		float x = startX, y = startY;
		int characterPointer = 0;
		float scale = font.getScaleFactor(fontSize);
		for (int i = 0; i < linesPass1.size(); i++) {
			String line = linesPass1.get(i);
			for (char c : line.toCharArray()) {
				Letter letter = page.getLetter(c);
				if (c != ' ') {
					vertices.get(characterPointer + 0).getPosition().set(x + letter.getOffset().x * scale, y + letter.getOffset().y * scale, 0);
					vertices.get(characterPointer + 0).getTextureCoord().set(letter.getTexCoords0());
					vertices.get(characterPointer + 0).getColor().set(color);
					vertices.get(characterPointer + 1).getPosition().set(x + (letter.getOffset().x + letter.getSize().x) * scale, y + letter.getOffset().y * scale, 0);
					vertices.get(characterPointer + 1).getTextureCoord().set(letter.getTexCoords1().x, letter.getTexCoords0().y);
					vertices.get(characterPointer + 1).getColor().set(color);
					vertices.get(characterPointer + 2).getPosition().set(x + (letter.getOffset().x + letter.getSize().x) * scale, y + (letter.getOffset().y + letter.getSize().y) * scale, 0);
					vertices.get(characterPointer + 2).getTextureCoord().set(letter.getTexCoords1());
					vertices.get(characterPointer + 2).getColor().set(color);
					vertices.get(characterPointer + 3).getPosition().set(x + letter.getOffset().x * scale, y + (letter.getOffset().y + letter.getSize().y) * scale, 0);
					vertices.get(characterPointer + 3).getTextureCoord().set(letter.getTexCoords0().x, letter.getTexCoords1().y);
					vertices.get(characterPointer + 3).getColor().set(color);
					characterPointer += 4;
					x += letter.getAdvance().x * scale;
				} else{
					x += font.getSpaceWidth() * scale;
				}
			}
			x = startX;
			y += lineHeight;
		}
		for (int i = characterPointer * 4; i < vertices.size(); i++) {
			vertices.get(i).reset();
		}
		for (int i = 0, c = 0; i < characterPointer * 6 && i < indices.size(); i += 6, c += 4) {
			indices.set(i + 0, c + 0);
			indices.set(i + 1, c + 1);
			indices.set(i + 2, c + 2);
			indices.set(i + 3, c + 2);
			indices.set(i + 4, c + 3);
			indices.set(i + 5, c + 0);
		}
		for (int i = characterPointer * 6; i < indices.size(); i++) {
			indices.set(i, 0);
		}
	}
	
	private static float getWidth(char c, Font font, float fontSize, Page page) {
		if (c == ' ')
			return font.getSpaceWidth() * font.getScaleFactor(fontSize);
		else
			return page.getLetter(c).getSize().x * font.getScaleFactor(fontSize);
			
	}
	
	private static float getWidth(String text, Font font, float fontSize, Page page) {
		float width = 0;
		for (char letter : text.toCharArray()) {
			if (letter == ' ')
				width += getWidth(letter, font, fontSize, page);
		}
		return width;
	}

}
