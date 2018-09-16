package com.jharter.game.ashley.components;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.ObjectMap;
import com.jharter.game.ashley.components.Components.ActivePlayerComp;
import com.jharter.game.ashley.components.Components.CleanupTurnActionComp;
import com.jharter.game.ashley.components.Components.CursorComp;
import com.jharter.game.ashley.components.Components.CursorInputRegulatorComp;
import com.jharter.game.ashley.components.Components.PlayerComp;
import com.jharter.game.ashley.components.Components.SpriteComp;
import com.jharter.game.ashley.components.Components.TurnActionComp;
import com.jharter.game.ashley.components.Components.VitalsComp;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.Components.ZonePositionComp;
import com.jharter.game.ashley.components.subcomponents.TurnAction;
import com.jharter.game.util.ArrayUtil;
import com.jharter.game.util.id.ID;
import com.jharter.game.util.id.IDUtil;

/**
 * Class that links components, entities, and ids together. It is a sister class of C, the component mapper.
 */
public class CompWrappers {
	
	private ObjectMap<Class<?>, CompWrapper<?>> wrappersByClass = new ObjectMap<Class<?>, CompWrapper<?>>();
	private ObjectMap<Class<? extends Component>, Boolean> permissionsByClass = new ObjectMap<Class<? extends Component>, Boolean>();
	
	CompWrappers() {}
	
	public <W extends CompWrapper<C>, C extends Component> W get(Class<W> wrapperClass, C comp) {
		if(!isAllowed(comp.getClass())) {
			return null;
		}
		if(!wrappersByClass.containsKey(comp.getClass())) {
			try {
				Constructor c = wrapperClass.getDeclaredConstructors()[0];
				c.setAccessible(true);
				wrappersByClass.put(comp.getClass(), (W) c.newInstance());
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		W wrapper = (W) wrappersByClass.get(comp.getClass());
		wrapper.setComponent(comp);
		return wrapper;
	}
	
	public <W extends CompWrapper<C>, C extends Component> W get(Class<W> wrapperClass, Class<C> compClass, Entity entity) {
		return get(wrapperClass, Comp.getFor(compClass).get(entity));
	}
	
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
	
	private static class CompWrapper<T extends Component> {
		
		private T comp;
		
		void setComponent(T comp) {
			this.comp = comp;
		}
		
		protected T comp() {
			return comp;
		}
		
	}
	
	// ----------------------- BEGIN COMP WRAPPERS ----------------------------
	
	public static class CompWrapperSpriteComp extends CompWrapper<SpriteComp> {
		
		private CompWrapperSpriteComp() {}
		
		public float scaledWidth() {
			return scaledWidth(comp().scale.x);
		}
		
		public float scaledWidth(float scaleX) {
			if(scaleX == 1) {
				return comp().width;
			}
			return scaleX * comp().width;
		}
		
		public float scaledHeight() {
			return scaledHeight(comp().scale.y);
		}
		
		public float scaledHeight(float scaleY) {
			if(scaleY == 1) {
				return comp().height;
			}
			return scaleY * comp().height;
		}
	}
	
	public static class CompWrapperVitalsComp extends CompWrapper<VitalsComp> {
		
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
	
	public static class CompWrapperCursorInputRegulatorComp extends CompWrapper<CursorInputRegulatorComp> {
		
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
	
	public static class CompWrapperZonePositionComp extends CompWrapper<ZonePositionComp> {
		
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
	
	public static class CompWrapperZoneComp extends CompWrapper<ZoneComp> {
		
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
	public static class CompWrapperTurnActionComp extends CompWrapper<TurnActionComp> {
		
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
	
	public static class CompWrapperActivePlayerComp extends CompWrapper<ActivePlayerComp> {
		
		private CompWrapperActivePlayerComp() {}
		
		public PlayerComp getPlayerComp() {
			return Comp.PlayerComp.get(Comp.Entity.get(comp().activePlayerID));
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
	
	public static class CompWrapperCursorComp extends CompWrapper<CursorComp> {
		
		private CompWrapperCursorComp() {}
		
		private Entity TurnActionEntity() {
			return Comp.Entity.get(comp().turnActionEntityID);
		}
		
		/**
		 * Purely helper method
		 */
		public void cancelTurnAction(Engine engine) {
			Entity entity = TurnActionEntity();
			if(entity != null) {
				Comp.add(engine, CleanupTurnActionComp.class, entity);
			}
			comp().turnActionEntityID = null;
		}
		
		/**
		 * Purely helper method		 */
		public TurnAction turnAction() {
			Entity entity = TurnActionEntity();
			if(entity == null) {
				return null;
			}
			return Comp.TurnActionComp.get(entity).turnAction;
		}		
		
	}
}
