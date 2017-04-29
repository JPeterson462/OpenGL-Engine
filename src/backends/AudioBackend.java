package backends;

import java.io.InputStream;

import org.joml.Vector3f;

import engine.audio.AudioFormat;
import engine.audio.Music;
import engine.audio.SoundEffect;

public interface AudioBackend {
	
	public void createContext();

	public Music loadMusic(InputStream source);
	
	public SoundEffect loadSoundEffect(InputStream source, AudioFormat format);
	
	public void setBackgroundMusic(Music music);
	
	public void playBackgroundMusic();
	
	public void stopBackgroundMusic();
	
	public void playSoundEffect(SoundEffect effect);
	
	public void playSoundEffect(SoundEffect effect, Vector3f position);
	
	public void setListenerPosition(Vector3f position);
	
	public void destroyContext();

}
