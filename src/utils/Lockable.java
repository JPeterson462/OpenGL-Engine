package utils;

import java.util.concurrent.atomic.AtomicBoolean;

public interface Lockable {
	
	public AtomicBoolean getLock();

}
