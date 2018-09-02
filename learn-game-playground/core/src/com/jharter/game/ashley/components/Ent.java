package com.jharter.game.ashley.components;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.utils.ObjectMap;
import com.jharter.game.ashley.components.Components.BodyComp;
import com.jharter.game.ashley.components.Components.CursorComp;
import com.jharter.game.ashley.components.Components.DisabledComp;
import com.jharter.game.ashley.components.Components.IDComp;
import com.jharter.game.ashley.components.Components.InputComp;
import com.jharter.game.ashley.components.Components.MultiSpriteComp;
import com.jharter.game.ashley.components.Components.RemoveComp;
import com.jharter.game.ashley.components.Components.SensorComp;
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
import com.jharter.game.ashley.components.Components.ZonePositionComp;
import com.jharter.game.util.id.ID;
import com.jharter.game.util.id.IDUtil;

import uk.co.carelesslabs.Enums.ZoneType;
import uk.co.carelesslabs.box2d.Box2DWorld;

/**
 * Used to retrieve entities directly, the sibling of "C" the mapper and "L" the linker
 */
public class Ent {
	
	private Ent() {}

	private static final ObjectMap<ID, Entity> entitiesById = new ObjectMap<ID, Entity>();
	public static int activePlayerIndex = 0;
	
	public static void resetActivePlayerEntity() {
		activePlayerIndex = 0;
	}
	
	public static void nextActivePlayerEntity() {
		activePlayerIndex = (activePlayerIndex+1) % IDUtil.getPlayerIDs().size();
	}
	
	public static int getActivePlayerIndex() {
		return activePlayerIndex;
	}
	
	public static boolean hasNextActivePlayer() {
		return activePlayerIndex == IDUtil.getPlayerIDs().size()-1;
	}
	
	public static void addIdListener(PooledEngine engine, final Box2DWorld box2D) {
		engine.addEntityListener(Family.all(IDComp.class).get(), new EntityListener() {
			
			private ComponentMapper<IDComp> im = ComponentMapper.getFor(IDComp.class);
			
			@Override
			public void entityAdded(Entity entity) {
				IDComp idComp = im.get(entity);
				if(idComp.id != null) {
					entitiesById.put(idComp.id, entity);
				}
			}

			@Override
			public void entityRemoved(Entity entity) {
				IDComp idComp = im.get(entity);
				BodyComp b = Comp.BodyComp.get(entity);
				SensorComp s = Comp.SensorComp.get(entity);
				if(b != null && b.body != null) {
					box2D.world.destroyBody(b.body);
				}
				if(s != null && s.sensor != null) {
					box2D.world.destroyBody(s.sensor);
				}
				if(idComp.id != null) {
					entitiesById.remove(idComp.id);
				}
			}
			
		});
	}
	
	/**
	 * Convenience class so that we can retrieve entities in the same form as components.
	 */
	public static class EntityMapperEntity {
		
		private EntityMapperEntity() {}
		
		public Entity get(ID id) {
			if(id == null) {
				return null;
			}
			if(entitiesById.containsKey(id)) {
				return entitiesById.get(id);
			}
			return null;
		}
		
		public void remove(ID id) {
			remove(get(id));
		}
		
		public void remove(Entity entity) {
			if(entity == null) {
				return;
			}
			Comp.getOrAdd(RemoveComp.class, entity);
		}
		
	}
	
	public static class EntityMapperCursorEntity {
		
		private EntityMapperCursorEntity() {}
		
		public Entity Entity() {
			return Ent.Entity.get(IDUtil.getCursorEntityID());
		}
		
		public CursorComp CursorComp() {
			return Comp.CursorComp.get(Entity());
		}
		
		public void enable() {
			Entity entity = Entity();
			if(Comp.DisabledComp.has(entity)) {
				entity.remove(DisabledComp.class);
			}
			InputComp in = Comp.InputComp.get(entity);
			in.input.reset();
		}
		
		public void disable() {
			Entity entity = Entity();
			if(!Comp.DisabledComp.has(entity)) {
				entity.add(Comp.create(DisabledComp.class));
			}
			InputComp in = Comp.InputComp.get(entity);
			in.input.reset();
		}
		
		public boolean isEnabled() {
			return !isDisabled();
		}
		
		public boolean isDisabled() {
			return Comp.DisabledComp.has(Entity());
		}
		
		public void single() {
			Entity entity = Entity();
			if(Comp.MultiSpriteComp.has(entity)) {
				entity.remove(MultiSpriteComp.class);
			}
		}
		
		public void toHand() {
			CursorComp c = Comp.CursorComp.get(Entity());
			ZonePositionComp zp = Comp.ZonePositionComp.get(Entity());
			zp.index = 0;
			zp.zoneID = Link.ZoneComp.getID(IDUtil.getPlayerEntityID(), ZoneType.HAND);
			zp.clearHistory();
		}
		
		public void reset() {
			single();
			toHand();
			CursorComp().turnActionEntityID = null;
		}
	}
	
	public static class EntityMapperTurnEntity {
		
		private EntityMapperTurnEntity() {}
		
		public Entity Entity() {
			return Ent.Entity.get(IDUtil.getTurnEntityID());
		}
		
		public TurnTimerComp TurnTimerComp() {
			return Comp.TurnTimerComp.get(Entity());
		}
		
		public TurnPhaseComp TurnPhaseComp() {
			return Comp.TurnPhaseComp.get(Entity());
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
	
	public static final EntityMapperEntity Entity = new EntityMapperEntity();
	public static final EntityMapperCursorEntity CursorEntity = new EntityMapperCursorEntity();
	public static final EntityMapperTurnEntity TurnEntity = new EntityMapperTurnEntity();
	
}
