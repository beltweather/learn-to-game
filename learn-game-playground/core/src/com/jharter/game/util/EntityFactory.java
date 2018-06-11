package com.jharter.game.util;

import com.badlogic.gdx.math.Vector3;
import com.jharter.game.util.id.ID;
import com.jharter.game.util.id.IDGenerator;

import uk.co.carelesslabs.box2d.Box2DWorld;
import uk.co.carelesslabs.entity.Hero;

public class EntityFactory {
	
	private EntityFactory() {}
	
	public static Hero newHero(Vector3 pos, Box2DWorld world) {
		return newHero(IDGenerator.newID(), pos, world);
	}
	
	public static Hero newHero(ID id, Vector3 pos, Box2DWorld world) {
		if(id == null) {
			id = IDGenerator.newID();
		}
		return new Hero(id, pos, world);
	}

}
