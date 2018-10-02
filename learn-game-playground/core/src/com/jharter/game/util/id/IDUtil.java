package com.jharter.game.util.id;

import java.util.UUID;

public class IDUtil {

	private IDUtil() {}

	public static ID newID() {
		return new ID(UUID.randomUUID());
	}
	
}
