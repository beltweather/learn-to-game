package com.jharter.game.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.utils.ObjectMap;
import com.jharter.game.ecs.components.CompWrappers.CompWrapperSpriteComp;
import com.jharter.game.ecs.components.CompWrappers.CompWrapperVitalsComp;
import com.jharter.game.ecs.components.CompWrappers.CompWrapperZoneComp;
import com.jharter.game.ecs.components.Components.ActionQueuedComp;
import com.jharter.game.ecs.components.Components.ActionReadyComp;
import com.jharter.game.ecs.components.Components.ActivePlayerComp;
import com.jharter.game.ecs.components.Components.ActiveTurnActionComp;
import com.jharter.game.ecs.components.Components.AnimatingComp;
import com.jharter.game.ecs.components.Components.AnimationComp;
import com.jharter.game.ecs.components.Components.BodyComp;
import com.jharter.game.ecs.components.Components.CardComp;
import com.jharter.game.ecs.components.Components.ChangeZoneComp;
import com.jharter.game.ecs.components.Components.CleanupTurnActionComp;
import com.jharter.game.ecs.components.Components.CollisionComp;
import com.jharter.game.ecs.components.Components.CursorComp;
import com.jharter.game.ecs.components.Components.CursorInputComp;
import com.jharter.game.ecs.components.Components.CursorInputRegulatorComp;
import com.jharter.game.ecs.components.Components.DescriptionComp;
import com.jharter.game.ecs.components.Components.DisabledComp;
import com.jharter.game.ecs.components.Components.FocusComp;
import com.jharter.game.ecs.components.Components.IDComp;
import com.jharter.game.ecs.components.Components.InputComp;
import com.jharter.game.ecs.components.Components.InteractComp;
import com.jharter.game.ecs.components.Components.InvisibleComp;
import com.jharter.game.ecs.components.Components.MultiSpriteComp;
import com.jharter.game.ecs.components.Components.PlayerComp;
import com.jharter.game.ecs.components.Components.RemoveComp;
import com.jharter.game.ecs.components.Components.SensorComp;
import com.jharter.game.ecs.components.Components.ShapeRenderComp;
import com.jharter.game.ecs.components.Components.SpriteComp;
import com.jharter.game.ecs.components.Components.StatsComp;
import com.jharter.game.ecs.components.Components.TargetPositionComp;
import com.jharter.game.ecs.components.Components.TargetableComp;
import com.jharter.game.ecs.components.Components.TextureComp;
import com.jharter.game.ecs.components.Components.TileComp;
import com.jharter.game.ecs.components.Components.TurnActionComp;
import com.jharter.game.ecs.components.Components.TurnPhaseComp;
import com.jharter.game.ecs.components.Components.TurnPhaseEndBattleComp;
import com.jharter.game.ecs.components.Components.TurnPhaseEndTurnComp;
import com.jharter.game.ecs.components.Components.TurnPhaseNoneComp;
import com.jharter.game.ecs.components.Components.TurnPhasePerformEnemyActionsComp;
import com.jharter.game.ecs.components.Components.TurnPhasePerformFriendActionsComp;
import com.jharter.game.ecs.components.Components.TurnPhaseSelectEnemyActionsComp;
import com.jharter.game.ecs.components.Components.TurnPhaseSelectFriendActionsComp;
import com.jharter.game.ecs.components.Components.TurnPhaseStartBattleComp;
import com.jharter.game.ecs.components.Components.TurnPhaseStartTurnComp;
import com.jharter.game.ecs.components.Components.TurnTimerComp;
import com.jharter.game.ecs.components.Components.TypeComp;
import com.jharter.game.ecs.components.Components.UntargetableComp;
import com.jharter.game.ecs.components.Components.VelocityComp;
import com.jharter.game.ecs.components.Components.VitalsComp;
import com.jharter.game.ecs.components.Components.ZoneComp;
import com.jharter.game.ecs.components.Components.ZonePositionComp;
import com.jharter.game.ecs.entities.EntityManager;
import com.jharter.game.util.id.ID;

/**
 * Main mapper class for all components that maps ids to comps and entities directly.
 * It is the sister class of "L" the linker class which maps relationships accross components.
 * This class should remain fully stateless.
 */
public class CompManager {
	
