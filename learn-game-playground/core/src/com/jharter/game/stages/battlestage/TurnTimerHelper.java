package com.jharter.game.stages.battlestage;

import com.badlogic.ashley.core.PooledEngine;
import com.jharter.game.ashley.components.EntityBuilder;
import com.jharter.game.ashley.components.Mapper;
import com.jharter.game.util.Units;

public class TurnTimerHelper {
	
	private TurnTimerHelper() {}
	
	public static void addTurnTimer(PooledEngine engine, float maxTurnTimeSec) {
		EntityBuilder b = EntityBuilder.create(engine);
		b.IDComp().id = Mapper.getTurnEntityID();
		b.TurnTimerComp().maxTurnTimeSec = maxTurnTimeSec;
		b.TurnPhaseComp();
		b.TurnPhaseStartBattleComp();
		b.SpriteComp().position.x = Units.u12(65); //800;
		b.SpriteComp().position.y = Units.u12(-35); //-400;
		b.SpriteComp().width = 100 / Units.PIXELS_PER_UNIT;
		b.SpriteComp().height = 100 / Units.PIXELS_PER_UNIT;
		b.SpriteComp();
		engine.addEntity(b.Entity());
		b.free();
	}

}
