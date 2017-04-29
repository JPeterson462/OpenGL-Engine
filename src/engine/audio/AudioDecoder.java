package engine.audio;

import java.io.InputStream;

public interface AudioDecoder {
	
	public SoundData decode(InputStream stream);

}
