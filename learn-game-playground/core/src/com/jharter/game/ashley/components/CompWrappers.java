package com.jharter.game.ashley.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.ObjectMap;
import com.jharter.game.ashley.components.Components.ActivePlayerComp;
import com.jharter.game.ashley.components.Components.CardComp;
import com.jharter.game.ashley.components.Components.PlayerComp;
import com.jharter.game.ashley.components.Components.SpriteComp;
import com.jharter.game.ashley.components.Components.TurnActionComp;
import com.jharter.game.util.ArrayUtil;
import com.jharter.game.util.id.ID;
import com.jharter.game.util.id.IDUtil;

/**
 * Class that links components, entities, and ids together. It is a sister class of C, the component mapper.
 */
public class CompWrappers {
	
	private final CompWrapperPlayerComp CompLinkerPlayerComp = new CompWrapperPlayerComp();
	public CompWrapperPlayerComp PlayerComp(PlayerComp p) {
		CompLinkerPlayerComp.setComponent(p);
		return CompLinkerPlayerComp;
	}
	
	private final CompWrapperCardComp CompLinkerCardComp = new CompWrapperCardComp();
	public CompWrapperCardComp CardComp(CardComp c) {
		CompLinkerCardComp.setComponent(c);
		return CompLinkerCardComp;
	}
	
	private final CompWrapperTurnActionComp CompLinkerTurnActionComp = new CompWrapperTurnActionComp();
	public CompWrapperTurnActionComp TurnActionComp(TurnActionComp t) {
		CompLinkerTurnActionComp.setComponent(t);
		return CompLinkerTurnActionComp;
	}
	
	private final CompWrapperActivePlayerComp CompLinkerActivePlayerComp = new CompWrapperActivePlayerComp();
	public CompWrapperActivePlayerComp ActivePlayerComp(ActivePlayerComp a) {
		CompLinkerActivePlayerComp.setComponent(a);
		return CompLinkerActivePlayerComp;
	}
	
	CompWrappers() {}
	
	public class CompWrapper<T extends Component> {
		
		private T comp;
		
		private CompWrapper() {}
		
		void setComponent(T comp) {
			this.comp = comp;
		}
		
		protected T Comp() {
			return comp;
		}
		
	}
	
	/**
	 * Mark as deprecated until something actually needs to use this. There's nothing
	 * wrong with this class, it's just not referenced anywhere right now.
	 */
	@Deprecated
	public class CompWrapperTurnActionComp extends CompWrapper<TurnActionComp> {
		
		private CompWrapperTurnActionComp() {}
		
		public ID getActionTargetID() {
			return getActionTargetID(Comp().turnAction.targetIDs.size-1);
		}
		
		public ID getActionTargetID(int index) {
			if(index < 0 || index >= Comp().turnAction.targetIDs.size) {
				return null;
			}
			return Comp().turnAction.targetIDs.get(index);
		}
		
		public Entity getActionTargetEntity() {
			return Comp.Entity.get(getActionTargetID());
		}
		
		public Entity getActionTargetEntity(int index) {
			return Comp.Entity.get(getActionTargetID(index));
		}
		
		public SpriteComp getActionTargetSprite() {
			return Comp.SpriteComp.get(getActionTargetEntity());
		}
		
		public SpriteComp getActionTargetSprite(int index) {
			return Comp.SpriteComp.get(getActionTargetEntity(index));
		}
		
	}
	
	public class CompWrapperCardComp extends CompWrapper<CardComp> {
		
		private CompWrapperCardComp() {}
		
		public ID getPlayerID() {
			return Comp().playerID;
		}
		
		public Entity getPlayerEntity() {
			return Comp.Entity.get(getPlayerID());
		}
		
		public PlayerComp getPlayerComp() {
			return Comp.PlayerComp.get(getPlayerEntity());
		}

		public ID getBattleAvatarID() {
			return getPlayerComp().battleAvatarID;
		}
		
		public Entity getBattleAvatarEntity() {
			return PlayerComp(getPlayerComp()).getBattleAvatarEntity();
		}
		
		public SpriteComp getBattleAvatarSpriteComp() {
			return PlayerComp(getPlayerComp()).getBattleAvatarSpriteComp();
		}
		
	}
	
	public class CompWrapperPlayerComp extends CompWrapper<PlayerComp> {
		
		private CompWrapperPlayerComp() {}
		
		public ID getBattleAvatarID() {
			return Comp().battleAvatarID;
		}
		
		public Entity getBattleAvatarEntity() {
			return Comp.Entity.get(getBattleAvatarID());
		}
		
		public SpriteComp getBattleAvatarSpriteComp() {
			return Comp.SpriteComp.get(getBattleAvatarEntity());
		}
		
	}
	
	public class CompWrapperActivePlayerComp extends CompWrapper<ActivePlayerComp> {
		
		private CompWrapperActivePlayerComp() {}
		
		public PlayerComp getPlayerComp() {
			return Comp.PlayerComp.get(Comp.Entity.get(Comp().activePlayerID));
		}
		
		public ID getBattleAvatarID() {
			return PlayerComp(getPlayerComp()).getBattleAvatarID();
		}
		
		public Entity getBattleAvatarEntity() {
			return PlayerComp(getPlayerComp()).getBattleAvatarEntity();
		}
	
		public SpriteComp getBattleAvatarSpriteComp() {
			return PlayerComp(getPlayerComp()).getBattleAvatarSpriteComp();
		}
		
		public void setPlayer(int index) {
			if(!ArrayUtil.has(IDUtil.getPlayerIDs(), index)) {
				index = 0;
			}
			Comp.Entity.DefaultTurn().ActivePlayerComp().activePlayerID = IDUtil.getPlayerIDs().get(index);
		}
		
		public boolean nextPlayer() {
			return nextPlayer(false);
		}
		
		public boolean prevPlayer() {
			return prevPlayer(false);
		}
		
		public boolean nextPlayer(boolean includeSpent) {
			int i = IDUtil.getPlayerIDs().indexOf(Comp().activePlayerID, false);
			if(includeSpent) {
				Comp.Entity.DefaultTurn().ActivePlayerComp().activePlayerID = IDUtil.getPlayerIDs().get(ArrayUtil.nextIndex(IDUtil.getPlayerIDs(), i));
				return true;
			}
			
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
		
		public boolean prevPlayer(boolean includeSpent) {
			int i = IDUtil.getPlayerIDs().indexOf(Comp().activePlayerID, false);
			if(includeSpent) {
				Comp.Entity.DefaultTurn().ActivePlayerComp().activePlayerID = IDUtil.getPlayerIDs().get(ArrayUtil.prevIndex(IDUtil.getPlayerIDs(), i));
				return true;
			}
			
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
	
}
