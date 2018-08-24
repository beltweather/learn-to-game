package com.jharter.game.ashley.components;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pools;
import com.jharter.game.ashley.components.Components.ActionQueuedComp;
import com.jharter.game.ashley.components.Components.ActionReadyComp;
import com.jharter.game.ashley.components.Components.ActionSpentComp;
import com.jharter.game.ashley.components.Components.ActiveCardComp;
import com.jharter.game.ashley.components.Components.AlphaComp;
import com.jharter.game.ashley.components.Components.AnimatedPathComp;
import com.jharter.game.ashley.components.Components.AnimationComp;
import com.jharter.game.ashley.components.Components.BodyComp;
import com.jharter.game.ashley.components.Components.CardComp;
import com.jharter.game.ashley.components.Components.CollisionComp;
import com.jharter.game.ashley.components.Components.CursorComp;
import com.jharter.game.ashley.components.Components.CursorInputComp;
import com.jharter.game.ashley.components.Components.CursorInputRegulatorComp;
import com.jharter.game.ashley.components.Components.DescriptionComp;
import com.jharter.game.ashley.components.Components.FocusComp;
import com.jharter.game.ashley.components.Components.IDComp;
import com.jharter.game.ashley.components.Components.InputComp;
import com.jharter.game.ashley.components.Components.InteractComp;
import com.jharter.game.ashley.components.Components.InvisibleComp;
import com.jharter.game.ashley.components.Components.MultiPositionComp;
import com.jharter.game.ashley.components.Components.PlayerComp;
import com.jharter.game.ashley.components.Components.PositionComp;
import com.jharter.game.ashley.components.Components.RemoveComp;
import com.jharter.game.ashley.components.Components.SensorComp;
import com.jharter.game.ashley.components.Components.SizeComp;
import com.jharter.game.ashley.components.Components.StatsComp;
import com.jharter.game.ashley.components.Components.TargetPositionComp;
import com.jharter.game.ashley.components.Components.TextureComp;
import com.jharter.game.ashley.components.Components.TileComp;
import com.jharter.game.ashley.components.Components.TurnActionComp;
import com.jharter.game.ashley.components.Components.TurnTimerComp;
import com.jharter.game.ashley.components.Components.TypeComp;
import com.jharter.game.ashley.components.Components.UntargetableComp;
import com.jharter.game.ashley.components.Components.VelocityComp;
import com.jharter.game.ashley.components.Components.VitalsComp;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.Components.ZonePositionComp;
import com.jharter.game.util.id.ID;
import com.jharter.game.util.id.IDGenerator;

import uk.co.carelesslabs.Enums.ZoneType;
import uk.co.carelesslabs.box2d.Box2DWorld;

public class Mapper {
	
	private static ID turnTimerID;
	private static Map<ZoneType, ID> idsByZoneType = new HashMap<ZoneType, ID>();
	private static final ObjectMap<ID, Entity> entitiesById = new ObjectMap<ID, Entity>();
	
	public static ID getTurnTimerID() {
		if(turnTimerID == null) {
			turnTimerID = IDGenerator.newID();
		}
		return turnTimerID;
	}
	
