package backends.openal;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.EXTThreadLocalContext;
import org.lwjgl.openal.SOFTDirectChannels;
import org.lwjgl.system.MemoryUtil;

import com.esotericsoftware.minlog.Log;

import backends.AudioBackend;
import engine.Asset;
import engine.audio.AudioDecoder;
import engine.audio.AudioFormat;
import engine.audio.AudioStream;
import engine.audio.Music;
import engine.audio.SoundData;
import engine.audio.SoundEffect;
import engine.audio.VorbisDecoder;

public class ALAudioBackend implements AudioBackend {
	
	private long device, context;
	
	private ALMemory memory = new ALMemory();
	
	private Music backgroundMusic;
	
	private HashMap<AudioFormat, AudioDecoder> decoders = new HashMap<>();
	
	private FloatBuffer listenerPosition = BufferUtils.createFloatBuffer(3), 
						listenerVelocity = BufferUtils.createFloatBuffer(3), 
						listenerOrientation = BufferUtils.createFloatBuffer(6);
	
	private FloatBuffer sourcePosition = BufferUtils.createFloatBuffer(3);
	
	private boolean loopingMusic = false;
	
	private void checkError() {
		int error = AL10.alGetError();
		boolean hasErrors = false;
		while (error != AL10.AL_NO_ERROR) {
			Log.warn("OpenAL Error (" + error + ")");
			hasErrors = true;
			error = AL10.alGetError();
		}
		if (hasErrors) {
			throw new IllegalStateException("Encountered OpenAL Errors!");
		}
	}

	@Override
	public void setGain(float gain) {
		AL10.alListenerf(AL10.AL_GAIN, gain);
	}

	@Override
	public void setLooping(boolean looping) {
		loopingMusic = looping;
	}
	
	@Override
	public void createContext() {
		decoders.put(AudioFormat.VORBIS, new VorbisDecoder());
		device = ALC10.alcOpenDevice((ByteBuffer) null);
		if (device == MemoryUtil.NULL) {
			throw new IllegalStateException("Failed to open the default audio device.");
		}
		ALCCapabilities deviceCaps = ALC.createCapabilities(device);
		context = ALC10.alcCreateContext(device, (IntBuffer) null);
		if (context == MemoryUtil.NULL) {
			throw new IllegalStateException("Failed to create an OpenAL context.");
		}
		EXTThreadLocalContext.alcSetThreadContext(context);
		AL.createCapabilities(deviceCaps);
		listenerPosition.put(0, 0);
		listenerPosition.put(1, 0);
		listenerPosition.put(2, 0);
		listenerVelocity.put(0, 0);
		listenerVelocity.put(1, 0);
		listenerVelocity.put(2, 0);
		listenerOrientation.put(0, 0);
		listenerOrientation.put(1, 0);
		listenerOrientation.put(2, -1);
		listenerOrientation.put(3, 0);
		listenerOrientation.put(4, 1);
		listenerOrientation.put(5, 0);
		AL10.alListenerfv(AL10.AL_POSITION, listenerPosition);
		AL10.alListenerfv(AL10.AL_VELOCITY, listenerVelocity);
		AL10.alListenerfv(AL10.AL_ORIENTATION, listenerOrientation);
		checkError();
	}

	@Override
	public Music loadMusic(Asset stream, AudioFormat format) {
		AudioStream audio = decoders.get(format).openStream(stream);
		int source = AL10.alGenSources();
		int[] buffers = new int[] {
				AL10.alGenBuffers(),
				AL10.alGenBuffers(),
				AL10.alGenBuffers()
		};
		memory.sourceSet.add(source);
		for (int i = 0; i < buffers.length; i++) {
			memory.bufferSet.add(buffers[i]);
		}
		return new Music(new ALMusic(source, buffers), audio, audio.getLength());
	}

	@Override
	public SoundEffect loadSoundEffect(Asset stream, AudioFormat format) {
		SoundData data = decoders.get(format).decode(stream);
		int source = AL10.alGenSources();
		int buffer = AL10.alGenBuffers();
		if (AL.getCapabilities().AL_SOFT_direct_channels) {
			AL10.alSourcei(source, SOFTDirectChannels.AL_DIRECT_CHANNELS_SOFT, AL10.AL_TRUE);
		}
		memory.sourceSet.add(source);
		memory.bufferSet.add(buffer);
		AL10.alBufferData(buffer, data.getChannels() > 1 ? AL10.AL_FORMAT_STEREO16 : AL10.AL_FORMAT_MONO16, data.getPCM(), data.getSampleRate());
		AL10.alSourcei(source, AL10.AL_BUFFER, buffer);
		return new SoundEffect(new ALSoundEffect(source));
	}

