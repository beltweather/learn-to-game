package com.jharter.game.util.id;

import java.util.UUID;

public class IDGenerator {

	private IDGenerator() {}
	
	public static ID newID() {
		return new ID(UUID.randomUUID());
	}
	
}
