package engine.input;

public class Modifiers {
	
	private boolean shift, control, alt;
	
	public Modifiers(boolean shift, boolean control, boolean alt) {
		this.shift = shift;
		this.control = control;
		this.alt = alt;
	}
	
	public boolean isShift() {
		return shift;
	}
	
	public boolean isControl() {
		return control;
	}
	
	public boolean isAlt() {
		return alt;
	}

}
