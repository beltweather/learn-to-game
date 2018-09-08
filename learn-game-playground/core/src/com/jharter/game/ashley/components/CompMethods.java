package com.jharter.game.ashley.components;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ashley.components.Components.ActivePlayerComp;
import com.jharter.game.ashley.components.Components.CardComp;
import com.jharter.game.ashley.components.Components.CursorComp;
import com.jharter.game.ashley.components.Components.PlayerComp;
import com.jharter.game.ashley.components.Components.SpriteComp;
import com.jharter.game.ashley.components.Components.TurnActionComp;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.Components.ZonePositionComp;
import com.jharter.game.ashley.components.subcomponents.TurnAction;
import com.jharter.game.util.ArrayUtil;
import com.jharter.game.util.id.ID;
import com.jharter.game.util.id.IDUtil;

import uk.co.carelesslabs.Enums.ZoneType;

/**
 * Class that links components, entities, and ids together. It is a sister class of C, the component mapper.
 */
public class CompMethods {
	
	CompMethods() {}
	
	public class CompLinkerZoneComp {
		
		private CompLinkerZoneComp() {}
		
		public ID getID(ID ownerID, ZoneType type) {
			return IDUtil.getZoneID(ownerID, type);
		}

		public ZoneComp get(Entity entity) {
			return get(Comp.ZonePositionComp.get(entity));
		}
		
		public ZoneComp get(ZonePositionComp zp) {
			Entity zone = Comp.Entity.get(zp.zoneID);
			return com.jharter.game.ashley.components.Comp.ZoneComp.get(zone);
		}
		
		public ZoneComp get(ID zoneID) {
			return Comp.ZoneComp.get(Comp.Entity.get(zoneID));
		}
		
		public ZoneComp get(ID ownerID, ZoneType zoneType) {
			ID zoneID = getID(ownerID, zoneType);
			if(zoneID == null) {
				return null;
			}
			Entity zone = Comp.Entity.get(zoneID);
			if(zone == null) {
				return null;
			}
			return com.jharter.game.ashley.components.Comp.ZoneComp.get(zone);
		}
		
		public boolean has(ZonePositionComp zp) {
			return Comp.ZoneComp.has(Comp.Entity.get(zp.zoneID));
		}
		
	}
	
	public class CompLinkerCursorComp {
		
		private CompLinkerCursorComp() {}
		
		public TurnAction getTurnAction(CursorComp c) {
			if(c.turnActionEntityID == null) {
				return null;
			}
			TurnActionComp t = Comp.TurnActionComp.get(Comp.Entity.get(c.turnActionEntityID));
			if(t == null) {
				return null;
			}
			return t.turnAction;
		}		
		
		public ID getPlayerID(CursorComp c) {
			return Comp.Entity.TurnEntity.ActivePlayerComp().activePlayerID;
		}
		
		public boolean isValidTarget(Entity cursor) {
			return isValidTarget(cursor, Comp.ZonePositionComp.get(cursor).index);
		}
		
		public boolean isValidTarget(Entity cursor, int index) {
			CursorComp c = Comp.CursorComp.get(cursor);
			return hasValidTarget(CursorComp.getPlayerID(c), ZoneComp.get(cursor).zoneType, CursorComp.getTurnAction(c), index, 0, 0);
		}
		
		public int findFirstValidTargetInZone(ID ownerID, ZoneType zoneType, TurnAction t) {
			return findNextValidTarget(ownerID, zoneType, t, -1, 1, 0);
		}
		
		public int findNextValidTargetInZone(ID ownerID, ZoneType zoneType, TurnAction t, int index, int direction) {
			return findNextValidTarget(ownerID, zoneType, t, index, direction, 0);
		}
		
		private boolean hasValidTarget(ID ownerID, ZoneType zoneType, TurnAction t, int index, int direction, int depth) {
			return findNextValidTarget(ownerID, zoneType, t, index, direction, depth) >= 0;
		}
		
		private int findNextValidTarget(ID ownerID, ZoneType zoneType, TurnAction t, int index, int direction, int depth) {
			ZoneComp z = ZoneComp.get(ownerID, zoneType);
			for(int i = 0; i < z.objectIDs.size(); i++) {
				index = ArrayUtil.findNextIndex(index, direction, z.objectIDs.size());
				if(!z.hasIndex(index)) {
					return -1;
				}
				Entity entity = Comp.Entity.get(z.objectIDs.get(index));
				if(entity != null && (t == null || t.isValidTarget(entity))) {
					if(t == null) {
						TurnActionComp taComp = Comp.TurnActionComp.get(entity);
						if(taComp != null) {
							TurnAction ta = taComp.turnAction;
							ZoneType nextZoneType = ta.getNextTargetZoneType(depth);
							if(nextZoneType == ZoneType.NONE || hasValidTarget(ownerID, nextZoneType, ta, 0, 1, depth+1)) {
								return index;
							}
						}
					} else {
						return index;
					}
				}
				if(direction == 0) {
					break;
				}
			}			
			return -1;
		}
		
	}
	
	public class CompLinkerTurnActionComp {
		
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
			return Comp.Entity.get(getActionTargetID(t));
		}
		
