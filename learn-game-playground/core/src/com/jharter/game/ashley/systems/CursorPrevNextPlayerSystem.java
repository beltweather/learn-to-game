package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.jharter.game.ashley.components.Comp;
import com.jharter.game.ashley.components.Components.ActivePlayerComp;
import com.jharter.game.ashley.components.Components.AnimatingComp;
import com.jharter.game.ashley.components.Components.CursorComp;
import com.jharter.game.ashley.components.Components.CursorInputComp;
import com.jharter.game.ashley.components.Components.DisabledComp;
import com.jharter.game.ashley.components.Components.InvisibleComp;
import com.jharter.game.ashley.systems.boilerplate.FirstSystem;
import com.jharter.game.util.ArrayUtil;
import com.jharter.game.util.id.ID;
import com.jharter.game.util.id.IDUtil;

public class CursorPrevNextPlayerSystem extends FirstSystem {

	@SuppressWarnings("unchecked")
	public CursorPrevNextPlayerSystem() {
		super(Family.all(CursorComp.class, CursorInputComp.class).exclude(InvisibleComp.class, AnimatingComp.class, DisabledComp.class).get());
		add(ActivePlayerComp.class, Family.all(ActivePlayerComp.class).get());
	}

	@Override
	public void processEntity(Entity cursor, float deltaTime) {
		CursorInputComp ci = Comp.CursorInputComp.get(cursor);
		ActivePlayerComp a = getFirstComponent(ActivePlayerComp.class);
		
		if(ci.prev) {
			
			if(prevPlayer(a)) {
				Comp.Entity.Cursor(cursor).toHand(getEngine());
			}
			
		} else if(ci.next) {

			if(nextPlayer(a)) {
				Comp.Entity.Cursor(cursor).toHand(getEngine());
			}
			
		} 
	}
	
	private boolean nextPlayer(ActivePlayerComp a) {
		int i = IDUtil.getPlayerIDs().indexOf(a.activePlayerID, false);
		int counter = 0;
		while(counter < IDUtil.getPlayerIDs().size()) {
			i = ArrayUtil.nextIndex(IDUtil.getPlayerIDs(), i);
			ID playerID = IDUtil.getPlayerIDs().get(i);
			if(!Comp.Entity.DefaultTurn().ActivePlayerComp().spentPlayers.contains(playerID, false)) {
				Comp.Entity.DefaultTurn().ActivePlayerComp().activePlayerID = playerID;
				return true;
			}
			counter++;
		}
		return false;
	}
	
	private boolean prevPlayer(ActivePlayerComp a) {
		int i = IDUtil.getPlayerIDs().indexOf(a.activePlayerID, false);
		int counter = 0;
		while(counter < IDUtil.getPlayerIDs().size()) {
			i = ArrayUtil.prevIndex(IDUtil.getPlayerIDs(), i);
			ID playerID = IDUtil.getPlayerIDs().get(i);
			if(!Comp.Entity.DefaultTurn().ActivePlayerComp().spentPlayers.contains(playerID, false)) {
				Comp.Entity.DefaultTurn().ActivePlayerComp().activePlayerID = playerID;
				return true;
			}
			counter++;
		}
		return false;
	}
	
}

