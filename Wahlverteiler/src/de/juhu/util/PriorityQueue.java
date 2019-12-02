package de.juhu.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class PriorityQueue<T extends Comparable<T>> implements List<T> {

	public ArrayList<T> list;

	public PriorityQueue() {
		this.list = new ArrayList<T>();
	}

	public PriorityQueue(int i) {
		this.list = new ArrayList<T>();
	}

	public T poll() {
		if (this.list == null || this.list.isEmpty())
			return null;

		return this.list.remove(0);
	}

	public T peek() {
		if (this.list == null || this.list.isEmpty())
			return null;

		return this.get(0);
	}

	@Override
	public int size() {
		return this.list.size();
	}

	@Override
	public boolean isEmpty() {
		return this.list.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return this.list.contains(o);
	}

	@Override
	public Iterator<T> iterator() {
		return this.list.iterator();
	}

	@Override
	public Object[] toArray() {
		return this.list.toArray();
	}

	@SuppressWarnings("hiding")
	@Override
	public <T> T[] toArray(T[] a) {
		return this.list.toArray(a);
	}

	@Override
	public boolean add(T e) {
		if (e == null)
			return false;
		if (this.list == null)
			return (this.list = new ArrayList<>()).add(e);
		if (this.list.isEmpty())
			return this.list.add(e);

		for (int i = 0; i < this.list.size(); i++) {
			if (e.compareTo(this.get(i)) > 0) {
				this.list.add(i, e);
				break;
			}
		}

		return true;
	}

	@Override
	public boolean remove(Object o) {
		return this.list.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return this.list.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		boolean isAdded = true;

		for (T t : c) {
			isAdded = this.add(t) && isAdded;
		}

		return isAdded;
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		return this.addAll(c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return this.list.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return this.list.retainAll(c);
	}

	@Override
	public void clear() {
		this.list.clear();
	}

	@Override
	public T get(int index) {
		return this.list.get(index);
	}

	@Override
	public T set(int index, T element) {
		return null;
	}

	@Override
	public void add(int index, T element) {
	}

	@Override
	public T remove(int index) {
		return this.list.remove(index);
	}

	@Override
	public int indexOf(Object o) {
		return this.list.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return this.list.lastIndexOf(o);
	}

	@Override
	public ListIterator<T> listIterator() {
		return this.list.listIterator();
	}

	@Override
	public ListIterator<T> listIterator(int index) {
		return this.list.listIterator(index);
	}

	@Override
	public List<T> subList(int fromIndex, int toIndex) {
		return this.list.subList(fromIndex, toIndex);
	}

}