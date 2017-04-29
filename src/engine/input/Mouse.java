package engine.input;

import java.util.ArrayList;
import java.util.HashMap;

public class Mouse implements MouseListener {
	
	private ArrayList<MouseListener> listeners = new ArrayList<>();
	
	private HashMap<MouseButton, Boolean> mouseButtonStates = new HashMap<>();

	private int x = -1, y = -1;
	
	private int count;
	
	public void onCursorPos(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void onButtonDown(MouseButton button) {
		mouseButtonStates.put(button, true);
	}
	
	public void onButtonUp(MouseButton button) {
		mouseButtonStates.put(button, false);
	}
	
	public boolean isButtonDown(MouseButton button) {
		return mouseButtonStates.containsKey(button) && mouseButtonStates.get(button).booleanValue();
	}
	
	public void addListener(MouseListener listener) {
		listeners.add(listener);
		count = listeners.size();
	}
	
	public int getMouseX() {
		return x;
	}
	
	public int getMouseY() {
		return y;
	}

	@Override
	public void onDrag(int x, int y, int dx, int dy, Modifiers modifiers) {
		for (int i = 0; i < count; i++) {
			listeners.get(i).onDrag(x, y, dx, dy, modifiers);
		}
	}

	@Override
	public void onScroll(int dx, int dy, Modifiers modifiers) {
		for (int i = 0; i < count; i++) {
			listeners.get(i).onScroll(dx, dy, modifiers);
		}
	}

	@Override
	public void onClick(int x, int y, MouseButton button, Modifiers modifiers) {
		for (int i = 0; i < count; i++) {
			listeners.get(i).onClick(x, y, button, modifiers);
		}
	}

}
