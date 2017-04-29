package backends.openal;

import java.util.HashSet;

import org.lwjgl.openal.AL10;

public class ALMemory {
	
	public HashSet<Integer> bufferSet = new HashSet<>();
	
	public HashSet<Integer> sourceSet = new HashSet<>();
	
	public void destroy() {
		bufferSet.forEach(buffer -> AL10.alDeleteBuffers(buffer));
		sourceSet.forEach(source -> AL10.alDeleteSources(source));
		bufferSet.clear();
		sourceSet.clear();
	}

}
