package engine.audio;

import java.nio.ShortBuffer;

public class SoundData {
	
	private ShortBuffer pcm;
	
	private int channels, sampleRate;

	public SoundData(ShortBuffer pcm, int channels, int sampleRate) {
		this.pcm = pcm;
		this.channels = channels;
		this.sampleRate = sampleRate;
	}

	public ShortBuffer getPCM() {
		return pcm;
	}

	public int getChannels() {
		return channels;
	}

	public int getSampleRate() {
		return sampleRate;
	}

}
