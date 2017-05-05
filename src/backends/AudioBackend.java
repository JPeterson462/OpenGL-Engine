package backends;

import org.joml.Vector3f;

import engine.Asset;
import engine.audio.AudioFormat;
import engine.audio.Music;
import engine.audio.SoundEffect;

public interface AudioBackend {
	
	public void setGain(float gain);
	
	public void setLooping(boolean looping);
	
	public void createContext();

	public Music loadMusic(Asset source, AudioFormat format);
	
	public SoundEffect loadSoundEffect(Asset source, AudioFormat format);
	
	public void setBackgroundMusic(Music music);
	
	public boolean isBackgroundMusicDonePlaying();
	
	public void playSoundEffect(SoundEffect effect);
	
	public void playSoundEffect(SoundEffect effect, Vector3f position);
	
	public void setListener(Vector3f position, Vector3f orientation, Vector3f velocity);
	
	public void updateContext();
	
	public void destroyContext();

}