	public static TurnTimerComp getTurnTimerComp() {
		return Mapper.TurnTimerComp.get(Entity.get(turnTimerID));
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
				BodyComp b = Mapper.BodyComp.get(entity);
				SensorComp s = Mapper.SensorComp.get(entity);
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
	
	public static class ComponentMapperZoneComp {
		
		private ComponentMapperZoneComp() {}
		
		public ID getID(ZoneType type) {
			if(!idsByZoneType.containsKey(type)) {
				idsByZoneType.put(type, IDGenerator.newID());
			}
			return idsByZoneType.get(type);
		}
		
		public ZoneComp get(Entity entity) {
			return ComponentMapper.getFor(ZoneComp.class).get(entity);
		}
		
		public ZoneComp get(ZonePositionComp zp) {
			Entity zone = Mapper.Entity.get(zp.zoneType());
			return Mapper.ZoneComp.get(zone);
		}
		
		public ZoneComp get(ZoneType zoneType) {
			Entity zone = Mapper.Entity.get(zoneType);
			return Mapper.ZoneComp.get(zone);
		}
		
		public boolean has(Entity entity) {
			return ComponentMapper.getFor(ZoneComp.class).has(entity);
		}
		
		public boolean has(ZonePositionComp zp) {
			return has(Mapper.Entity.get(zp.zoneType()));
		}
		
		public boolean has(ZoneType zoneType) {
			return has(Mapper.Entity.get(zoneType));
		}
		
	}
	
	/**
	 * Convenience class so that we can retrieve entities in the same form as components.
	 */
	public static class ComponentMapperEntity {
		
		private ComponentMapperEntity() {}
		
		public Entity get(ID id) {
			if(id == null) {
				return null;
			}
			if(entitiesById.containsKey(id)) {
				return entitiesById.get(id);
			}
			return null;
		}
		
		public Entity get(ZoneType zoneType) {
			return get(ZoneComp.getID(zoneType));
		}
		
	}
	
	public static class ComponentMapperComp {
		
		private ComponentMapperComp() {}
		
		public <T> T get(Class<T> klass) {
			return Pools.get(klass).obtain();
		}
	
	}
	
	public static final ComponentMapperComp Comp = new ComponentMapperComp();
	public static final ComponentMapperEntity Entity = new ComponentMapperEntity();
	public static final ComponentMapper<PlayerComp> PlayerComp = ComponentMapper.getFor(PlayerComp.class);
	public static final ComponentMapper<FocusComp> FocusComp = ComponentMapper.getFor(FocusComp.class);
	public static final ComponentMapper<IDComp> IDComp = ComponentMapper.getFor(IDComp.class);
	public static final ComponentMapper<PositionComp> PositionComp = ComponentMapper.getFor(PositionComp.class);
	public static final ComponentMapper<SizeComp> SizeComp = ComponentMapper.getFor(SizeComp.class);
	public static final ComponentMapper<TypeComp> TypeComp = ComponentMapper.getFor(TypeComp.class);
	public static final ComponentMapper<TileComp> TileComp = ComponentMapper.getFor(TileComp.class);
	public static final ComponentMapper<TextureComp> TextureComp = ComponentMapper.getFor(TextureComp.class);
	public static final ComponentMapper<AnimationComp> AnimationComp = ComponentMapper.getFor(AnimationComp.class);
	public static final ComponentMapper<BodyComp> BodyComp = ComponentMapper.getFor(BodyComp.class);
	public static final ComponentMapper<SensorComp> SensorComp = ComponentMapper.getFor(SensorComp.class);
	public static final ComponentMapper<TargetPositionComp> TargetPositionComp = ComponentMapper.getFor(TargetPositionComp.class);
	public static final ComponentMapper<VelocityComp> VelocityComp = ComponentMapper.getFor(VelocityComp.class);
	public static final ComponentMapper<CollisionComp> CollisionComp = ComponentMapper.getFor(CollisionComp.class);
	public static final ComponentMapper<RemoveComp> RemoveComp = ComponentMapper.getFor(RemoveComp.class);
	public static final ComponentMapper<InputComp> InputComp = ComponentMapper.getFor(InputComp.class);
	public static final ComponentMapper<InvisibleComp> InvisibleComp = ComponentMapper.getFor(InvisibleComp.class);
	public static final ComponentMapper<InteractComp> InteractComp = ComponentMapper.getFor(InteractComp.class);
	public static final ComponentMapper<CursorComp> CursorComp = ComponentMapper.getFor(CursorComp.class);
	public static final ComponentMapper<CursorInputComp> CursorInputComp = ComponentMapper.getFor(CursorInputComp.class);
	public static final ComponentMapper<CursorInputRegulatorComp> CursorInputRegulatorComp = ComponentMapper.getFor(CursorInputRegulatorComp.class);
	public static final ComponentMapperZoneComp ZoneComp = new ComponentMapperZoneComp();
	public static final ComponentMapper<ZonePositionComp> ZonePositionComp = ComponentMapper.getFor(ZonePositionComp.class);
	public static final ComponentMapper<CardComp> CardComp = ComponentMapper.getFor(CardComp.class);
	public static final ComponentMapper<ActiveCardComp> ActiveCardComp = ComponentMapper.getFor(ActiveCardComp.class);
	public static final ComponentMapper<TurnActionComp> TurnActionComp = ComponentMapper.getFor(TurnActionComp.class);
	public static final ComponentMapper<DescriptionComp> DescriptionComp = ComponentMapper.getFor(DescriptionComp.class);
	public static final ComponentMapper<VitalsComp> VitalsComp = ComponentMapper.getFor(VitalsComp.class);
	public static final ComponentMapper<StatsComp> StatsComp = ComponentMapper.getFor(StatsComp.class);
	public static final ComponentMapper<MultiPositionComp> MultiPositionComp = ComponentMapper.getFor(MultiPositionComp.class);
	public static final ComponentMapper<ActionReadyComp> ActionReadyComp = ComponentMapper.getFor(ActionReadyComp.class);
	public static final ComponentMapper<ActionQueuedComp> ActionQueuedComp = ComponentMapper.getFor(ActionQueuedComp.class);
	public static final ComponentMapper<ActionSpentComp> ActionSpentComp = ComponentMapper.getFor(ActionSpentComp.class);
	public static final ComponentMapper<TurnTimerComp> TurnTimerComp = ComponentMapper.getFor(TurnTimerComp.class);
	public static final ComponentMapper<UntargetableComp> UntargetableComp = ComponentMapper.getFor(UntargetableComp.class);
	public static final ComponentMapper<AlphaComp> AlphaComp = ComponentMapper.getFor(AlphaComp.class);
	public static final ComponentMapper<AnimatedPathComp> AnimatedPathComp = ComponentMapper.getFor(AnimatedPathComp.class);
	
}
