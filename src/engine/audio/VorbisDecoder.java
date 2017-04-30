package engine.audio;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.HashSet;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryUtil;

import utils.IOUtils;

public class VorbisDecoder implements AudioDecoder {
	
	private static IntBuffer channelsBuffer = BufferUtils.createIntBuffer(1), sampleRateBuffer = BufferUtils.createIntBuffer(1);
	
	private HashSet<Long> decoderHandles = new HashSet<>();
	
	public SoundData decode(InputStream stream) {
		ShortBuffer pcm = STBVorbis.stb_vorbis_decode_memory(IOUtils.readToBuffer(stream), channelsBuffer, sampleRateBuffer);
		return new SoundData(pcm, channelsBuffer.get(0), sampleRateBuffer.get(0), 0);
	}

	@Override
	public AudioStream openStream(InputStream stream) {
		ByteBuffer vorbis = IOUtils.readToBuffer(stream);
		IntBuffer error = BufferUtils.createIntBuffer(1);
		long ihandle = STBVorbis.stb_vorbis_open_memory(vorbis, error, null);
		if (ihandle == MemoryUtil.NULL) {
			throw new IllegalStateException("Failed to open Ogg Vorbis file. Error: " + error.get(0));
		}
		decoderHandles.add(ihandle);
		SoundData data;
		try (STBVorbisInfo info = STBVorbisInfo.malloc()) {
			STBVorbis.stb_vorbis_get_info(ihandle, info);
			data = new SoundData(null, info.channels(), info.sample_rate(), STBVorbis.stb_vorbis_stream_length_in_samples(ihandle));
		}
		return new VorbisAudioStream(data, STBVorbis.stb_vorbis_stream_length_in_seconds(ihandle), ihandle, stream, vorbis) {

			@Override
			public boolean readData(final int bufferSize, ShortBuffer pcm) {
				int samples = 0;
				while (samples < bufferSize) {
					pcm.position(samples);
					int samplesPerChannel = STBVorbis.stb_vorbis_get_samples_short_interleaved(handle, data.getChannels(), pcm);
					if (samplesPerChannel == 0) {
						break;
					}
					samples += samplesPerChannel * data.getChannels();
				}
				if (samples == 0)
					return false;
				this.samples = samples;
				pcm.position(0);
				return true;
			}

			@Override
			public void rewind() {
				STBVorbis.stb_vorbis_seek_start(handle);
				data.setSampleCount(STBVorbis.stb_vorbis_stream_length_in_samples(handle));
			}
			
		};
	}
	
	@SuppressWarnings("unused")
	private abstract class VorbisAudioStream extends AudioStream {
		
		protected long handle;
		
		// Keep pointers, prevent GC
		private InputStream stream;
		private ByteBuffer buffer;

		public VorbisAudioStream(SoundData data, float length, long handle, InputStream stream, ByteBuffer buffer) {
			super(data, length);
			this.handle = handle;
			this.stream = stream;
			this.buffer = buffer;
		}
		
	}

	@Override
	public void shutdown() {
		decoderHandles.forEach(handle -> STBVorbis.stb_vorbis_close(handle));
		decoderHandles.clear();
	}

}
