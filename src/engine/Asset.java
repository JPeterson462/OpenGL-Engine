package engine;

import java.io.InputStream;
import java.io.OutputStream;

public interface Asset {
	
	public InputStream read();
	
	public OutputStream write();
	
	public Asset getRelative(String path);
	
	public Type getType();
	
	public String getExtension();
	
	public enum Type {
		
		LOCAL,
		
		REMOTE,
		
		UNKNOWN
		
	}

}
