package utils;

public class LockException extends RuntimeException {
	
	private static final long serialVersionUID = -4589223515981799628L;

	public LockException(Lockable structure) {
		super("This structure cannot be modified while locked: " + structure);
	}

	public LockException() {
		super("This structure cannot be modified while locked");
	}
	
}
