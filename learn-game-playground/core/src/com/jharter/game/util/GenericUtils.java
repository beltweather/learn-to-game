package com.jharter.game.util;

public class GenericUtils {
	
	private GenericUtils() {}
	
	public static boolean safeEquals(Object a, Object b) {
		if(a == null && b == null) {
			return true;
		}
		if(a == null || b == null) {
			return false;
		}
		return a.equals(b);
	}

}
