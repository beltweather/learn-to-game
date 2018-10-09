package com.jharter.game.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.utils.ObjectMap;
import com.jharter.game.ecs.components.Components.ActionQueueableComp;
import com.jharter.game.ecs.components.Components.ActionQueuedComp;
import com.jharter.game.ecs.components.Components.ActionReadyTag;
import com.jharter.game.ecs.components.Components.ActivePlayerComp;
import com.jharter.game.ecs.components.Components.ActiveTurnActionComp;
import com.jharter.game.ecs.components.Components.AnimatingComp;
import com.jharter.game.ecs.components.Components.AnimationComp;
import com.jharter.game.ecs.components.Components.AutoSelectTurnActionComp;
import com.jharter.game.ecs.components.Components.BodyComp;
import com.jharter.game.ecs.components.Components.CardComp;
import com.jharter.game.ecs.components.Components.CardOwnerComp;
import com.jharter.game.ecs.components.Components.ChangeZoneComp;
import com.jharter.game.ecs.components.Components.ChangeZoneCompUtil;
import com.jharter.game.ecs.components.Components.CleanupTurnActionTag;
import com.jharter.game.ecs.components.Components.CollisionComp;
import com.jharter.game.ecs.components.Components.CursorComp;
import com.jharter.game.ecs.components.Components.CursorInputComp;
import com.jharter.game.ecs.components.Components.CursorInputRegulatorComp;
import com.jharter.game.ecs.components.Components.CursorTargetComp;
import com.jharter.game.ecs.components.Components.CursorTargetEvent;
import com.jharter.game.ecs.components.Components.CursorUntargetEvent;
import com.jharter.game.ecs.components.Components.DescriptionComp;
import com.jharter.game.ecs.components.Components.DisabledTag;
import com.jharter.game.ecs.components.Components.EnemyTag;
import com.jharter.game.ecs.components.Components.FocusTag;
import com.jharter.game.ecs.components.Components.FriendTag;
import com.jharter.game.ecs.components.Components.IDComp;
import com.jharter.game.ecs.components.Components.InputComp;
import com.jharter.game.ecs.components.Components.InteractComp;
import com.jharter.game.ecs.components.Components.InvisibleTag;
import com.jharter.game.ecs.components.Components.MultiSpriteComp;
import com.jharter.game.ecs.components.Components.NextTurnPhaseComp;
import com.jharter.game.ecs.components.Components.PlayerTag;
import com.jharter.game.ecs.components.Components.RemoveComp;
import com.jharter.game.ecs.components.Components.SensorComp;
import com.jharter.game.ecs.components.Components.ShapeRenderComp;
import com.jharter.game.ecs.components.Components.SpriteComp;
import com.jharter.game.ecs.components.Components.SpriteCompUtil;
import com.jharter.game.ecs.components.Components.StatsComp;
import com.jharter.game.ecs.components.Components.TargetPositionComp;
import com.jharter.game.ecs.components.Components.TargetableTag;
import com.jharter.game.ecs.components.Components.TextureComp;
import com.jharter.game.ecs.components.Components.TileComp;
import com.jharter.game.ecs.components.Components.TurnActionComp;
import com.jharter.game.ecs.components.Components.TurnPhaseEndBattleTag;
import com.jharter.game.ecs.components.Components.TurnPhaseEndTurnTag;
import com.jharter.game.ecs.components.Components.TurnPhaseNoneTag;
import com.jharter.game.ecs.components.Components.TurnPhasePerformActionsTag;
import com.jharter.game.ecs.components.Components.TurnPhaseSelectActionsTag;
import com.jharter.game.ecs.components.Components.TurnPhaseStartBattleTag;
import com.jharter.game.ecs.components.Components.TurnPhaseStartTurnTag;
import com.jharter.game.ecs.components.Components.TurnPhaseTag;
import com.jharter.game.ecs.components.Components.TurnTimerComp;
import com.jharter.game.ecs.components.Components.TypeComp;
import com.jharter.game.ecs.components.Components.UntargetableTag;
import com.jharter.game.ecs.components.Components.VelocityComp;
import com.jharter.game.ecs.components.Components.VitalsComp;
import com.jharter.game.ecs.components.Components.VitalsCompUtil;
import com.jharter.game.ecs.components.Components.ZoneComp;
import com.jharter.game.ecs.components.Components.ZoneCompUtil;
import com.jharter.game.ecs.components.Components.ZonePositionComp;
import com.jharter.game.ecs.entities.EntityManager;
import com.jharter.game.ecs.entities.IEntityHandler;
import com.jharter.game.util.id.ID;

/**
 * Main mapper class for all components that maps ids to comps and entities directly.
 * It is the sister class of "L" the linker class which maps relationships accross components.
 * This class should remain fully stateless.
 */
public class CompManager {
	
