package com.jharter.game.util.id;

import java.util.UUID;

import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import uk.co.carelesslabs.Enums.ZoneType;

public class IDUtil {

	private IDUtil() {}

	public static ID newID() {
		return new ID(UUID.randomUUID());
	}
	
}
