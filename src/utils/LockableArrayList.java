package utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;
import java.util.concurrent.atomic.AtomicBoolean;

public class LockableArrayList<T> implements List<T>, RandomAccess, Cloneable, Serializable, Lockable {
	
	private static final long serialVersionUID = 1L;

	private ArrayList<T> internal;
	
	private AtomicBoolean lock = new AtomicBoolean(false);
	
	private final boolean subListBlocked;
	
	public LockableArrayList() {
		internal = new ArrayList<>();
		subListBlocked = false;
	}
	
	public LockableArrayList(ArrayList<T> list) {
		internal = list;
		subListBlocked = false;
	}
	
	public LockableArrayList(ArrayList<T> list, boolean subListBlocked) {
		internal = list;
		this.subListBlocked = subListBlocked;
	}
	
	public AtomicBoolean getLock() {
		return lock;
	}
	
	private void tryLockedOperation() {
		if (lock.get()) {
			throw new LockException(this);
		}
	}

	@Override
	public boolean add(T e) {
		tryLockedOperation();
		return internal.add(e);
	}

	@Override
	public void add(int index, T element) {
		tryLockedOperation();
		internal.add(index, element);
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		tryLockedOperation();
		return internal.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		tryLockedOperation();
		return internal.addAll(index, c);
	}

	@Override
	public void clear() {
		tryLockedOperation();
		internal.clear();
	}

	@Override
	public boolean contains(Object o) {
		return internal.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return internal.containsAll(c);
	}

	@Override
	public T get(int index) {
		return internal.get(index);
	}

	@Override
	public int indexOf(Object o) {
		return internal.indexOf(o);
	}

	@Override
	public boolean isEmpty() {
		return internal.isEmpty();
	}

	@Override
	public Iterator<T> iterator() {
		Iterator<T> iterator = internal.iterator();
		if (lock.get())
			return new LockedIterator<T>(iterator);
		return iterator;
	}

	@Override
	public int lastIndexOf(Object o) {
		return internal.lastIndexOf(o);
	}

	@Override
	public ListIterator<T> listIterator() {
		ListIterator<T> iterator = internal.listIterator();
		if (lock.get())
			return new LockedListIterator<T>(iterator);
		return iterator;
	}

	@Override
	public ListIterator<T> listIterator(int index) {
		ListIterator<T> iterator = internal.listIterator(index);
		if (lock.get())
			return new LockedListIterator<T>(iterator);
		return iterator;
	}

	@Override
	public boolean remove(Object o) {
		tryLockedOperation();
		return internal.remove(o);
	}

	@Override
	public T remove(int index) {
		tryLockedOperation();
		return internal.remove(index);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		tryLockedOperation();
		return internal.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		tryLockedOperation();
		return internal.retainAll(c);
	}

	@Override
	public T set(int index, T element) {
		tryLockedOperation();
		return internal.set(index, element);
	}

	@Override
	public int size() {
		return internal.size();
	}

	@Override
	public List<T> subList(int fromIndex, int toIndex) {
		if (subListBlocked) {
			tryLockedOperation();
		}
		return internal.subList(fromIndex, toIndex);
	}

	@Override
	public Object[] toArray() {
		return internal.toArray();
	}

	@SuppressWarnings("hiding")
	@Override
	public <T> T[] toArray(T[] a) {
		return internal.toArray(a);
	}

}
