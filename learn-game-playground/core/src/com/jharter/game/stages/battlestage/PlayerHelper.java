package com.jharter.game.stages.battlestage;

import com.badlogic.ashley.core.PooledEngine;
import com.jharter.game.ashley.components.Components.PlayerComp;
import com.jharter.game.ashley.components.EntityBuilder;
import com.jharter.game.util.id.ID;

public class PlayerHelper {

	private PlayerHelper() {}
	
	public static PlayerComp addPlayer(PooledEngine engine, ID playerID) {
		EntityBuilder b = EntityBuilder.create(engine);
		b.IDComp().id = playerID;
		PlayerComp p = b.PlayerComp();
		engine.addEntity(b.Entity());
		b.free();
		return p;
	}
	
}
