package engine.audio;

import java.nio.ShortBuffer;

import org.lwjgl.BufferUtils;

public abstract class AudioStream {

	private SoundData data;
	
	private ShortBuffer pcm = BufferUtils.createShortBuffer(BUFFER_SIZE);
	
	public static final int BUFFER_SIZE = 1024 * 64; // Read 64k at a time
	
	protected int samples;
	
	private float length;
	
	public AudioStream(SoundData data, float length) {
		this.data = data;
		this.length = length;
	}
	
	public float getLength() {
		return length;
	}
	
	public ShortBuffer getPCM() {
		return pcm;
	}
	
	public SoundData getData() {
		return data;
	}
	
	public int getSamplesUsed() {
		return samples;
	}
	
	public abstract boolean readData(final int bufferSize, ShortBuffer pcm);
	
	public abstract void rewind();
	
}
