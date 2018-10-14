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
import com.jharter.game.ecs.components.Components.AssociatedTurnActionsComp;
import com.jharter.game.ecs.components.Components.AutoSelectTurnActionComp;
import com.jharter.game.ecs.components.Components.BodyComp;
import com.jharter.game.ecs.components.Components.CardComp;
import com.jharter.game.ecs.components.Components.CardOwnerComp;
import com.jharter.game.ecs.components.Components.ChangeZoneComp;
import com.jharter.game.ecs.components.Components.ChangeZoneCompUtil;
import com.jharter.game.ecs.components.Components.CleanupTurnActionTag;
import com.jharter.game.ecs.components.Components.CollisionComp;
import com.jharter.game.ecs.components.Components.CursorChangedZoneEvent;
import com.jharter.game.ecs.components.Components.CursorComp;
import com.jharter.game.ecs.components.Components.CursorInputComp;
import com.jharter.game.ecs.components.Components.CursorInputRegulatorComp;
import com.jharter.game.ecs.components.Components.CursorTargetComp;
import com.jharter.game.ecs.components.Components.CursorTargetEvent;
import com.jharter.game.ecs.components.Components.CursorUntargetEvent;
import com.jharter.game.ecs.components.Components.DescriptionComp;
import com.jharter.game.ecs.components.Components.DisabledTag;
import com.jharter.game.ecs.components.Components.DiscardCardTag;
import com.jharter.game.ecs.components.Components.EnemyTag;
import com.jharter.game.ecs.components.Components.FocusTag;
import com.jharter.game.ecs.components.Components.FriendTag;
import com.jharter.game.ecs.components.Components.IDComp;
import com.jharter.game.ecs.components.Components.InputComp;
import com.jharter.game.ecs.components.Components.InteractComp;
import com.jharter.game.ecs.components.Components.InvisibleTag;
import com.jharter.game.ecs.components.Components.MultiSpriteComp;
import com.jharter.game.ecs.components.Components.NextTurnPhaseComp;
import com.jharter.game.ecs.components.Components.PendingTurnActionTag;
import com.jharter.game.ecs.components.Components.PendingVitalsComp;
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

	@SuppressWarnings("rawtypes")
	private final ObjectMap<Class, CompMapper> compMappersByClass = new ObjectMap<Class, CompMapper>();

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

		private Class<T> compClass;
		private ComponentMapper<T> mapper;

		private CompMapper(Class<T> compClass) {
			this.mapper = ComponentMapper.getFor(compClass);
			this.compClass = compClass;
		}

		public T create() {
			return getEngine().createComponent(compClass);
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

		public T add(ID entityID) {
			return add(Entity.get(entityID));
		}

		public T add(Entity entity) {
			if(entity == null) {
				return null;
			}
			if(has(entity)) {
				return get(entity);
			}
			T comp = create();
			entity.add(comp);
			return comp;
		}

		public T getOrAdd(ID entityID) {
			return getOrAdd(Entity.get(entityID));
		}

		public T getOrAdd(Entity entity) {
			if(entity == null) {
				return null;
			}
			if(has(entity)) {
				return get(entity);
			}
			return add(entity);
		}

		public boolean remove(ID entityID) {
			return remove(Entity.get(entityID));
		}

		public boolean remove(Entity entity) {
			if(entity == null || !has(entity)) {
				return false;
			}
			entity.remove(compClass);
			return true;
		}

		public boolean toggle(ID entityID) {
			return toggle(Entity.get(entityID));
		}

		public boolean toggle(Entity entity) {
			return toggle(entity, !has(entity));
		}

		public boolean toggle(ID entityID, boolean add) {
			return toggle(Entity.get(entityID), add);
		}

		public boolean toggle(Entity entity, boolean add) {
			if(entity == null) {
				return false;
			}
			if(add) {
				return add(entity) != null;
			}
			return remove(entity);
		}

		public <TB extends Component> TB swap(Class<TB> compClassNew, ID entityID) {
			return swap(compClassNew, Entity.get(entityID));
		}

		public <TB extends Component> TB swap(Class<TB> compClassNew, Entity entity) {
			remove(entity);
			return getFor(compClassNew).getOrAdd(entity);
		}

	}

	@SuppressWarnings("unchecked")
	public <T extends Component> CompMapper<T> getFor(Class<T> compClass) {
		if(!compMappersByClass.containsKey(compClass)) {
			compMappersByClass.put(compClass, new CompMapper<T>(compClass));
		}
		return compMappersByClass.get(compClass);
	}

	// Standard Component Mappers
	public final CompMapper<ActionQueueableComp> ActionQueueableComp = getFor(ActionQueueableComp.class);
	public final CompMapper<ActionQueuedComp> ActionQueuedComp = getFor(ActionQueuedComp.class);
	public final CompMapper<ActionReadyTag> ActionReadyComp = getFor(ActionReadyTag.class);
	public final CompMapper<ActivePlayerComp> ActivePlayerComp = getFor(ActivePlayerComp.class);
	public final CompMapper<ActiveTurnActionComp> ActiveTurnActionComp = getFor(ActiveTurnActionComp.class);
	public final CompMapper<AnimatingComp> AnimatingComp = getFor(AnimatingComp.class);
	public final CompMapper<AnimationComp> AnimationComp = getFor(AnimationComp.class);
	public final CompMapper<AssociatedTurnActionsComp> AssociatedTurnActionsComp = getFor(AssociatedTurnActionsComp.class);
	public final CompMapper<AutoSelectTurnActionComp> AutoSelectTurnActionComp = getFor(AutoSelectTurnActionComp.class);
	public final CompMapper<BodyComp> BodyComp = getFor(BodyComp.class);
	public final CompMapper<CardComp> CardComp = getFor(CardComp.class);
	public final CompMapper<CardOwnerComp> CardOwnerComp = getFor(CardOwnerComp.class);
	public final CompMapper<ChangeZoneComp> ChangeZoneComp = getFor(ChangeZoneComp.class);
	public final CompMapper<CleanupTurnActionTag> ActionSpentComp = getFor(CleanupTurnActionTag.class);
	public final CompMapper<CleanupTurnActionTag> CleanupTurnActionTag = getFor(CleanupTurnActionTag.class);
	public final CompMapper<CollisionComp> CollisionComp = getFor(CollisionComp.class);
	public final CompMapper<CursorChangedZoneEvent> CursorChangedZoneEvent = getFor(CursorChangedZoneEvent.class);
	public final CompMapper<CursorComp> CursorComp = getFor(CursorComp.class);
	public final CompMapper<CursorInputComp> CursorInputComp = getFor(CursorInputComp.class);
	public final CompMapper<CursorInputRegulatorComp> CursorInputRegulatorComp = getFor(CursorInputRegulatorComp.class);
	public final CompMapper<CursorTargetComp> CursorTargetComp = getFor(CursorTargetComp.class);
	public final CompMapper<CursorTargetEvent> CursorTargetEvent = getFor(CursorTargetEvent.class);
	public final CompMapper<CursorUntargetEvent> CursorUntargetEvent = getFor(CursorUntargetEvent.class);
	public final CompMapper<DescriptionComp> DescriptionComp = getFor(DescriptionComp.class);
	public final CompMapper<DisabledTag> DisabledComp = getFor(DisabledTag.class);
	public final CompMapper<DisabledTag> DisabledTag = getFor(DisabledTag.class);
	public final CompMapper<DiscardCardTag> DiscardCardTag = getFor(DiscardCardTag.class);
	public final CompMapper<EnemyTag> EnemyComp = getFor(EnemyTag.class);
	public final CompMapper<FocusTag> FocusComp = getFor(FocusTag.class);
	public final CompMapper<FriendTag> FriendComp = getFor(FriendTag.class);
	public final CompMapper<IDComp> IDComp = getFor(IDComp.class);
	public final CompMapper<InputComp> InputComp = getFor(InputComp.class);
	public final CompMapper<InteractComp> InteractComp = getFor(InteractComp.class);
	public final CompMapper<InvisibleTag> InvisibleTag = getFor(InvisibleTag.class);
	public final CompMapper<MultiSpriteComp> MultiSpriteComp = getFor(MultiSpriteComp.class);
	public final CompMapper<NextTurnPhaseComp> NextTurnPhaseComp = getFor(NextTurnPhaseComp.class);
	public final CompMapper<PendingTurnActionTag> PendingTurnActionTag = getFor(PendingTurnActionTag.class);
	public final CompMapper<PendingVitalsComp> PendingVitalsComp = getFor(PendingVitalsComp.class);
	public final CompMapper<PlayerTag> PlayerComp = getFor(PlayerTag.class);
	public final CompMapper<RemoveComp> RemoveComp = getFor(RemoveComp.class);
	public final CompMapper<SensorComp> SensorComp = getFor(SensorComp.class);
	public final CompMapper<ShapeRenderComp> ShapeRenderComp = getFor(ShapeRenderComp.class);
	public final CompMapper<SpriteComp> SpriteComp = getFor(SpriteComp.class);
	public final CompMapper<StatsComp> StatsComp = getFor(StatsComp.class);
	public final CompMapper<TargetableTag> TargetableComp = getFor(TargetableTag.class);
	public final CompMapper<TargetableTag> TargetableTag = getFor(TargetableTag.class);
	public final CompMapper<TargetPositionComp> TargetPositionComp = getFor(TargetPositionComp.class);
	public final CompMapper<TextureComp> TextureComp = getFor(TextureComp.class);
	public final CompMapper<TileComp> TileComp = getFor(TileComp.class);
	public final CompMapper<TurnActionComp> TurnActionComp = getFor(TurnActionComp.class);
	public final CompMapper<TurnPhaseEndBattleTag> TurnPhaseEndBattleComp = getFor(TurnPhaseEndBattleTag.class);
	public final CompMapper<TurnPhaseEndTurnTag> TurnPhaseEndTurnComp = getFor(TurnPhaseEndTurnTag.class);
	public final CompMapper<TurnPhaseNoneTag> TurnPhaseNoneComp = getFor(TurnPhaseNoneTag.class);
	public final CompMapper<TurnPhasePerformActionsTag> TurnPhasePerformFriendActionsComp = getFor(TurnPhasePerformActionsTag.class);
	public final CompMapper<TurnPhaseSelectActionsTag> TurnPhaseSelectFriendActionsComp = getFor(TurnPhaseSelectActionsTag.class);
	public final CompMapper<TurnPhaseStartBattleTag> TurnPhaseStartBattleComp = getFor(TurnPhaseStartBattleTag.class);
	public final CompMapper<TurnPhaseStartTurnTag> TurnPhaseStartTurnComp = getFor(TurnPhaseStartTurnTag.class);
	public final CompMapper<TurnPhaseTag> TurnPhaseComp = getFor(TurnPhaseTag.class);
	public final CompMapper<TurnTimerComp> TurnTimerComp = getFor(TurnTimerComp.class);
	public final CompMapper<TypeComp> TypeComp = getFor(TypeComp.class);
	public final CompMapper<UntargetableTag> UntargetableComp = getFor(UntargetableTag.class);
	public final CompMapper<UntargetableTag> UntargetableTag = getFor(UntargetableTag.class);
	public final CompMapper<VelocityComp> VelocityComp = getFor(VelocityComp.class);
	public final CompMapper<VitalsComp> VitalsComp = getFor(VitalsComp.class);
	public final CompMapper<ZoneComp> ZoneComp = getFor(ZoneComp.class);
	public final CompMapper<ZonePositionComp> ZonePositionComp = getFor(ZonePositionComp.class);


	// To insert template type: cm -> ctrl+space -> enter

	// Component Util Methods
	public SpriteCompUtil util(SpriteComp comp) { return utilManager.get(SpriteCompUtil.class, comp); }
	public ZoneCompUtil util(ZoneComp comp) { return utilManager.get(ZoneCompUtil.class, comp); }
	public VitalsCompUtil util(VitalsComp comp) { return utilManager.get(VitalsCompUtil.class, comp); }
	public VitalsCompUtil util(Entity entity, Class<VitalsComp> compClass) { return utilManager.get(VitalsCompUtil.class, VitalsComp.class, entity); }
	public ChangeZoneCompUtil util(ChangeZoneComp comp) { return utilManager.get(ChangeZoneCompUtil.class, comp) ; }

}
