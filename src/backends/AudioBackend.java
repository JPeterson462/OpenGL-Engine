package backends;

import java.io.InputStream;

import org.joml.Vector3f;

import engine.audio.AudioFormat;
import engine.audio.Music;
import engine.audio.SoundEffect;

public interface AudioBackend {
	
	public void setGain(float gain);
	
	public void setLooping(boolean looping);
	
	public void createContext();

	public Music loadMusic(InputStream source, AudioFormat format);
	
	public SoundEffect loadSoundEffect(InputStream source, AudioFormat format);
	
	public void setBackgroundMusic(Music music);
	
	public boolean isBackgroundMusicDonePlaying();
	
	public void playSoundEffect(SoundEffect effect);
	
	public void playSoundEffect(SoundEffect effect, Vector3f position);
	
	public void setListener(Vector3f position, Vector3f orientation, Vector3f velocity);
	
	public void updateContext();
	
	public void destroyContext();

}
