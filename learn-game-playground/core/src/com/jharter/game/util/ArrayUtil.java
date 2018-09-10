package com.jharter.game.util;

import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;
import com.jharter.game.ashley.components.Components.ZoneComp;

public class ArrayUtil {

	private ArrayUtil() {}
	
	public static boolean has(Array array, int index) {
		return index >= 0 && index < array.size;
	}
	
	public static boolean has(ImmutableArray array, int index) {
		return index >= 0 && index < array.size();
	}
	
	public static int nextIndex(Array array, int index) {
		if(index >= array.size - 1) {
			return 0;
		}
		return index + 1;
	}
	
	public static int prevIndex(Array array, int index) {
		if(index <= 0) {
			return array.size - 1;
		}
		return index - 1;
	}
	
	public static int nextIndex(ImmutableArray array, int index) {
		if(index >= array.size() - 1) {
			return 0;
		}
		return index + 1;
	}
	
	public static int prevIndex(ImmutableArray array, int index) {
		if(index <= 0) {
			return array.size() - 1;
		}
		return index - 1;
	}
	
	public static int findNextIndex(Array array, int currentIndex, int direction) {
		return findNextIndex(array.size, currentIndex, direction);
	}
	
	public static int findNextIndex(ImmutableArray array, int currentIndex, int direction) {
		return findNextIndex(array.size(), currentIndex, direction);
	}
	
	private static int findNextIndex(int size, int currentIndex, int direction) {
		if(direction == 0) {
			return currentIndex;
		}
		int index = currentIndex + direction;
		if(index < 0) {
			index = size - 1;
		} else if(index >= size) {
			index = 0;
		}
		return index;
	}
	
	public static interface IndexFinder<T> {
		
		public boolean isFound(T obj);
		
	}
	
	public static interface ImmutableIndexFinder<T> {
		
		public boolean isFound(T obj);
		
	}
	
	public static interface IndexFinderWithArgs<T> {
		
		public boolean isFound(T obj, Object...args);
		
	}
	
	public static interface ImmutableIndexFinderWithArgs<T> {
		
		public boolean isFound(T obj, Object...args);
		
	}
	
	public static <T> int findNextIndex(Array<T> array, int currentIndex, int direction, IndexFinder<T> indexFinder) {
		int index = currentIndex;
		for(int i = 0; i < array.size; i++) {
			index = findNextIndex(array, index, direction);
			if(indexFinder.isFound(array.get(index))) {
				return index;
			}
			if(direction == 0) {
				break;
			}
		}
		return -1;
	}
	
	public static <T> int findNextIndex(ImmutableArray<T> array, int currentIndex, int direction, ImmutableIndexFinder<T> indexFinder) {
		int index = currentIndex;
		for(int i = 0; i < array.size(); i++) {
			index = findNextIndex(array, index, direction);
			if(indexFinder.isFound(array.get(index))) {
				return index;
			}
			if(direction == 0) {
				break;
			}
		}
		return -1;
	}
	
	public static <T> int findNextIndex(Array<T> array, int currentIndex, int direction, IndexFinderWithArgs<T> indexFinder, Object...args) {
		int index = currentIndex;
		for(int i = 0; i < array.size; i++) {
			index = findNextIndex(array, index, direction);
			if(indexFinder.isFound(array.get(index), args)) {
				return index;
			}
			if(direction == 0) {
				break;
			}
		}
		return -1;
	}
	
	public static <T> int findNextIndex(ImmutableArray<T> array, int currentIndex, int direction, IndexFinderWithArgs<T> indexFinder, Object...args) {
		int index = currentIndex;
		for(int i = 0; i < array.size(); i++) {
			index = findNextIndex(array, index, direction);
			if(indexFinder.isFound(array.get(index), args)) {
				return index;
			}
			if(direction == 0) {
				break;
			}
		}
		return -1;
	}

}
