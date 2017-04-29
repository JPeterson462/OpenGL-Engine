package utils;

import java.util.ArrayList;

public class InsertionSort {
	
	public static <T extends Comparable<T>> void sort(ArrayList<T> list) {
		for (int i = 1; i < list.size(); i++) {
			T t = list.get(i);
			int j = i - 1;
			while (j > 0 && list.get(j).compareTo(t) > 0) {
				list.set(j + 1, list.get(j));
				j = j - 1;
			}
			list.set(j + 1, t);
		}
	}

}
