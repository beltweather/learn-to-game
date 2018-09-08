package com.jharter.game.ashley.components;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.utils.ObjectMap;
import com.jharter.game.ashley.components.Components.ActionSpentComp;
import com.jharter.game.ashley.components.Components.ActivePlayerComp;
import com.jharter.game.ashley.components.Components.BodyComp;
import com.jharter.game.ashley.components.Components.CursorComp;
import com.jharter.game.ashley.components.Components.DisabledComp;
import com.jharter.game.ashley.components.Components.IDComp;
import com.jharter.game.ashley.components.Components.InputComp;
import com.jharter.game.ashley.components.Components.MultiSpriteComp;
import com.jharter.game.ashley.components.Components.RemoveComp;
import com.jharter.game.ashley.components.Components.SensorComp;
import com.jharter.game.ashley.components.Components.TurnPhaseComp;
import com.jharter.game.ashley.components.Components.TurnTimerComp;
import com.jharter.game.ashley.components.Components.ZonePositionComp;
import com.jharter.game.util.id.ID;
import com.jharter.game.util.id.IDUtil;

import uk.co.carelesslabs.Enums.ZoneType;
import uk.co.carelesslabs.box2d.Box2DWorld;

/**
 * Used to retrieve entities directly, the sibling of "C" the mapper and "L" the linker
 */
public class CompEntities {
	
	CompEntities() {}

	private final ObjectMap<ID, Entity> entitiesById = new ObjectMap<ID, Entity>();
	
	public void addIdListener(Engine engine, final Box2DWorld box2D) {
		engine.addEntityListener(Family.all(IDComp.class).get(), new EntityListener() {
			
			@Override
			public void entityAdded(Entity entity) {
				IDComp idComp = Comp.IDComp.get(entity);
				if(idComp.id != null) {
					entitiesById.put(idComp.id, entity);
				}
			}

			@Override
			public void entityRemoved(Entity entity) {
				IDComp idComp = Comp.IDComp.get(entity);
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
	
	public Entity get(ID id) {
		if(id == null) {
			return null;
		}
		if(entitiesById.containsKey(id)) {
			return entitiesById.get(id);
		}
		return null;
	}
	
	public void remove(Engine engine, ID id) {
		remove(engine, get(id));
	}
	
	public void remove(Engine engine, Entity entity) {
		if(entity == null) {
			return;
		}
		Comp.getOrAdd(engine, RemoveComp.class, entity);
	}
		
	public class EntityMapperCursorEntity {
		
		private EntityMapperCursorEntity() {}
		
		public Entity Entity() {
			return get(IDUtil.getCursorEntityID());
		}
		
		public CursorComp CursorComp() {
			return Comp.CursorComp.get(Entity());
		}
		
		public ZonePositionComp ZonePositionComp() {
			return Comp.ZonePositionComp.get(Entity());
		}
		
		public Entity TurnActionEntity() {
			return Comp.Entity.get(CursorComp().turnActionEntityID);
		}
		
		public void cancelTurnAction(Engine engine) {
			Entity entity = TurnActionEntity();
			if(entity != null) {
				Comp.add(engine, ActionSpentComp.class, entity);
			}
			CursorComp().turnActionEntityID = null;
		}
		
		public void enable() {
			Entity entity = Entity();
			if(Comp.DisabledComp.has(entity)) {
				entity.remove(DisabledComp.class);
			}
			InputComp in = Comp.InputComp.get(entity);
			in.input.reset();
		}
		
		public void disable(Engine engine) {
			Entity entity = Entity();
			if(!Comp.DisabledComp.has(entity)) {
				entity.add(Comp.create(engine, DisabledComp.class));
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
		
		public void toHand(Engine engine) {
			Comp.Entity.CursorEntity.cancelTurnAction(engine);
			ZonePositionComp zp = Comp.ZonePositionComp.get(Entity());
			zp.index = 0;
			zp.zoneID = Comp.Method.ZoneComp.getID(TurnEntity.ActivePlayerComp().activePlayerID, ZoneType.HAND);
			zp.clearHistory();
		}
		
		public boolean isValidTarget() {
			return Comp.Method.CursorComp.isValidTarget(Entity());
		}
		
		public boolean isValidTarget(int index) {
			return Comp.Method.CursorComp.isValidTarget(Entity(), index);
		}
		
		public void reset(Engine engine) {
			single();
			toHand(engine);
			CursorComp().turnActionEntityID = null;
		}
	}
	
	public class EntityMapperTurnEntity {
		
		private EntityMapperTurnEntity() {}
		
		public Entity Entity() {
			return get(IDUtil.getTurnEntityID());
		}
		
		public TurnTimerComp TurnTimerComp() {
			return Comp.TurnTimerComp.get(Entity());
		}
		
		public TurnPhaseComp TurnPhaseComp() {
			return Comp.TurnPhaseComp.get(Entity());
		}
		
		public ActivePlayerComp ActivePlayerComp() {
			return Comp.ActivePlayerComp.get(Entity());
		}
		
		public boolean isTurnPhaseStartBattle() { return Comp.TurnPhaseStartBattleComp.has(Entity()); }
		public boolean isTurnPhaseStartTurn() { return Comp.TurnPhaseStartTurnComp.has(Entity()); }
		public boolean isTurnPhaseSelectEnemyActions() { return Comp.TurnPhaseSelectEnemyActionsComp.has(Entity()); }
		public boolean isTurnPhaseSelectFriendActions() { return Comp.TurnPhaseSelectFriendActionsComp.has(Entity()); }
		public boolean isTurnPhasePerformFriendActions() { return Comp.TurnPhasePerformFriendActionsComp.has(Entity()); }
		public boolean isTurnPhasePerformEnemyActions() { return Comp.TurnPhasePerformEnemyActionsComp.has(Entity()); }
		public boolean isTurnPhaseEndTurn() { return Comp.TurnPhaseEndTurnComp.has(Entity()); }
		public boolean isTurnPhaseEndBattle() { return Comp.TurnPhaseEndBattleComp.has(Entity()); }
		public boolean isTurnPhaseNone() { return Comp.TurnPhaseNoneComp.has(Entity()); }
		
	}
	
	public final EntityMapperCursorEntity CursorEntity = new EntityMapperCursorEntity();
	public final EntityMapperTurnEntity TurnEntity = new EntityMapperTurnEntity();
	
}
