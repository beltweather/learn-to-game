package com.jharter.game.ashley.systems.cursor;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.jharter.game.ashley.components.Comp;
import com.jharter.game.ashley.components.Components.ActivePlayerComp;
import com.jharter.game.ashley.components.Components.CursorComp;
import com.jharter.game.ashley.components.Components.CursorInputComp;
import com.jharter.game.util.ArrayUtil;
import com.jharter.game.util.id.ID;
import com.jharter.game.util.id.IDUtil;

public class CursorPrevNextSystem extends CursorSystem {

	@SuppressWarnings("unchecked")
	public CursorPrevNextSystem() {
		super(CursorInputComp.class);
	}

	@Override
	public void processEntity(Entity cursor, float deltaTime) {
		CursorComp c = Comp.CursorComp.get(cursor);
		CursorInputComp ci = Comp.CursorInputComp.get(cursor);

		if(ci.prev) {
			
			if(prevPlayer()) {
				c.reset();
			}
			
		} else if(ci.next) {

			if(nextPlayer()) {
				c.reset();
			}
			
		} 
		
		ci.prev = false;
		ci.next = false;
	}
	
	private boolean nextPlayer() {
		ActivePlayerComp a = getActivePlayer();
		int i = IDUtil.getPlayerIDs().indexOf(a.activePlayerID, false);
		int counter = 0;
		while(counter < IDUtil.getPlayerIDs().size()) {
			i = ArrayUtil.nextIndex(IDUtil.getPlayerIDs(), i);
			ID playerID = IDUtil.getPlayerIDs().get(i);
			if(!a.spentPlayers.contains(playerID, false)) {
				a.activePlayerID = playerID;
				return true;
			}
			counter++;
		}
		return false;
	}
	
	private boolean prevPlayer() {
		ActivePlayerComp a = getActivePlayer();
		int i = IDUtil.getPlayerIDs().indexOf(a.activePlayerID, false);
		int counter = 0;
		while(counter < IDUtil.getPlayerIDs().size()) {
			i = ArrayUtil.prevIndex(IDUtil.getPlayerIDs(), i);
			ID playerID = IDUtil.getPlayerIDs().get(i);
			if(!a.spentPlayers.contains(playerID, false)) {
				a.activePlayerID = playerID;
				return true;
			}
			counter++;
		}
		return false;
	}
	
}

