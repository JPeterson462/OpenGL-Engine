package engine.audio;

public class Music {
	
	private Object backendData;
	
	private AudioStream audioStream;
	
	private float length, played;
	
	public Music(Object backendData, AudioStream stream, float length) {
		this.backendData = backendData;
		audioStream = stream;
		this.length = length;
		played = 0;
	}
	
	public float getLength() {
		return length;
	}
	
	public void setPlayed(float played) {
		this.played = played;
	}
	
	public void addToPlayed(float delta) {
		played += delta;
	}
	
	public float getPlayed() {
		return played;
	}
	
	public Object getBackendData() {
		return backendData;
	}
	
	public AudioStream getAudioStream() {
		return audioStream;
	}

}
