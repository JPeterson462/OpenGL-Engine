package engine.input;

public interface MouseListener {
	
	public void onDrag(int x, int y, int dx, int dy, Modifiers modifiers);
	
	public void onScroll(int dx, int dy, Modifiers modifiers);
	
	public void onClick(int x, int y, MouseButton button, Modifiers modifiers);

}
