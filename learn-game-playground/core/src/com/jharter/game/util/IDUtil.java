package com.jharter.game.util;

import java.util.UUID;

public class IDUtil {

	private IDUtil() {}
	
	//private static int idGenerator = 0;
	
	public static String newID() {
		return UUID.randomUUID().toString();
	}
	
}
