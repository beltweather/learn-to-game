package com.jharter.game.ashley.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.ObjectMap;
import com.jharter.game.ashley.components.Components.ActivePlayerComp;
import com.jharter.game.ashley.components.Components.CardComp;
import com.jharter.game.ashley.components.Components.CursorInputRegulatorComp;
import com.jharter.game.ashley.components.Components.PlayerComp;
import com.jharter.game.ashley.components.Components.SpriteComp;
import com.jharter.game.ashley.components.Components.TurnActionComp;
import com.jharter.game.ashley.components.Components.VitalsComp;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.Components.ZonePositionComp;
import com.jharter.game.util.ArrayUtil;
import com.jharter.game.util.id.ID;
import com.jharter.game.util.id.IDUtil;

/**
 * Class that links components, entities, and ids together. It is a sister class of C, the component mapper.
 */
public class CompWrappers {
	
	private final CompWrapperPlayerComp CompLinkerPlayerComp = new CompWrapperPlayerComp();
	public CompWrapperPlayerComp PlayerComp(PlayerComp comp) {
		if(!isAllowed(comp.getClass())) {
			return null;
		}
		CompLinkerPlayerComp.setComponent(comp);
		return CompLinkerPlayerComp;
	}
	
	private final CompWrapperCardComp CompLinkerCardComp = new CompWrapperCardComp();
	public CompWrapperCardComp CardComp(CardComp comp) {
		if(!isAllowed(comp.getClass())) {
			return null;
		}
		CompLinkerCardComp.setComponent(comp);
		return CompLinkerCardComp;
	}
	
	private final CompWrapperTurnActionComp CompLinkerTurnActionComp = new CompWrapperTurnActionComp();
	public CompWrapperTurnActionComp TurnActionComp(TurnActionComp comp) {
		if(!isAllowed(comp.getClass())) {
			return null;
		}
		CompLinkerTurnActionComp.setComponent(comp);
		return CompLinkerTurnActionComp;
	}
	
	private final CompWrapperActivePlayerComp CompLinkerActivePlayerComp = new CompWrapperActivePlayerComp();
	public CompWrapperActivePlayerComp ActivePlayerComp(ActivePlayerComp comp) {
		if(!isAllowed(comp.getClass())) {
			return null;
		}
		CompLinkerActivePlayerComp.setComponent(comp);
		return CompLinkerActivePlayerComp;
	}
	
	private final CompWrapperZoneComp CompLinkerZoneComp = new CompWrapperZoneComp();
	public CompWrapperZoneComp ZoneComp(ZoneComp comp) {
		if(!isAllowed(comp.getClass())) {
			return null;
		}
		CompLinkerZoneComp.setComponent(comp);
		return CompLinkerZoneComp;
	}
	
	private final CompWrapperZonePositionComp CompLinkerZonePositionComp = new CompWrapperZonePositionComp();
	public CompWrapperZonePositionComp ZonePositionComp(ZonePositionComp comp) {
		if(!isAllowed(comp.getClass())) {
			return null;
		}
		CompLinkerZonePositionComp.setComponent(comp);
		return CompLinkerZonePositionComp;
	}
	
	private final CompWrapperCursorInputRegulatorComp CompLinkerCursorInputRegulatorComp = new CompWrapperCursorInputRegulatorComp();
	public CompWrapperCursorInputRegulatorComp CursorInputRegulatorComp(CursorInputRegulatorComp comp) {
		if(!isAllowed(comp.getClass())) {
			return null;
		}
		CompLinkerCursorInputRegulatorComp.setComponent(comp);
		return CompLinkerCursorInputRegulatorComp;
	}
	
	private final CompWrapperVitalsComp CompLinkerVitalsComp = new CompWrapperVitalsComp();
	public CompWrapperVitalsComp VitalsComp(VitalsComp comp) {
		if(!isAllowed(comp.getClass())) {
			return null;
		}
		CompLinkerVitalsComp.setComponent(comp);
		return CompLinkerVitalsComp;
	}
	public CompWrapperVitalsComp VitalsComp(Entity entity) {
		return VitalsComp(Comp.VitalsComp.get(entity));
	}
	
	private ObjectMap<Class<? extends Component>, Boolean> permissionsByClass = new ObjectMap<Class<? extends Component>, Boolean>();
	
	CompWrappers() {}
	
	void clearPermissions() {
		permissionsByClass.clear();
	}
	
	void setPermissions(Class<? extends Component>... allowClasses) {
		for(Class<? extends Component> klass : allowClasses) {
			permissionsByClass.put(klass, true);
		}
	}
	
	boolean isAllowed(Class<? extends Component> klass) {
		if(permissionsByClass.size == 0) {
			return true;
		}
		if(!permissionsByClass.containsKey(klass)) {
			return false;
		}
		return permissionsByClass.get(klass);
	}
	