		public Entity getActionTargetEntity(TurnActionComp t, int index) {
			return Comp.Entity.get(getActionTargetID(t, index));
		}
		
		public SpriteComp getActionTargetSprite(TurnActionComp t) {
			return Comp.SpriteComp.get(getActionTargetEntity(t));
		}
		
		public SpriteComp getActionTargetSprite(TurnActionComp t, int index) {
			return Comp.SpriteComp.get(getActionTargetEntity(t, index));
		}
		
	}
	
	public class CompLinkerCardComp {
		
		private CompLinkerCardComp() {}
		
		public ID getPlayerID(CardComp c) {
			return c.playerID;
		}
		
		public Entity getPlayerEntity(CardComp c) {
			return Comp.Entity.get(getPlayerID(c));
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
	
	public class CompLinkerPlayerComp {
		
		private CompLinkerPlayerComp() {}
		
		public ID getBattleAvatarID(PlayerComp p) {
			return p.battleAvatarID;
		}
		
		public Entity getBattleAvatarEntity(PlayerComp p) {
			return Comp.Entity.get(getBattleAvatarID(p));
		}
		
		public SpriteComp getBattleAvatarSpriteComp(PlayerComp p) {
			return Comp.SpriteComp.get(getBattleAvatarEntity(p));
		}
		
	}
	
	public class CompLinkerActivePlayerComp {
		
		private CompLinkerActivePlayerComp() {}
		
		public ID getBattleAvatarID(ActivePlayerComp p) {
			return PlayerComp.getBattleAvatarID(Comp.PlayerComp.get(Comp.Entity.get(p.activePlayerID)));
		}
		
		public Entity getBattleAvatarEntity(ActivePlayerComp p) {
			return PlayerComp.getBattleAvatarEntity(Comp.PlayerComp.get(Comp.Entity.get(p.activePlayerID)));
		}
	
		public SpriteComp getBattleAvatarSpriteComp(ActivePlayerComp p) {
			return PlayerComp.getBattleAvatarSpriteComp(Comp.PlayerComp.get(Comp.Entity.get(p.activePlayerID)));
		}
		
		public void setPlayer(ActivePlayerComp p, int index) {
			if(!ArrayUtil.has(IDUtil.getPlayerIDs(), index)) {
				index = 0;
			}
			Comp.Entity.TurnEntity.ActivePlayerComp().activePlayerID = IDUtil.getPlayerIDs().get(index);
		}
		
		public boolean nextPlayer(ActivePlayerComp p) {
			return nextPlayer(p, false);
		}
		
		public boolean prevPlayer(ActivePlayerComp p) {
			return prevPlayer(p, false);
		}
		
		public boolean nextPlayer(ActivePlayerComp p, boolean includeSpent) {
			int i = IDUtil.getPlayerIDs().indexOf(p.activePlayerID, false);
			if(includeSpent) {
				Comp.Entity.TurnEntity.ActivePlayerComp().activePlayerID = IDUtil.getPlayerIDs().get(ArrayUtil.nextIndex(IDUtil.getPlayerIDs(), i));
				return true;
			}
			
			int counter = 0;
			while(counter < IDUtil.getPlayerIDs().size()) {
				i = ArrayUtil.nextIndex(IDUtil.getPlayerIDs(), i);
				ID playerID = IDUtil.getPlayerIDs().get(i);
				if(!Comp.Entity.TurnEntity.ActivePlayerComp().spentPlayers.contains(playerID, false)) {
					Comp.Entity.TurnEntity.ActivePlayerComp().activePlayerID = playerID;
					return true;
				}
				counter++;
			}
			return false;
		}
		
		public boolean prevPlayer(ActivePlayerComp p, boolean includeSpent) {
			int i = IDUtil.getPlayerIDs().indexOf(p.activePlayerID, false);
			if(includeSpent) {
				Comp.Entity.TurnEntity.ActivePlayerComp().activePlayerID = IDUtil.getPlayerIDs().get(ArrayUtil.prevIndex(IDUtil.getPlayerIDs(), i));
				return true;
			}
			
			int counter = 0;
			while(counter < IDUtil.getPlayerIDs().size()) {
				i = ArrayUtil.prevIndex(IDUtil.getPlayerIDs(), i);
				ID playerID = IDUtil.getPlayerIDs().get(i);
				if(!Comp.Entity.TurnEntity.ActivePlayerComp().spentPlayers.contains(playerID, false)) {
					Comp.Entity.TurnEntity.ActivePlayerComp().activePlayerID = playerID;
					return true;
				}
				counter++;
			}
			return false;
		}

	}
	
	public final CompLinkerCardComp CardComp = new CompLinkerCardComp();
	public final CompLinkerPlayerComp PlayerComp = new CompLinkerPlayerComp();
	public final CompLinkerActivePlayerComp ActivePlayerComp = new CompLinkerActivePlayerComp();
	public final CompLinkerTurnActionComp TurnActionComp = new CompLinkerTurnActionComp();
	public final CompLinkerCursorComp CursorComp = new CompLinkerCursorComp();
	public final CompLinkerZoneComp ZoneComp = new CompLinkerZoneComp();
	
}
