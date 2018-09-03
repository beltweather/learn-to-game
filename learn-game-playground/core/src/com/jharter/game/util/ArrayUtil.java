package com.jharter.game.util;

import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;

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
	
}