	public class CompWrapper<T extends Component> {
		
		private T comp;
		
		private CompWrapper() {}
		
		void setComponent(T comp) {
			this.comp = comp;
		}
		
		protected T comp() {
			return comp;
		}
		
	}
	
	public class CompWrapperVitalsComp extends CompWrapper<VitalsComp> {
		
		private CompWrapperVitalsComp() {}
		
		public void heal(int hp) {
			comp().health += hp;
			if(comp().health > comp().maxHealth) {
				comp().health = comp().maxHealth;
			}
		}
		
		public void damage(int hp) {
			comp().health -= hp;
			if(comp().health < 0) {
				comp().health = 0;
			}
		}
		
		public boolean isDead() {
			return comp().health == 0;
		}
		
		public boolean isNearDeath() {
			return comp().health <= comp().weakHealth && !isDead();
		}
		
	}
	
	public class CompWrapperCursorInputRegulatorComp extends CompWrapper<CursorInputRegulatorComp> {
		
		private CompWrapperCursorInputRegulatorComp() {}
	
		public boolean ignoreMovement(boolean moved, float deltaTime) {
			if(!moved) {
				comp().processedMove = false;
				comp().processedMoveDelta = 0;
				comp().maxProcessedMoveDelta = 0.2f;
				return true;
			} else if(moved && comp().processedMove) {
				comp().processedMoveDelta += deltaTime;
				if(comp().processedMoveDelta < comp().maxProcessedMoveDelta) {
					return true;
				} else if(comp().maxProcessedMoveDelta > 0.005f){
					comp().maxProcessedMoveDelta /= 1.5f;
				}
			}
			comp().processedMove = true;
			comp().processedMoveDelta = 0;
			return false;
		}
		
	}
	
	public class CompWrapperZonePositionComp extends CompWrapper<ZonePositionComp> {
		
		private CompWrapperZonePositionComp() {}
		
		public ZoneComp getZoneComp() {
			if(comp().zoneID == null) {
				return null;
			}
			return Comp.Find.ZoneComp.findZone(comp());
		}
		
		public void checkpoint(Engine engine) {
			comp().history.add(copyForHistory(engine));
		}
		
		public void undoCheckpoint() {
			if(comp().history.size == 0) {
				return;
			}
			comp().history.pop();
		}
		
		public boolean tryRevertToLastCheckpoint() {
			if(comp().history.size == 0) {
				return false;
			}
			ZonePositionComp copy = comp().history.pop();
			comp().zoneID = copy.zoneID;
			comp().index = copy.index;
			return true;
		}
		
		public void clearHistory() {
			comp().history.clear();
		}
		
		private ZonePositionComp copyForHistory(Engine engine) {
			ZonePositionComp zp = Comp.create(engine, ZonePositionComp.class);
			zp.zoneID = comp().zoneID;
			zp.index = comp().index;
			// Intentionally ignoring history for copies since we don't use it
			return zp;
		}
		
	}
	
	public class CompWrapperZoneComp extends CompWrapper<ZoneComp> {
		
		private CompWrapperZoneComp() {}
		
		public boolean hasIndex(int index) {
			return index >= 0 && index < comp().internalObjectIDs.size;
		}
		
		public void add(EntityBuilder b) {
			add(b.IDComp().id, b.ZonePositionComp());
		}
		
		public void add(ID id, ZonePositionComp zp) {
			comp().internalObjectIDs.add(id);
			if(zp != null) {
				zp.index = comp().internalObjectIDs.size;
				zp.zoneID = comp().zoneID;
			}
		}
		
		public void remove(ID id) {
			comp().internalObjectIDs.removeValue(id, false);
			for(int i = 0; i < comp().internalObjectIDs.size; i++) {
				ID oID = comp().internalObjectIDs.get(i);
				Entity obj = Comp.Entity.get(oID);
				ZonePositionComp zp = Comp.ZonePositionComp.get(obj);
				zp.index = i;
			}
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
			return getActionTargetID(comp().turnAction.targetIDs.size-1);
		}
		
		public ID getActionTargetID(int index) {
			if(index < 0 || index >= comp().turnAction.targetIDs.size) {
				return null;
			}
			return comp().turnAction.targetIDs.get(index);
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
			return comp().playerID;
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
			return comp().battleAvatarID;
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
			return Comp.PlayerComp.get(Comp.Entity.get(comp().activePlayerID));
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
			int i = IDUtil.getPlayerIDs().indexOf(comp().activePlayerID, false);
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
			int i = IDUtil.getPlayerIDs().indexOf(comp().activePlayerID, false);
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