	@Override
	public void setBackgroundMusic(Music music) {
		backgroundMusic = music;
		backgroundMusic.setPlayed(0);
		ALMusic alMusic = (ALMusic) music.getBackendData();
		if (AL.getCapabilities().AL_SOFT_direct_channels) {
			AL10.alSourcei(alMusic.getSource(), SOFTDirectChannels.AL_DIRECT_CHANNELS_SOFT, AL10.AL_TRUE);
		}
		AudioStream stream = music.getAudioStream();
		stream.rewind();
		int[] buffers = alMusic.getBuffers();
		for (int i = 0; i < buffers.length; i++) {
			if (!stream(buffers[i], stream)) { 
				throw new IllegalStateException("OpenAL failed to play this music " + music);
			}
		}
		AL10.alSourceQueueBuffers(alMusic.getSource(), buffers);
		AL10.alSourcePlay(alMusic.getSource());
	}
	
	public boolean isBackgroundMusicDonePlaying() {
		return backgroundMusic == null || backgroundMusic.isDonePlaying();
	}
	
	private boolean stream(int buffer, AudioStream stream) {
		if (stream.getData().getSamples() > 0 && stream.readData(AudioStream.BUFFER_SIZE, stream.getPCM())) {
			AL10.alBufferData(buffer, stream.getData().getChannels() > 1 ? AL10.AL_FORMAT_STEREO16 : AL10.AL_FORMAT_MONO16, stream.getPCM(), stream.getData().getSampleRate());
			stream.getData().usedSamples(stream.getSamplesUsed());
			return true;
		}
		return false;
	}
	
	private boolean update(int source, boolean loop, AudioStream stream) {
		int processed = AL10.alGetSourcei(source, AL10.AL_BUFFERS_PROCESSED);
		for (int i = 0; i < processed; i++) {
			int buffer = AL10.alSourceUnqueueBuffers(source);
			if (!stream(buffer, stream)) {
				if (loop) {
					stream.rewind();
					if (!stream(buffer, stream)) {
						return false;
					}
				} else {
					return false;
				}
			}
			AL10.alSourceQueueBuffers(source, buffer);
		}
		if (processed == 3) {
			AL10.alSourcePlay(source);
		}
		return true;
	}

	@Override
	public void playSoundEffect(SoundEffect effect) {
		ALSoundEffect backendEffect = (ALSoundEffect) effect.getBackendData();
		backendEffect.play();
		checkError();
	}

	@Override
	public void playSoundEffect(SoundEffect effect, Vector3f position) {
		ALSoundEffect backendEffect = (ALSoundEffect) effect.getBackendData();
		sourcePosition.put(0, position.x);
		sourcePosition.put(1, position.y);
		sourcePosition.put(2, position.z);
		AL10.alSourcefv(backendEffect.getSource(), AL10.AL_POSITION, sourcePosition);
		backendEffect.play();
		checkError();
	}

	@Override
	public void setListener(Vector3f position, Vector3f orientation, Vector3f velocity) {
		listenerPosition.put(0, position.x);
		listenerPosition.put(1, position.y);
		listenerPosition.put(2, position.z);
		AL10.alListenerfv(AL10.AL_POSITION, listenerPosition);
		listenerOrientation.put(0, orientation.x);
		listenerOrientation.put(1, orientation.y);
		listenerOrientation.put(2, orientation.z);
		AL10.alListenerfv(AL10.AL_ORIENTATION, listenerOrientation);
		listenerVelocity.put(0, velocity.x);
		listenerVelocity.put(1, velocity.y);
		listenerVelocity.put(2, velocity.z);
		AL10.alListenerfv(AL10.AL_VELOCITY, listenerVelocity);
	}
	
	@Override
	public void updateContext() {
		if (backgroundMusic != null) {
			ALMusic music = (ALMusic) backgroundMusic.getBackendData();
			if (!update(music.getSource(), loopingMusic, backgroundMusic.getAudioStream())) {
				throw new IllegalStateException("OpenAL could not replay this background music: " + backgroundMusic);
			}
			float samplesInTrack = backgroundMusic.getAudioStream().getData().getSampleRate() * backgroundMusic.getLength();
			float samplesLeft = backgroundMusic.getAudioStream().getData().getSamples();
			backgroundMusic.setPlayed(((samplesInTrack - samplesLeft) / samplesInTrack) / backgroundMusic.getAudioStream().getData().getSampleRate());
		}
		checkError();
	}
	
	@Override
	public void destroyContext() {
		decoders.values().forEach(decoder -> decoder.shutdown());
		memory.destroy();
		EXTThreadLocalContext.alcSetThreadContext(MemoryUtil.NULL);
		ALC10.alcDestroyContext(context);
		ALC10.alcCloseDevice(device);
	}

}
