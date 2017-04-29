package backends.openal;

import java.io.InputStream;
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
import org.lwjgl.system.MemoryUtil;

import com.esotericsoftware.minlog.Log;

import backends.AudioBackend;
import engine.audio.AudioDecoder;
import engine.audio.AudioFormat;
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
	}

	@Override
	public Music loadMusic(InputStream source) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SoundEffect loadSoundEffect(InputStream stream, AudioFormat format) {
		SoundData data = decoders.get(format).decode(stream);
		int source = AL10.alGenSources();
		int buffer = AL10.alGenBuffers();
		AL10.alBufferData(buffer, data.getChannels() > 1 ? AL10.AL_FORMAT_STEREO16 : AL10.AL_FORMAT_MONO16, data.getPCM(), data.getSampleRate());
		AL10.alSourcei(source, AL10.AL_BUFFER, buffer);
		return new SoundEffect(new ALSoundEffect(source));
	}

	@Override
	public void setBackgroundMusic(Music music) {
		backgroundMusic = music;
		// reopen stream
	}

	@Override
	public void playBackgroundMusic() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stopBackgroundMusic() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void playSoundEffect(SoundEffect effect) {
		ALSoundEffect backendEffect = (ALSoundEffect) effect.getBackendData();
		backendEffect.play();
		checkError();
	}

	@Override
	public void playSoundEffect(SoundEffect effect, Vector3f position) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setListenerPosition(Vector3f position) {
		
	}
	
	@Override
	public void destroyContext() {
		memory.destroy();
		EXTThreadLocalContext.alcSetThreadContext(MemoryUtil.NULL);
		ALC10.alcDestroyContext(context);
		ALC10.alcCloseDevice(device);
	}

}
