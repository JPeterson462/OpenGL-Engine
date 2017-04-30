package backends.openal;

public class ALMusic {
	
	private int source, buffers[];
	
	public ALMusic(int source, int[] buffers) {
		this.source = source;
		this.buffers = buffers;
	}
	
	public int getSource() {
		return source;
	}
	
	public int[] getBuffers() {
		return buffers;
	}

}
