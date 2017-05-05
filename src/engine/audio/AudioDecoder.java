package engine.audio;

import engine.Asset;

public interface AudioDecoder {
	
	public SoundData decode(Asset stream);
	
	public AudioStream openStream(Asset stream);
	
	public void shutdown();

}
