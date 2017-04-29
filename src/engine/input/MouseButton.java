package engine.input;

public class MouseButton {
	
	public static MouseButton BUTTON_1 = new MouseButton(0),
			BUTTON_2 = new MouseButton(0), BUTTON_3 = new MouseButton(0),
			BUTTON_4 = new MouseButton(0), BUTTON_5 = new MouseButton(0),
			BUTTON_6 = new MouseButton(0), BUTTON_7 = new MouseButton(0),
			BUTTON_8 = new MouseButton(0), LEFT = new MouseButton(0),
			MIDDLE = new MouseButton(0), RIGHT = new MouseButton(0);
	
	private final int id;
	
	public MouseButton(int id) {
		this.id = id;
	}
	
	public boolean equals(Object o) {
		return o instanceof MouseButton && id == ((MouseButton) o).id;
	}
	
	public int hashCode() {
		return Integer.hashCode(id);
	}

}
