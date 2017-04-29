package engine.audio;

public class SoundEffect {
	
	private Object backendData;
	
	public SoundEffect(Object backendData) {
		this.backendData = backendData;
	}
	
	public Object getBackendData() {
		return backendData;
	}

}