	private final ObjectMap<Class, CompMapper> componentMappersByClass = new ObjectMap<Class, CompMapper>();
	
	public CompManager() {
		
	}
	
	private final CompWrappers Wrap = new CompWrappers(this);
	public final EntityManager Entity = new EntityManager(this);
	
	public class CompMapper<T extends Component> {
		
		private ComponentMapper<T> mapper;
		
		private CompMapper(Class<T> klass) {
			mapper = ComponentMapper.getFor(klass);
		}
		
		public boolean has(ID entityID) {
			return has(Entity.get(entityID));
		}
		
		public boolean has(Entity entity) {
			return mapper.has(entity);
		}
		
		public T get(ID entityID) {
			return get(Entity.get(entityID));
		}
		
		public T get(Entity entity) {
			return mapper.get(entity);
		}
		
	}
	
	public <T extends Component> CompMapper<T> getFor(Class<T> klass) {
		if(!componentMappersByClass.containsKey(klass)) {
			componentMappersByClass.put(klass, new CompMapper<T>(klass));
		}
		return componentMappersByClass.get(klass);
	}
	
	public <T extends Component> T create(Engine engine, Class<T> klass) {
		if(engine == null || !(engine instanceof PooledEngine)) {
			return null;
		}
		return ((PooledEngine) engine).createComponent(klass);
	}
	
	public <T extends Component> void add(Engine engine, Class<T> klass, Entity entity) {
		if(entity != null && !getFor(klass).has(entity)) {
			entity.add(create(engine, klass));
		}
	}
	
	public <T extends Component> T getOrAdd(Engine engine, Class<T> klass, Entity entity) {
		if(getFor(klass).has(entity)) {
			return getFor(klass).get(entity);
		}
		T comp = create(engine, klass);
		entity.add(comp);
		return comp;
	}
	
	public <T extends Component> boolean remove(Class<T> klass, Entity entity) {
		if(entity == null || !getFor(klass).has(entity)) {
			return false;
		}
		entity.remove(klass);
		return true;
	}
	
	public <TA extends Component, TB extends Component> void swap(Engine engine, Class<TA> klassOld, Class<TB> klassNew, Entity entity) {
		remove(klassOld, entity);
		add(engine, klassNew, entity);
	}
	
	public boolean has(Class<? extends Component> klass, Entity entity) {
		return getFor(klass).has(entity);
	}
	
