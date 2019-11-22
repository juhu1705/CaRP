package de.juhu.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class MergeSort<T extends Comparable> implements Callable {

	private ArrayList<T> input;
	private ExecutorService pool;

	public MergeSort(ArrayList<T> input, ExecutorService pool) {
		this.input = input;

		this.pool = pool;
	}

	@Override
	public ArrayList<T> call() throws Exception {
		if (this.input.size() <= 1)
			return this.input;

		List<T> toSort1 = this.input.subList(0, this.input.size() / 2);
		List<T> toSort2 = this.input.subList(this.input.size() / 2, this.input.size());

		// Future<ArrayList<T>> fsorted1 = this.pool.submit(new MergeSort<T>(new
		// ArrayList<T>(toSort1), pool));
		Future<ArrayList<T>> fsorted2 = this.pool.submit(new MergeSort<T>(new ArrayList<T>(toSort2), pool));

		ArrayList<T> sorted1 = /** fsorted1.get() */
				this.mergeThreadless(new ArrayList<T>(toSort1), pool);
		ArrayList<T> sorted2 = fsorted2.get();
		// LOGGER.info((sorted1.size() + sorted2.size()) + "");
		ArrayList<T> sorted = new ArrayList<T>(this.input.size());

		while (!sorted1.isEmpty()) {
			if (sorted2.isEmpty())
				sorted.add(sorted1.remove(0));
			else if (sorted1.get(0).compareTo(sorted2.get(0)) < 0)
				sorted.add(sorted1.remove(0));
			else {
				while (!(sorted1.get(0).compareTo(sorted2.get(0)) < 0)) {
					sorted.add(sorted2.remove(0));
					if (sorted2.isEmpty())
						break;
				}
				sorted.add(sorted1.remove(0));
			}
		}
		sorted.addAll(sorted2);

		return sorted;
	}

	private ArrayList<T> mergeThreadless(ArrayList<T> input, ExecutorService pool) throws Exception {
		if (input.size() <= 1)
			return input;

		List<T> toSort1 = input.subList(0, input.size() / 2);
		List<T> toSort2 = input.subList(input.size() / 2, input.size());

		Future<ArrayList<T>> fsorted2 = pool.submit(new MergeSort<T>(new ArrayList<T>(toSort2), pool));

		ArrayList<T> sorted1 = this.mergeThreadless(new ArrayList<T>(toSort1), pool);
		ArrayList<T> sorted2 = fsorted2.get();
		// LOGGER.info((sorted1.size() + sorted2.size()) + "");
		ArrayList<T> sorted = new ArrayList<T>(input.size());

		while (!sorted1.isEmpty()) {
			if (sorted2.isEmpty())
				sorted.add(sorted1.remove(0));
			else if (sorted1.get(0).compareTo(sorted2.get(0)) < 0)
				sorted.add(sorted1.remove(0));
			else {
				while (!(sorted1.get(0).compareTo(sorted2.get(0)) < 0)) {
					sorted.add(sorted2.remove(0));
					if (sorted2.isEmpty())
						break;
				}
				sorted.add(sorted1.remove(0));
			}
		}
		sorted.addAll(sorted2);

		return sorted;
	}
}
