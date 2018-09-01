package com.jharter.game.ashley.components.subcomponents;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.jharter.game.ashley.components.Components.CardComp;
import com.jharter.game.ashley.components.Components.CursorComp;
import com.jharter.game.ashley.components.Components.PlayerComp;
import com.jharter.game.ashley.components.Components.SpriteComp;
import com.jharter.game.ashley.components.Components.TurnActionComp;
import com.jharter.game.ashley.components.Components.TurnPhaseComp;
import com.jharter.game.ashley.components.Components.TurnPhaseEndBattleComp;
import com.jharter.game.ashley.components.Components.TurnPhaseEndTurnComp;
import com.jharter.game.ashley.components.Components.TurnPhaseNoneComp;
import com.jharter.game.ashley.components.Components.TurnPhasePerformEnemyActionsComp;
import com.jharter.game.ashley.components.Components.TurnPhasePerformFriendActionsComp;
import com.jharter.game.ashley.components.Components.TurnPhaseSelectEnemyActionsComp;
import com.jharter.game.ashley.components.Components.TurnPhaseSelectFriendActionsComp;
import com.jharter.game.ashley.components.Components.TurnPhaseStartBattleComp;
import com.jharter.game.ashley.components.Components.TurnPhaseStartTurnComp;
import com.jharter.game.ashley.components.Components.TurnTimerComp;
import com.jharter.game.ashley.components.M;
import com.jharter.game.util.id.ID;
import com.jharter.game.util.id.IDUtil;

public class CompLinker {
	
	private CompLinker() {}
	
	public static class CompLinkerCursorComp {
		
		private CompLinkerCursorComp() {}
		
		public TurnAction getTurnAction(CursorComp c) {
			if(c.turnActionEntityID == null) {
				return null;
			}
			TurnActionComp t = M.TurnActionComp.get(M.Entity.get(c.turnActionEntityID));
			if(t == null) {
				return null;
			}
			return t.turnAction;
		}		
		
		public ID getPlayerID() {
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
			return M.Entity.get(getActionTargetID(t));
		}
		
		public Entity getActionTargetEntity(TurnActionComp t, int index) {
			return M.Entity.get(getActionTargetID(t, index));
		}
		
		public SpriteComp getActionTargetSprite(TurnActionComp t) {
			return M.SpriteComp.get(getActionTargetEntity(t));
		}
		
		public SpriteComp getActionTargetSprite(TurnActionComp t, int index) {
			return M.SpriteComp.get(getActionTargetEntity(t, index));
		}
		
	}
	
	public static class CompLinkerCardComp {
		
		private CompLinkerCardComp() {}
		
		public ID getPlayerID(CardComp c) {
			return c.playerID;
		}
		
		public Entity getPlayerEntity(CardComp c) {
			return M.Entity.get(getPlayerID(c));
		}
		
		public PlayerComp getPlayerComp(CardComp c) {
			return M.PlayerComp.get(getPlayerEntity(c));
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
	
	public static class CompLinkerTurnEntity {
		
		private CompLinkerTurnEntity() {}
		
		public Entity Entity() {
			return M.Entity.get(IDUtil.getTurnEntityID());
		}
		
		public TurnTimerComp TurnTimerComp() {
			return M.TurnTimerComp.get(Entity());
		}
		
		public TurnPhaseComp TurnPhaseComp() {
			return M.TurnPhaseComp.get(Entity());
		}
		
		public boolean isTurnPhaseStartBattle() { return ComponentMapper.getFor(TurnPhaseStartBattleComp.class).has(Entity()); }
		public boolean isTurnPhaseStartTurn() { return ComponentMapper.getFor(TurnPhaseStartTurnComp.class).has(Entity()); }
		public boolean isTurnPhaseSelectEnemyActions() { return ComponentMapper.getFor(TurnPhaseSelectEnemyActionsComp.class).has(Entity()); }
		public boolean isTurnPhaseSelectFriendActions() { return ComponentMapper.getFor(TurnPhaseSelectFriendActionsComp.class).has(Entity()); }
		public boolean isTurnPhasePerformFriendActions() { return ComponentMapper.getFor(TurnPhasePerformFriendActionsComp.class).has(Entity()); }
		public boolean isTurnPhasePerformEnemyActions() { return ComponentMapper.getFor(TurnPhasePerformEnemyActionsComp.class).has(Entity()); }
		public boolean isTurnPhaseEndTurn() { return ComponentMapper.getFor(TurnPhaseEndTurnComp.class).has(Entity()); }
		public boolean isTurnPhaseEndBattle() { return ComponentMapper.getFor(TurnPhaseEndBattleComp.class).has(Entity()); }
		public boolean isTurnPhaseNone() { return ComponentMapper.getFor(TurnPhaseNoneComp.class).has(Entity()); }
		
	}
	
	public static class CompLinkerPlayerComp {
		
		private CompLinkerPlayerComp() {}
		
		public ID getBattleAvatarID(PlayerComp p) {
			return p.battleAvatarID;
		}
		
		public Entity getBattleAvatarEntity(PlayerComp p) {
			return M.Entity.get(getBattleAvatarID(p));
		}
		
		public SpriteComp getBattleAvatarSpriteComp(PlayerComp p) {
			return M.SpriteComp.get(getBattleAvatarEntity(p));
		}
		
	}
	
	public static final CompLinkerTurnEntity TurnEntity = new CompLinkerTurnEntity();
	public static final CompLinkerCardComp CardComp = new CompLinkerCardComp();
	public static final CompLinkerPlayerComp PlayerComp = new CompLinkerPlayerComp();
	public static final CompLinkerTurnActionComp TurnActionComp = new CompLinkerTurnActionComp();
	public static final CompLinkerCursorComp CursorComp = new CompLinkerCursorComp();

}
