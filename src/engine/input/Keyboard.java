package engine.input;

import java.util.ArrayList;
import java.util.HashMap;

public class Keyboard implements KeyboardListener {
	
	private ArrayList<KeyboardListener> listeners = new ArrayList<>();
	
	private HashMap<Key, Boolean> keyStates = new HashMap<>();
	
	private int count;
	
	public void onKeyDown(Key key) {
		keyStates.put(key, true);
	}
	
	public void onKeyUp(Key key) {
		keyStates.put(key, false);
	}
	
	public boolean isKeyDown(Key key) {
		return keyStates.containsKey(key) && keyStates.get(key).booleanValue();
	}
	
	public void addListener(KeyboardListener listener) {
		listeners.add(listener);
		count = listeners.size();
	}

	@Override
	public void onKeyTyped(Key key, Modifiers modifiers) {
		for (int i = 0; i < count; i++) {
			listeners.get(i).onKeyTyped(key, modifiers);
		}
	}

	@Override
	public void onLetterTyped(char letter, Modifiers modifiers) {
		for (int i = 0; i < count; i++) {
			listeners.get(i).onLetterTyped(letter, modifiers);
		}
	}
	
}
