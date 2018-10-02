package com.jharter.game.ecs.helpers;

import com.jharter.game.ecs.entities.EntityBuilder;
import com.jharter.game.ecs.entities.EntityHandler;
import com.jharter.game.ecs.entities.IEntityHandler;
import com.jharter.game.util.id.ID;
import com.jharter.game.util.id.IDUtil;

public class BattleHelper extends EntityHandler {
	
	public BattleHelper(IEntityHandler handler) {
		super(handler);
	}

	public void addBattle(ID activePlayerID) {
		EntityBuilder b = EntityBuilder.create(getEngine());
		b.IDComp().id = IDUtil.newID();
		//b.ActivePlayerComp().activePlayerID = activePlayerID;
		getEngine().addEntity(b.Entity());
		b.free();
	}
	
}
