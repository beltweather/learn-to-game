package com.jharter.game.ashley.components.subcomponents;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ashley.components.Components.CardComp;
import com.jharter.game.ashley.components.Components.CursorComp;
import com.jharter.game.ashley.components.Components.PlayerComp;
import com.jharter.game.ashley.components.Components.SpriteComp;
import com.jharter.game.ashley.components.Components.TurnActionComp;
import com.jharter.game.ashley.components.M;
import com.jharter.game.util.id.ID;

public class CompLinker {
	
	private CompLinker() {}
	
	public static ID getPlayerID(CardComp c) {
		return c.playerID;
	}
	
	public static Entity getPlayerEntity(CardComp c) {
		return M.Entity.get(getPlayerID(c));
	}
	
	public static PlayerComp getPlayerComp(CardComp c) {
		return M.PlayerComp.get(getPlayerEntity(c));
	}

	public static ID getBattleAvatarID(CardComp c) {
		return getPlayerComp(c).battleAvatarID;
	}
	
	public static Entity getBattleAvatarEntity(CardComp c) {
		return getBattleAvatarEntity(getPlayerComp(c));
	}
	
	public static SpriteComp getBattleAvatarSpriteComp(CardComp c) {
		return getBattleAvatarSpriteComp(getPlayerComp(c));
	}
	
	public static ID getBattleAvatarID(PlayerComp p) {
		return p.battleAvatarID;
	}
	
	public static Entity getBattleAvatarEntity(PlayerComp p) {
		return M.Entity.get(getBattleAvatarID(p));
	}
	
	public static SpriteComp getBattleAvatarSpriteComp(PlayerComp p) {
		return M.SpriteComp.get(getBattleAvatarEntity(p));
	}
	
	public static ID getActionTargetID(TurnActionComp t) {
		return getActionTargetID(t, t.turnAction.targetIDs.size-1);
	}
	
	public static ID getActionTargetID(TurnActionComp t, int index) {
		if(index < 0 || index >= t.turnAction.targetIDs.size) {
			return null;
		}
		return t.turnAction.targetIDs.get(index);
	}
	
	public static Entity getActionTargetEntity(TurnActionComp t) {
		return M.Entity.get(getActionTargetID(t));
	}
	
	public static Entity getActionTargetEntity(TurnActionComp t, int index) {
		return M.Entity.get(getActionTargetID(t, index));
	}
	
	public static SpriteComp getActionTargetSprite(TurnActionComp t) {
		return M.SpriteComp.get(getActionTargetEntity(t));
	}
	
	public static SpriteComp getActionTargetSprite(TurnActionComp t, int index) {
		return M.SpriteComp.get(getActionTargetEntity(t, index));
	}
	
	public static TurnAction getTurnAction(CursorComp c) {
		if(c.turnActionEntityID == null) {
			return null;
		}
		TurnActionComp t = M.TurnActionComp.get(M.Entity.get(c.turnActionEntityID));
		if(t == null) {
			return null;
		}
		return t.turnAction;
	}

}
