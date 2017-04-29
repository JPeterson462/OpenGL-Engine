package utils;

import java.util.Iterator;

public class LockedIterator<T> implements Iterator<T> {
	
	private Iterator<T> internal;
	
	public LockedIterator(Iterator<T> iterator) {
		internal = iterator;
	}

	@Override
	public boolean hasNext() {
		return internal.hasNext();
	}

	@Override
	public T next() {
		return internal.next();
	}
	
}
