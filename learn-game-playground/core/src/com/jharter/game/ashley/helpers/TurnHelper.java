package com.jharter.game.ashley.helpers;

import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.entities.EntityBuilder;
import com.jharter.game.ashley.entities.EntityHandler;
import com.jharter.game.ashley.entities.IEntityHandler;
import com.jharter.game.render.TurnTimerRenderMethod;
import com.jharter.game.util.U;
import com.jharter.game.util.id.IDUtil;

public class TurnHelper extends EntityHandler {
	
	public TurnHelper(IEntityHandler handler) {
		super(handler);
	}

	public void addTurnEntity(ZoneComp infoZone, float maxTurnTimeSec) {
		EntityBuilder b = EntityBuilder.create(getEngine());
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
		b.ShapeRenderComp().renderMethod = new TurnTimerRenderMethod(this);
		getEngine().addEntity(b.Entity());
		b.free();
	}

}
