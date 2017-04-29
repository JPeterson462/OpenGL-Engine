package utils;

import java.util.ListIterator;

public class LockedListIterator<T> implements ListIterator<T> {
	
	private ListIterator<T> internal;
	
	public LockedListIterator(ListIterator<T> iterator) {
		internal = iterator;
	}

	@Override
	public void add(T e) {
		throw new LockException();
	}

	@Override
	public boolean hasNext() {
		return internal.hasNext();
	}

	@Override
	public boolean hasPrevious() {
		return internal.hasPrevious();
	}

	@Override
	public T next() {
		return internal.next();
	}

	@Override
	public int nextIndex() {
		return internal.nextIndex();
	}

	@Override
	public T previous() {
		return internal.previous();
	}

	@Override
	public int previousIndex() {
		return internal.previousIndex();
	}

	@Override
	public void remove() {
		throw new LockException();
	}

	@Override
	public void set(T e) {
		throw new LockException();
	}

}
