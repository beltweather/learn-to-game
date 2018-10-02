package com.jharter.game.stages.battlestage;

import com.jharter.game.ashley.components.EntityBuilder;
import com.jharter.game.ashley.entities.EntityFactory;
import com.jharter.game.ashley.entities.IEntityFactory;
import com.jharter.game.util.id.ID;
import com.jharter.game.util.id.IDUtil;

public class BattleHelper extends EntityFactory {
	
	public BattleHelper(IEntityFactory factory) {
		super(factory);
	}

	public void addBattle(ID activePlayerID) {
		EntityBuilder b = EntityBuilder.create(getEngine());
		b.IDComp().id = IDUtil.newID();
		//b.ActivePlayerComp().activePlayerID = activePlayerID;
		getEngine().addEntity(b.Entity());
		b.free();
	}
	
}
