package com.jharter.game.ashley.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.utils.ObjectMap;
import com.jharter.game.ashley.components.CompWrappers.CompWrapperCursorComp;
import com.jharter.game.ashley.components.CompWrappers.CompWrapperSpriteComp;
import com.jharter.game.ashley.components.CompWrappers.CompWrapperVitalsComp;
import com.jharter.game.ashley.components.CompWrappers.CompWrapperZoneComp;
import com.jharter.game.ashley.components.Components.ActionQueuedComp;
import com.jharter.game.ashley.components.Components.ActionReadyComp;
import com.jharter.game.ashley.components.Components.ActivePlayerComp;
import com.jharter.game.ashley.components.Components.ActiveTurnActionComp;
import com.jharter.game.ashley.components.Components.AnimatingComp;
import com.jharter.game.ashley.components.Components.AnimationComp;
import com.jharter.game.ashley.components.Components.BodyComp;
import com.jharter.game.ashley.components.Components.CardComp;
import com.jharter.game.ashley.components.Components.ChangeZoneComp;
import com.jharter.game.ashley.components.Components.CleanupTurnActionComp;
import com.jharter.game.ashley.components.Components.CollisionComp;
import com.jharter.game.ashley.components.Components.CursorComp;
import com.jharter.game.ashley.components.Components.CursorInputComp;
import com.jharter.game.ashley.components.Components.CursorInputRegulatorComp;
import com.jharter.game.ashley.components.Components.DescriptionComp;
import com.jharter.game.ashley.components.Components.DisabledComp;
import com.jharter.game.ashley.components.Components.FocusComp;
import com.jharter.game.ashley.components.Components.IDComp;
import com.jharter.game.ashley.components.Components.InputComp;
import com.jharter.game.ashley.components.Components.InteractComp;
import com.jharter.game.ashley.components.Components.InvisibleComp;
import com.jharter.game.ashley.components.Components.MultiSpriteComp;
import com.jharter.game.ashley.components.Components.PlayerComp;
import com.jharter.game.ashley.components.Components.RemoveComp;
import com.jharter.game.ashley.components.Components.SensorComp;
import com.jharter.game.ashley.components.Components.ShapeRenderComp;
import com.jharter.game.ashley.components.Components.SpriteComp;
import com.jharter.game.ashley.components.Components.StatsComp;
import com.jharter.game.ashley.components.Components.TargetPositionComp;
import com.jharter.game.ashley.components.Components.TargetableComp;
import com.jharter.game.ashley.components.Components.TextureComp;
import com.jharter.game.ashley.components.Components.TileComp;
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
import com.jharter.game.ashley.components.Components.TypeComp;
import com.jharter.game.ashley.components.Components.UntargetableComp;
import com.jharter.game.ashley.components.Components.VelocityComp;
import com.jharter.game.ashley.components.Components.VitalsComp;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.Components.ZonePositionComp;
import com.jharter.game.util.id.ID;

/**
 * Main mapper class for all components that maps ids to comps and entities directly.
 * It is the sister class of "L" the linker class which maps relationships accross components.
 * This class should remain fully stateless.
 */
public class Comp {
	
	private static final ObjectMap<Class, CompMapper> componentMappersByClass = new ObjectMap<Class, CompMapper>();
	
	private Comp() {}
	
	private static final CompWrappers Wrap = new CompWrappers();
	public static final CompEntities Entity = new CompEntities();
	public static final CompFinders Find = new CompFinders();
	
	public static class CompMapper<T extends Component> {
		
		private ComponentMapper<T> mapper;
		
		private CompMapper(Class<T> klass) {
			mapper = ComponentMapper.getFor(klass);
		}
		
		public boolean has(ID entityID) {
			return has(Comp.Entity.get(entityID));
		}
		
		public boolean has(Entity entity) {
			return mapper.has(entity);
		}
		
		public T get(ID entityID) {
			return get(Comp.Entity.get(entityID));
		}
		
		public T get(Entity entity) {
			return mapper.get(entity);
		}
		
	}
	
