package engine.audio;

import java.io.InputStream;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBVorbis;

import utils.IOUtils;

public class VorbisDecoder implements AudioDecoder {
	
	private static IntBuffer channelsBuffer = BufferUtils.createIntBuffer(1), sampleRateBuffer = BufferUtils.createIntBuffer(1);
	
	public SoundData decode(InputStream stream) {
		ShortBuffer pcm = STBVorbis.stb_vorbis_decode_memory(IOUtils.readToBuffer(stream), channelsBuffer, sampleRateBuffer);
		return new SoundData(pcm, channelsBuffer.get(0), sampleRateBuffer.get(0));
	}

}
