package com.jharter.game.stages.battlestage;

import com.badlogic.ashley.core.PooledEngine;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.Comp;
import com.jharter.game.ashley.components.EntityBuilder;
import com.jharter.game.render.TurnTimerRenderMethod;
import com.jharter.game.util.U;
import com.jharter.game.util.id.IDUtil;

public class TurnHelper {
	
	private TurnHelper() {}
	
	public static void addTurnEntity(PooledEngine engine, ZoneComp infoZone, float maxTurnTimeSec) {
		EntityBuilder b = EntityBuilder.create(engine);
		b.IDComp().id = IDUtil.newID();
		b.TurnTimerComp().turnTimer.maxTurnTimeSec = maxTurnTimeSec;
		b.TurnPhaseComp();
		b.TurnPhaseStartBattleComp();
		b.ActivePlayerComp();
		b.SpriteComp().position.x = U.u12(65); //800;
		b.SpriteComp().position.y = U.u12(-35); //-400;
		b.SpriteComp().width = 100 / U.PIXELS_PER_UNIT;
		b.SpriteComp().height = 100 / U.PIXELS_PER_UNIT;
		b.SpriteComp();
		Comp.ZoneComp(infoZone).add(b);
		b.ShapeRenderComp().renderMethod = new TurnTimerRenderMethod();
		engine.addEntity(b.Entity());
		b.free();
	}

}