	public static <T extends Component> CompMapper<T> getFor(Class<T> klass) {
		if(!componentMappersByClass.containsKey(klass)) {
			componentMappersByClass.put(klass, new CompMapper<T>(klass));
		}
		return componentMappersByClass.get(klass);
	}
	
	public static <T extends Component> T create(Engine engine, Class<T> klass) {
		if(engine == null || !(engine instanceof PooledEngine)) {
			return null;
		}
		return ((PooledEngine) engine).createComponent(klass);
	}
	
	public static <T extends Component> void add(Engine engine, Class<T> klass, Entity entity) {
		if(entity != null && !getFor(klass).has(entity)) {
			entity.add(create(engine, klass));
		}
	}
	
	public static <T extends Component> T getOrAdd(Engine engine, Class<T> klass, Entity entity) {
		if(getFor(klass).has(entity)) {
			return getFor(klass).get(entity);
		}
		T comp = create(engine, klass);
		entity.add(comp);
		return comp;
	}
	
	public static <T extends Component> boolean remove(Class<T> klass, Entity entity) {
		if(entity == null || !getFor(klass).has(entity)) {
			return false;
		}
		entity.remove(klass);
		return true;
	}
	
	public static <TA extends Component, TB extends Component> void swap(Engine engine, Class<TA> klassOld, Class<TB> klassNew, Entity entity) {
		remove(klassOld, entity);
		add(engine, klassNew, entity);
	}
	
	public static boolean has(Class<? extends Component> klass, Entity entity) {
		return getFor(klass).has(entity);
	}
	
