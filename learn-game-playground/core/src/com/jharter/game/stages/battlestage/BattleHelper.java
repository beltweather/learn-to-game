package com.jharter.game.stages.battlestage;

import com.badlogic.ashley.core.PooledEngine;
import com.jharter.game.ashley.components.EntityBuilder;
import com.jharter.game.util.id.ID;
import com.jharter.game.util.id.IDUtil;

public class BattleHelper {
	
	private BattleHelper() {}
	
	public static void addBattle(PooledEngine engine, ID activePlayerID) {
		EntityBuilder b = EntityBuilder.create(engine);
		b.IDComp().id = IDUtil.getBattleEntityID();
		b.ActivePlayerComp().activePlayerID = activePlayerID;
		engine.addEntity(b.Entity());
		b.free();
	}
	
}
