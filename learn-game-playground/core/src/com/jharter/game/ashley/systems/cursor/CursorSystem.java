package com.jharter.game.ashley.systems.cursor;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.jharter.game.ashley.components.Comp;
import com.jharter.game.ashley.components.Components.ActivePlayerComp;
import com.jharter.game.ashley.components.Components.AnimatingComp;
import com.jharter.game.ashley.components.Components.CursorComp;
import com.jharter.game.ashley.components.Components.DisabledComp;
import com.jharter.game.ashley.components.Components.InvisibleComp;
import com.jharter.game.ashley.components.subcomponents.TurnAction;
import com.jharter.game.ashley.systems.boilerplate.FirstSystem;
import com.jharter.game.util.ArrayUtil;
import com.jharter.game.util.id.ID;
import com.jharter.game.util.id.IDUtil;

public abstract class CursorSystem extends FirstSystem {
	
	private static Class<? extends Component>[] combine(Class<? extends Component>[] additionalComps, Class<? extends Component>...cursorComps) {
		Class<?>[] combined = new Class<?>[additionalComps.length + cursorComps.length];
		for(int i = 0; i < additionalComps.length; i++) {
			combined[i] = additionalComps[i];
		}
		for(int i = additionalComps.length; i < combined.length; i++) {
			combined[i] = cursorComps[i - additionalComps.length];
		}
		return (Class<? extends Component>[]) combined;
	}

	public CursorSystem(Class<? extends Component>... cursorComps) {
		this(Family.all(combine(cursorComps, CursorComp.class)).exclude(InvisibleComp.class, DisabledComp.class, AnimatingComp.class).get());
	}
	
	private CursorSystem(Family family) {
		super(family);
		add(ActivePlayerComp.class, Family.all(ActivePlayerComp.class).get());
	}
	
	@Override
	protected void processEntity(Entity cursor, float deltaTime) {
		processEntity(cursor, Comp.CursorComp.get(cursor), deltaTime);
	}
	
	protected abstract void processEntity(Entity cursor, CursorComp c, float deltaTime);
	
	protected ActivePlayerComp getActivePlayer() {
		return getFirstComponent(ActivePlayerComp.class);
	}
	
	protected ID getActivePlayerID() {
		ActivePlayerComp a = getActivePlayer();
		return a == null ? null : a.activePlayerID;
	}
	
	protected TurnAction getTurnAction(CursorComp c) {
		Entity taEntity = Comp.Entity.get(c.turnActionID);
		if(taEntity == null) {
			return null;
		}
		return Comp.TurnActionComp.get(taEntity).turnAction;
	}		
	
	protected boolean isDisabled(Entity cursor) {
		return Comp.DisabledComp.has(cursor);
	}
	
	protected boolean nextPlayer() {
		return changePlayer(true);
	}
	
	protected boolean prevPlayer() {
		return changePlayer(false);
	}
	
	protected boolean changePlayer(boolean next) {
		ActivePlayerComp a = getActivePlayer();
		int i = IDUtil.getPlayerIDs().indexOf(a.activePlayerID, false);
		int counter = 0;
		while(counter < IDUtil.getPlayerIDs().size()) {
			i = ArrayUtil.prevOrNextIndex(IDUtil.getPlayerIDs(), i, next);
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
