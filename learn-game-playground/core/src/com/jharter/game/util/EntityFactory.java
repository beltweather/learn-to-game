package com.jharter.game.util;

import com.badlogic.gdx.math.Vector3;

import uk.co.carelesslabs.box2d.Box2DWorld;
import uk.co.carelesslabs.entity.Hero;

public class EntityFactory {
	
	private EntityFactory() {}
	
	public static Hero newHero(Vector3 pos, Box2DWorld world) {
		return newHero(IDUtil.newID(), pos, world);
	}
	
	public static Hero newHero(String id, Vector3 pos, Box2DWorld world) {
		if(id == null) {
			id = IDUtil.newID();
		}
		return new Hero(id, pos, world);
	}

}
