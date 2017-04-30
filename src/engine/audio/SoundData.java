package engine.audio;

import java.nio.ShortBuffer;

public class SoundData {
	
	private ShortBuffer pcm;
	
	private int channels, sampleRate, samples;

	public SoundData(ShortBuffer pcm, int channels, int sampleRate, int samples) {
		this.pcm = pcm;
		this.channels = channels;
		this.sampleRate = sampleRate;
		this.samples = samples;
	}
	
	public int getSamples() {
		return samples;
	}
	
	public void usedSamples(int samples) {
		this.samples -= samples / channels;
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
	
	public void setSampleCount(int samples) {
		this.samples = samples;
	}

}
