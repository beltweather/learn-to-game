package com.jharter.game.util;

import java.util.UUID;

public class IDUtil {

	private IDUtil() {}
	
	public static String newID() {
		return UUID.randomUUID().toString();
	}
	
}