	// Standard Component Mappers
	public static final CompMapper<SpriteComp> SpriteComp = getFor(SpriteComp.class);
	public static final CompMapper<FocusComp> FocusComp = getFor(FocusComp.class);
	public static final CompMapper<IDComp> IDComp = getFor(IDComp.class);
	public static final CompMapper<TypeComp> TypeComp = getFor(TypeComp.class);
	public static final CompMapper<TileComp> TileComp = getFor(TileComp.class);
	public static final CompMapper<TextureComp> TextureComp = getFor(TextureComp.class);
	public static final CompMapper<AnimationComp> AnimationComp = getFor(AnimationComp.class);
	public static final CompMapper<BodyComp> BodyComp = getFor(BodyComp.class);
	public static final CompMapper<SensorComp> SensorComp = getFor(SensorComp.class);
	public static final CompMapper<TargetPositionComp> TargetPositionComp = getFor(TargetPositionComp.class);
	public static final CompMapper<VelocityComp> VelocityComp = getFor(VelocityComp.class);
	public static final CompMapper<CollisionComp> CollisionComp = getFor(CollisionComp.class);
	public static final CompMapper<RemoveComp> RemoveComp = getFor(RemoveComp.class);
	public static final CompMapper<InputComp> InputComp = getFor(InputComp.class);
	public static final CompMapper<InvisibleComp> InvisibleComp = getFor(InvisibleComp.class);
	public static final CompMapper<InteractComp> InteractComp = getFor(InteractComp.class);
	public static final CompMapper<CursorComp> CursorComp = getFor(CursorComp.class);
	public static final CompMapper<CursorInputComp> CursorInputComp = getFor(CursorInputComp.class);
	public static final CompMapper<CursorInputRegulatorComp> CursorInputRegulatorComp = getFor(CursorInputRegulatorComp.class);
	public static final CompMapper<ZoneComp> ZoneComp = getFor(ZoneComp.class);
	public static final CompMapper<ZonePositionComp> ZonePositionComp = getFor(ZonePositionComp.class);
	public static final CompMapper<CardComp> CardComp = getFor(CardComp.class);
	public static final CompMapper<ActiveTurnActionComp> ActiveTurnActionComp = getFor(ActiveTurnActionComp.class);
	public static final CompMapper<TurnActionComp> TurnActionComp = getFor(TurnActionComp.class);
	public static final CompMapper<DescriptionComp> DescriptionComp = getFor(DescriptionComp.class);
	public static final CompMapper<VitalsComp> VitalsComp = getFor(VitalsComp.class);
	public static final CompMapper<StatsComp> StatsComp = getFor(StatsComp.class);
	public static final CompMapper<MultiSpriteComp> MultiSpriteComp = getFor(MultiSpriteComp.class);
	public static final CompMapper<ActionReadyComp> ActionReadyComp = getFor(ActionReadyComp.class);
	public static final CompMapper<ActionQueuedComp> ActionQueuedComp = getFor(ActionQueuedComp.class);
	public static final CompMapper<CleanupTurnActionComp> ActionSpentComp = getFor(CleanupTurnActionComp.class);
	public static final CompMapper<TurnTimerComp> TurnTimerComp = getFor(TurnTimerComp.class);
	public static final CompMapper<TargetableComp> TargetableComp = getFor(TargetableComp.class);
	public static final CompMapper<UntargetableComp> UntargetableComp = getFor(UntargetableComp.class);
	public static final CompMapper<DisabledComp> DisabledComp = getFor(DisabledComp.class);
	public static final CompMapper<AnimatingComp> AnimatingComp = getFor(AnimatingComp.class);
	public static final CompMapper<ChangeZoneComp> ChangeZoneComp = getFor(ChangeZoneComp.class);
	public static final CompMapper<PlayerComp> PlayerComp = getFor(PlayerComp.class);
	public static final CompMapper<ShapeRenderComp> ShapeRenderComp = getFor(ShapeRenderComp.class);
	public static final CompMapper<TurnPhaseComp> TurnPhaseComp = getFor(TurnPhaseComp.class);
	public static final CompMapper<TurnPhaseStartBattleComp> TurnPhaseStartBattleComp = getFor(TurnPhaseStartBattleComp.class);
	public static final CompMapper<TurnPhaseStartTurnComp> TurnPhaseStartTurnComp = getFor(TurnPhaseStartTurnComp.class);
	public static final CompMapper<TurnPhaseSelectEnemyActionsComp> TurnPhaseSelectEnemyActionsComp = getFor(TurnPhaseSelectEnemyActionsComp.class);
	public static final CompMapper<TurnPhaseSelectFriendActionsComp> TurnPhaseSelectFriendActionsComp = getFor(TurnPhaseSelectFriendActionsComp.class);
	public static final CompMapper<TurnPhasePerformFriendActionsComp> TurnPhasePerformFriendActionsComp = getFor(TurnPhasePerformFriendActionsComp.class);
	public static final CompMapper<TurnPhasePerformEnemyActionsComp> TurnPhasePerformEnemyActionsComp = getFor(TurnPhasePerformEnemyActionsComp.class);
	public static final CompMapper<TurnPhaseEndTurnComp> TurnPhaseEndTurnComp = getFor(TurnPhaseEndTurnComp.class);
	public static final CompMapper<TurnPhaseEndBattleComp> TurnPhaseEndBattleComp = getFor(TurnPhaseEndBattleComp.class);
	public static final CompMapper<TurnPhaseNoneComp> TurnPhaseNoneComp = getFor(TurnPhaseNoneComp.class);
	public static final CompMapper<ActivePlayerComp> ActivePlayerComp = getFor(ActivePlayerComp.class);
	
	// Special Component Wrappers for Added Methods
	public static CompWrapperSpriteComp SpriteComp(SpriteComp comp) { return Wrap.get(CompWrapperSpriteComp.class, comp); }
	public static CompWrapperZoneComp ZoneComp(ZoneComp comp) { return Wrap.get(CompWrapperZoneComp.class, comp); }
	public static CompWrapperVitalsComp VitalsComp(VitalsComp comp) { return Wrap.get(CompWrapperVitalsComp.class, comp); }
	public static CompWrapperVitalsComp VitalsComp(Entity entity) { return Wrap.get(CompWrapperVitalsComp.class, VitalsComp.class, entity); }
	public static CompWrapperCursorComp CursorComp(CursorComp comp) { return Wrap.get(CompWrapperCursorComp.class, comp); }

}
