package engine.audio;

import java.io.InputStream;

public interface AudioDecoder {
	
	public SoundData decode(InputStream stream);
	
	public AudioStream openStream(InputStream stream);
	
	public void shutdown();

}
