package com.jharter.game.stages.battlestage;

import com.badlogic.ashley.core.PooledEngine;
import com.jharter.game.ashley.components.EntityBuilder;
import com.jharter.game.ashley.components.Mapper;

public class TurnTimerHelper {
	
	private TurnTimerHelper() {}
	
	public static void addTurnTimer(PooledEngine engine, float maxTurnTimeSec) {
		EntityBuilder b = EntityBuilder.create(engine);
		b.IDComp().id = Mapper.getTurnEntityID();
		b.TurnTimerComp().maxTurnTimeSec = maxTurnTimeSec;
		b.TurnPhaseComp();
		b.TurnPhaseStartBattleComp();
		b.SpriteComp().position.x = 800;
		b.SpriteComp().position.y = -400;
		b.SpriteComp().width = 100;
		b.SpriteComp().height = 100;
		b.SpriteComp();
		engine.addEntity(b.Entity());
		b.free();
	}

}
