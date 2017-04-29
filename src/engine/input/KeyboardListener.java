package engine.input;

public interface KeyboardListener {
	
	public void onKeyTyped(Key key, Modifiers modifiers);
	
	public void onLetterTyped(char letter, Modifiers modifiers);

}
