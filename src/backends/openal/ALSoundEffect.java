package backends.openal;

import org.lwjgl.openal.AL10;

public class ALSoundEffect {
	
	private int source;
	
	public ALSoundEffect(int source) {
		this.source = source;
	}
	
	public void play() {
		AL10.alSourcePlay(source);
	}

	public int getSource() {
		return source;
	}

}