	private final ObjectMap<Class, CompMapper> componentMappersByClass = new ObjectMap<Class, CompMapper>();
	
	private IEntityHandler handler;
	private final CompUtilManager utilManager = new CompUtilManager(this);
	public final EntityManager Entity = new EntityManager(this);
	
	public CompManager(IEntityHandler handler) {
		this.handler = handler;
	}
	
	IEntityHandler getEntityHandler() {
		return handler;
	}
	
	private PooledEngine getEngine() {
		return handler.getEngine();
	}
	
	public class CompMapper<T extends Component> {
		
		private ComponentMapper<T> mapper;
		
		private CompMapper(Class<T> klass) {
			mapper = ComponentMapper.getFor(klass);
		}
		
		public boolean has(ID entityID) {
			return has(Entity.get(entityID));
		}
		
		public boolean has(Entity entity) {
			if(entity == null) {
				return false;
			}
			return mapper.has(entity);
		}
		
		public T get(ID entityID) {
			return get(Entity.get(entityID));
		}
		
		public T get(Entity entity) {
			if(entity == null) {
				return null;
			}
			return mapper.get(entity);
		}
		
	}
	
	public <T extends Component> CompMapper<T> getFor(Class<T> klass) {
		if(!componentMappersByClass.containsKey(klass)) {
			componentMappersByClass.put(klass, new CompMapper<T>(klass));
		}
		return componentMappersByClass.get(klass);
	}
	
	public <T extends Component> T create(Class<T> klass) {
		return getEngine().createComponent(klass);
	}
	
	public <T extends Component> T get(Class<T> klass, Entity entity) {
		if(getFor(klass).has(entity)) {
			return getFor(klass).get(entity);
		}
		return null;
	}
	
	public <T extends Component> boolean toggle(Class<T> klass, Entity entity, boolean add) {
		return add ? add(klass, entity) != null : remove(klass, entity);
	}
	
	public <T extends Component> T add(Class<T> klass, Entity entity) {
		if(getFor(klass).has(entity)) {
			return getFor(klass).get(entity);
		}
		T comp = create(klass);
		entity.add(comp);
		return comp;
	}
	
