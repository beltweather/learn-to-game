package com.jharter.game.ashley.components;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.utils.ObjectMap;
import com.jharter.game.ashley.components.Components.CleanupTurnActionComp;
import com.jharter.game.ashley.components.Components.ActivePlayerComp;
import com.jharter.game.ashley.components.Components.BodyComp;
import com.jharter.game.ashley.components.Components.CursorComp;
import com.jharter.game.ashley.components.Components.DisabledComp;
import com.jharter.game.ashley.components.Components.IDComp;
import com.jharter.game.ashley.components.Components.InputComp;
import com.jharter.game.ashley.components.Components.MultiSpriteComp;
import com.jharter.game.ashley.components.Components.RemoveComp;
import com.jharter.game.ashley.components.Components.SensorComp;
import com.jharter.game.ashley.components.Components.TurnActionComp;
import com.jharter.game.ashley.components.Components.TurnPhaseComp;
import com.jharter.game.ashley.components.Components.TurnTimerComp;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.Components.ZonePositionComp;
import com.jharter.game.ashley.components.subcomponents.TurnAction;
import com.jharter.game.util.ArrayUtil;
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
		
	private abstract class EntityMapper {
		
		protected Entity entity;
		
		private EntityMapper() {}
		
		void setEntity(Entity entity) {
			this.entity = entity;
		}
		
		protected abstract ID getDefaultEntityID();

		public Entity Entity() {
			if(entity != null) {
				return entity;
			}
			return get(getDefaultEntityID());
		}
		
	}
	
	public class EntityMapperCursorEntity extends EntityMapper {
		
		private EntityMapperCursorEntity() {}
		
		@Override
		protected ID getDefaultEntityID() {
			return IDUtil.getCursorEntityID();
		}
		
		private CursorComp CursorComp() {
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
			Comp.remove(MultiSpriteComp.class, Entity());
		}
		
		public void toHand(Engine engine) {
			Comp.CursorComp(CursorComp()).cancelTurnAction(engine);
			
			ZonePositionComp zp = Comp.ZonePositionComp.get(Entity());
			Comp.ZonePositionComp(zp).clearHistory();
			zp.index = 0;
			zp.zoneID = Comp.Find.ZoneComp.findZoneID(DefaultTurn().ActivePlayerComp().activePlayerID, ZoneType.HAND);
		
			CursorComp().targetID = Comp.Find.ZoneComp.findZone(DefaultTurn().ActivePlayerComp().activePlayerID, ZoneType.HAND).objectIDs.first();
		}
		
		public ID getPlayerID() {
			return DefaultTurn().ActivePlayerComp().activePlayerID;
		}
		
		public void toDefault(Engine engine) {
			single();
			toHand(engine);
		}

		/*public void cancelTurnAction(Engine engine) {
			Entity entity = TurnActionEntity();
			if(entity != null) {
				Comp.add(engine, ActionSpentComp.class, entity);
			}
			CursorComp().turnActionEntityID = null;
		}
		
		public Entity TurnActionEntity() {
			return Comp.Entity.get(CursorComp().turnActionEntityID);
		}
		
		public TurnAction TurnAction() {
			Entity entity = TurnActionEntity();
			if(entity == null) {
				return null;
			}
			return Comp.TurnActionComp.get(entity).turnAction;
		}*/		

	}
	
	public class EntityMapperTurnEntity extends EntityMapper {
		
		private EntityMapperTurnEntity() {}
		
		@Override
		protected ID getDefaultEntityID() {
			return IDUtil.getTurnEntityID();
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
	
	private final EntityMapperCursorEntity entityMapperCursor = new EntityMapperCursorEntity();
	private final EntityMapperTurnEntity entityMapperTurn = new EntityMapperTurnEntity();
	
	public EntityMapperCursorEntity Cursor(Entity cursorEntity) {
		entityMapperCursor.setEntity(cursorEntity);
		return entityMapperCursor;
	}
	
	public EntityMapperCursorEntity DefaultCursor() {
		return Cursor(null);
	}
	
	public EntityMapperTurnEntity Turn(Entity turnEntity) {
		entityMapperTurn.setEntity(turnEntity);
		return entityMapperTurn;
	}
	
	public EntityMapperTurnEntity DefaultTurn() {
		return Turn(null);
	}
	
}