	// Standard Component Mappers
	public final CompMapper<SpriteComp> SpriteComp = getFor(SpriteComp.class);
	public final CompMapper<FocusComp> FocusComp = getFor(FocusComp.class);
	public final CompMapper<IDComp> IDComp = getFor(IDComp.class);
	public final CompMapper<TypeComp> TypeComp = getFor(TypeComp.class);
	public final CompMapper<TileComp> TileComp = getFor(TileComp.class);
	public final CompMapper<TextureComp> TextureComp = getFor(TextureComp.class);
	public final CompMapper<AnimationComp> AnimationComp = getFor(AnimationComp.class);
	public final CompMapper<BodyComp> BodyComp = getFor(BodyComp.class);
	public final CompMapper<SensorComp> SensorComp = getFor(SensorComp.class);
	public final CompMapper<TargetPositionComp> TargetPositionComp = getFor(TargetPositionComp.class);
	public final CompMapper<VelocityComp> VelocityComp = getFor(VelocityComp.class);
	public final CompMapper<CollisionComp> CollisionComp = getFor(CollisionComp.class);
	public final CompMapper<RemoveComp> RemoveComp = getFor(RemoveComp.class);
	public final CompMapper<InputComp> InputComp = getFor(InputComp.class);
	public final CompMapper<InvisibleComp> InvisibleComp = getFor(InvisibleComp.class);
	public final CompMapper<InteractComp> InteractComp = getFor(InteractComp.class);
	public final CompMapper<CursorComp> CursorComp = getFor(CursorComp.class);
	public final CompMapper<CursorInputComp> CursorInputComp = getFor(CursorInputComp.class);
	public final CompMapper<CursorInputRegulatorComp> CursorInputRegulatorComp = getFor(CursorInputRegulatorComp.class);
	public final CompMapper<ZoneComp> ZoneComp = getFor(ZoneComp.class);
	public final CompMapper<ZonePositionComp> ZonePositionComp = getFor(ZonePositionComp.class);
	public final CompMapper<CardComp> CardComp = getFor(CardComp.class);
	public final CompMapper<ActiveTurnActionComp> ActiveTurnActionComp = getFor(ActiveTurnActionComp.class);
	public final CompMapper<TurnActionComp> TurnActionComp = getFor(TurnActionComp.class);
	public final CompMapper<DescriptionComp> DescriptionComp = getFor(DescriptionComp.class);
	public final CompMapper<VitalsComp> VitalsComp = getFor(VitalsComp.class);
	public final CompMapper<StatsComp> StatsComp = getFor(StatsComp.class);
	public final CompMapper<MultiSpriteComp> MultiSpriteComp = getFor(MultiSpriteComp.class);
	public final CompMapper<ActionReadyComp> ActionReadyComp = getFor(ActionReadyComp.class);
	public final CompMapper<ActionQueuedComp> ActionQueuedComp = getFor(ActionQueuedComp.class);
	public final CompMapper<CleanupTurnActionComp> ActionSpentComp = getFor(CleanupTurnActionComp.class);
	public final CompMapper<TurnTimerComp> TurnTimerComp = getFor(TurnTimerComp.class);
	public final CompMapper<TargetableComp> TargetableComp = getFor(TargetableComp.class);
	public final CompMapper<UntargetableComp> UntargetableComp = getFor(UntargetableComp.class);
	public final CompMapper<DisabledComp> DisabledComp = getFor(DisabledComp.class);
	public final CompMapper<AnimatingComp> AnimatingComp = getFor(AnimatingComp.class);
	public final CompMapper<ChangeZoneComp> ChangeZoneComp = getFor(ChangeZoneComp.class);
	public final CompMapper<PlayerComp> PlayerComp = getFor(PlayerComp.class);
	public final CompMapper<ShapeRenderComp> ShapeRenderComp = getFor(ShapeRenderComp.class);
	public final CompMapper<TurnPhaseComp> TurnPhaseComp = getFor(TurnPhaseComp.class);
	public final CompMapper<TurnPhaseStartBattleComp> TurnPhaseStartBattleComp = getFor(TurnPhaseStartBattleComp.class);
	public final CompMapper<TurnPhaseStartTurnComp> TurnPhaseStartTurnComp = getFor(TurnPhaseStartTurnComp.class);
	public final CompMapper<TurnPhaseSelectEnemyActionsComp> TurnPhaseSelectEnemyActionsComp = getFor(TurnPhaseSelectEnemyActionsComp.class);
	public final CompMapper<TurnPhaseSelectFriendActionsComp> TurnPhaseSelectFriendActionsComp = getFor(TurnPhaseSelectFriendActionsComp.class);
	public final CompMapper<TurnPhasePerformFriendActionsComp> TurnPhasePerformFriendActionsComp = getFor(TurnPhasePerformFriendActionsComp.class);
	public final CompMapper<TurnPhasePerformEnemyActionsComp> TurnPhasePerformEnemyActionsComp = getFor(TurnPhasePerformEnemyActionsComp.class);
	public final CompMapper<TurnPhaseEndTurnComp> TurnPhaseEndTurnComp = getFor(TurnPhaseEndTurnComp.class);
	public final CompMapper<TurnPhaseEndBattleComp> TurnPhaseEndBattleComp = getFor(TurnPhaseEndBattleComp.class);
	public final CompMapper<TurnPhaseNoneComp> TurnPhaseNoneComp = getFor(TurnPhaseNoneComp.class);
	public final CompMapper<ActivePlayerComp> ActivePlayerComp = getFor(ActivePlayerComp.class);
	
	// Special Component Wrappers for Added Methods
	public CompWrapperSpriteComp SpriteComp(SpriteComp comp) { return Wrap.get(CompWrapperSpriteComp.class, comp); }
	public CompWrapperZoneComp ZoneComp(ZoneComp comp) { return Wrap.get(CompWrapperZoneComp.class, comp); }
	public CompWrapperVitalsComp VitalsComp(VitalsComp comp) { return Wrap.get(CompWrapperVitalsComp.class, comp); }
	public CompWrapperVitalsComp VitalsComp(Entity entity) { return Wrap.get(CompWrapperVitalsComp.class, VitalsComp.class, entity); }

}
