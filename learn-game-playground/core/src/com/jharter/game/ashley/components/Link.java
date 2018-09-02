package com.jharter.game.ashley.components;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ashley.components.Components.CardComp;
import com.jharter.game.ashley.components.Components.CursorComp;
import com.jharter.game.ashley.components.Components.PlayerComp;
import com.jharter.game.ashley.components.Components.SpriteComp;
import com.jharter.game.ashley.components.Components.TurnActionComp;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.Components.ZonePositionComp;
import com.jharter.game.ashley.components.subcomponents.TurnAction;
import com.jharter.game.util.id.ID;
import com.jharter.game.util.id.IDUtil;

import uk.co.carelesslabs.Enums.ZoneType;

/**
 * Class that links components, entities, and ids together. It is a sister class of C, the component mapper.
 */
public class Link {
	
	private Link() {}
	
	public static class CompLinkerZoneComp {
		
		private CompLinkerZoneComp() {}
		
		public ID getID(ID ownerID, ZoneType type) {
			return IDUtil.getZoneID(ownerID, type);
		}

		public ZoneComp get(ZonePositionComp zp) {
			Entity zone = Ent.Entity.get(zp.zoneID);
			return com.jharter.game.ashley.components.Comp.ZoneComp.get(zone);
		}
		
		public ZoneComp get(ID zoneID) {
			return Comp.ZoneComp.get(Ent.Entity.get(zoneID));
		}
		
		public ZoneComp get(ID ownerID, ZoneType zoneType) {
			ID zoneID = getID(ownerID, zoneType);
			if(zoneID == null) {
				return null;
			}
			Entity zone = Ent.Entity.get(zoneID);
			if(zone == null) {
				return null;
			}
			return com.jharter.game.ashley.components.Comp.ZoneComp.get(zone);
		}
		
		public boolean has(ZonePositionComp zp) {
			return Comp.ZoneComp.has(Ent.Entity.get(zp.zoneID));
		}
		
	}
	
	public static class CompLinkerCursorComp {
		
		private CompLinkerCursorComp() {}
		
		public TurnAction getTurnAction(CursorComp c) {
			if(c.turnActionEntityID == null) {
				return null;
			}
			TurnActionComp t = Comp.TurnActionComp.get(Ent.Entity.get(c.turnActionEntityID));
			if(t == null) {
				return null;
			}
			return t.turnAction;
		}		
		
		public ID getPlayerID(CursorComp c) {
			return IDUtil.getPlayerEntityID();
		}
		
	}
	
	public static class CompLinkerTurnActionComp {
		
		private CompLinkerTurnActionComp() {}
		
		public ID getActionTargetID(TurnActionComp t) {
			return getActionTargetID(t, t.turnAction.targetIDs.size-1);
		}
		
		public ID getActionTargetID(TurnActionComp t, int index) {
			if(index < 0 || index >= t.turnAction.targetIDs.size) {
				return null;
			}
			return t.turnAction.targetIDs.get(index);
		}
		
		public Entity getActionTargetEntity(TurnActionComp t) {
			return Ent.Entity.get(getActionTargetID(t));
		}
		
		public Entity getActionTargetEntity(TurnActionComp t, int index) {
			return Ent.Entity.get(getActionTargetID(t, index));
		}
		
		public SpriteComp getActionTargetSprite(TurnActionComp t) {
			return Comp.SpriteComp.get(getActionTargetEntity(t));
		}
		
		public SpriteComp getActionTargetSprite(TurnActionComp t, int index) {
			return Comp.SpriteComp.get(getActionTargetEntity(t, index));
		}
		
	}
	
	public static class CompLinkerCardComp {
		
		private CompLinkerCardComp() {}
		
		public ID getPlayerID(CardComp c) {
			return c.playerID;
		}
		
		public Entity getPlayerEntity(CardComp c) {
			return Ent.Entity.get(getPlayerID(c));
		}
		
		public PlayerComp getPlayerComp(CardComp c) {
			return Comp.PlayerComp.get(getPlayerEntity(c));
		}

		public ID getBattleAvatarID(CardComp c) {
			return getPlayerComp(c).battleAvatarID;
		}
		
		public Entity getBattleAvatarEntity(CardComp c) {
			return PlayerComp.getBattleAvatarEntity(getPlayerComp(c));
		}
		
		public SpriteComp getBattleAvatarSpriteComp(CardComp c) {
			return PlayerComp.getBattleAvatarSpriteComp(getPlayerComp(c));
		}
		
	}
	
	public static class CompLinkerPlayerComp {
		
		private CompLinkerPlayerComp() {}
		
		public ID getBattleAvatarID(PlayerComp p) {
			return p.battleAvatarID;
		}
		
		public Entity getBattleAvatarEntity(PlayerComp p) {
			return Ent.Entity.get(getBattleAvatarID(p));
		}
		
		public SpriteComp getBattleAvatarSpriteComp(PlayerComp p) {
			return Comp.SpriteComp.get(getBattleAvatarEntity(p));
		}
		
	}
	
	public static final CompLinkerCardComp CardComp = new CompLinkerCardComp();
	public static final CompLinkerPlayerComp PlayerComp = new CompLinkerPlayerComp();
	public static final CompLinkerTurnActionComp TurnActionComp = new CompLinkerTurnActionComp();
	public static final CompLinkerCursorComp CursorComp = new CompLinkerCursorComp();
	public static final CompLinkerZoneComp ZoneComp = new CompLinkerZoneComp();
	
}
