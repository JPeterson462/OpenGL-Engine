package engine;

@FunctionalInterface
public interface FileDropCallback {
	
	public void onDropFiles(String[] paths);

}