	public <T extends Component> T getOrAdd(Class<T> klass, Entity entity) {
		if(getFor(klass).has(entity)) {
			return getFor(klass).get(entity);
		}
		T comp = create(klass);
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
	
	public <TA extends Component, TB extends Component> TB swap(Class<TA> klassOld, Class<TB> klassNew, Entity entity) {
		remove(klassOld, entity);
		return getOrAdd(klassNew, entity);
	}
	
	public boolean has(Class<? extends Component> klass, Entity entity) {
		return getFor(klass).has(entity);
	}

	public <T extends Component> boolean toggle(Class<T> klass, ID entityID, boolean add) {
		return toggle(klass, Entity.get(entityID), add);
	}
	
	public <T extends Component> T add(Class<T> klass, ID entityID) {
		return add(klass, Entity.get(entityID));
	}
	
	public <T extends Component> T get(Class<T> klass, ID entityID) {
		return get(klass, Entity.get(entityID));
	}
	
	public <T extends Component> T getOrAdd(Class<T> klass, ID entityID) {
		return getOrAdd(klass, Entity.get(entityID));
	}
	
	public <T extends Component> boolean remove(Class<T> klass, ID entityID) {
		return remove(klass, Entity.get(entityID));
	}
	
	public <TA extends Component, TB extends Component> TB swap(Class<TA> klassOld, Class<TB> klassNew, ID entityID) {
		return swap(klassOld, klassNew, Entity.get(entityID));
	}
	
	public boolean has(Class<? extends Component> klass, ID entityID) {
		return has(klass, Entity.get(entityID));
	}
	
	// Standard Component Mappers
	public final CompMapper<SpriteComp> SpriteComp = getFor(SpriteComp.class);
	public final CompMapper<FocusTag> FocusComp = getFor(FocusTag.class);
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
	public final CompMapper<InvisibleTag> InvisibleComp = getFor(InvisibleTag.class);
	public final CompMapper<InteractComp> InteractComp = getFor(InteractComp.class);
	public final CompMapper<CursorComp> CursorComp = getFor(CursorComp.class);
	public final CompMapper<CursorInputComp> CursorInputComp = getFor(CursorInputComp.class);
	public final CompMapper<CursorInputRegulatorComp> CursorInputRegulatorComp = getFor(CursorInputRegulatorComp.class);
	public final CompMapper<ZoneComp> ZoneComp = getFor(ZoneComp.class);
	public final CompMapper<ZonePositionComp> ZonePositionComp = getFor(ZonePositionComp.class);
	public final CompMapper<CardOwnerComp> CardOwnerComp = getFor(CardOwnerComp.class);
	public final CompMapper<CardComp> CardComp = getFor(CardComp.class);
	public final CompMapper<ActiveTurnActionComp> ActiveTurnActionComp = getFor(ActiveTurnActionComp.class);
	public final CompMapper<TurnActionComp> TurnActionComp = getFor(TurnActionComp.class);
	public final CompMapper<AutoSelectTurnActionComp> AutoSelectTurnActionComp = getFor(AutoSelectTurnActionComp.class);
	public final CompMapper<DescriptionComp> DescriptionComp = getFor(DescriptionComp.class);
	public final CompMapper<VitalsComp> VitalsComp = getFor(VitalsComp.class);
	public final CompMapper<StatsComp> StatsComp = getFor(StatsComp.class);
	public final CompMapper<MultiSpriteComp> MultiSpriteComp = getFor(MultiSpriteComp.class);
	public final CompMapper<ActionReadyTag> ActionReadyComp = getFor(ActionReadyTag.class);
	public final CompMapper<ActionQueueableComp> ActionQueueableComp = getFor(ActionQueueableComp.class);
	public final CompMapper<ActionQueuedComp> ActionQueuedComp = getFor(ActionQueuedComp.class);
	public final CompMapper<CleanupTurnActionTag> ActionSpentComp = getFor(CleanupTurnActionTag.class);
	public final CompMapper<TurnTimerComp> TurnTimerComp = getFor(TurnTimerComp.class);
	public final CompMapper<TargetableTag> TargetableComp = getFor(TargetableTag.class);
	public final CompMapper<UntargetableTag> UntargetableComp = getFor(UntargetableTag.class);
	public final CompMapper<DisabledTag> DisabledComp = getFor(DisabledTag.class);
	public final CompMapper<AnimatingComp> AnimatingComp = getFor(AnimatingComp.class);
	public final CompMapper<ChangeZoneComp> ChangeZoneComp = getFor(ChangeZoneComp.class);
	public final CompMapper<PlayerTag> PlayerComp = getFor(PlayerTag.class);
	public final CompMapper<ShapeRenderComp> ShapeRenderComp = getFor(ShapeRenderComp.class);
	public final CompMapper<NextTurnPhaseComp> NextTurnPhaseComp = getFor(NextTurnPhaseComp.class);
	public final CompMapper<TurnPhaseTag> TurnPhaseComp = getFor(TurnPhaseTag.class);
	public final CompMapper<TurnPhaseStartBattleTag> TurnPhaseStartBattleComp = getFor(TurnPhaseStartBattleTag.class);
	public final CompMapper<TurnPhaseStartTurnTag> TurnPhaseStartTurnComp = getFor(TurnPhaseStartTurnTag.class);
	public final CompMapper<TurnPhaseSelectActionsTag> TurnPhaseSelectFriendActionsComp = getFor(TurnPhaseSelectActionsTag.class);
	public final CompMapper<TurnPhasePerformActionsTag> TurnPhasePerformFriendActionsComp = getFor(TurnPhasePerformActionsTag.class);
	public final CompMapper<TurnPhaseEndTurnTag> TurnPhaseEndTurnComp = getFor(TurnPhaseEndTurnTag.class);
	public final CompMapper<TurnPhaseEndBattleTag> TurnPhaseEndBattleComp = getFor(TurnPhaseEndBattleTag.class);
	public final CompMapper<TurnPhaseNoneTag> TurnPhaseNoneComp = getFor(TurnPhaseNoneTag.class);
	public final CompMapper<ActivePlayerComp> ActivePlayerComp = getFor(ActivePlayerComp.class);
	public final CompMapper<FriendTag> FriendComp = getFor(FriendTag.class);
	public final CompMapper<EnemyTag> EnemyComp = getFor(EnemyTag.class);
	public final CompMapper<CursorTargetComp> CursorTargetComp = getFor(CursorTargetComp.class);
	public final CompMapper<CursorTargetEvent> CursorTargetEvent = getFor(CursorTargetEvent.class);
	public final CompMapper<CursorUntargetEvent> CursorUntargetEvent = getFor(CursorUntargetEvent.class);

	// Component Util Methods
	public SpriteCompUtil util(SpriteComp comp) { return utilManager.get(SpriteCompUtil.class, comp); }
	public ZoneCompUtil util(ZoneComp comp) { return utilManager.get(ZoneCompUtil.class, comp); }
	public VitalsCompUtil util(VitalsComp comp) { return utilManager.get(VitalsCompUtil.class, comp); }
	public VitalsCompUtil util(Entity entity, Class<VitalsComp> compClass) { return utilManager.get(VitalsCompUtil.class, VitalsComp.class, entity); }
	public ChangeZoneCompUtil util(ChangeZoneComp comp) { return utilManager.get(ChangeZoneCompUtil.class, comp) ; }
	
}
