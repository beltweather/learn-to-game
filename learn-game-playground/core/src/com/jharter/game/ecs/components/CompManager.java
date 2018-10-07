package com.jharter.game.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.utils.ObjectMap;
import com.jharter.game.ecs.components.Components.ActionQueueableComp;
import com.jharter.game.ecs.components.Components.ActionQueuedComp;
import com.jharter.game.ecs.components.Components.ActionReadyComp;
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
import com.jharter.game.ecs.components.Components.CleanupTurnActionComp;
import com.jharter.game.ecs.components.Components.CollisionComp;
import com.jharter.game.ecs.components.Components.CursorComp;
import com.jharter.game.ecs.components.Components.CursorInputComp;
import com.jharter.game.ecs.components.Components.CursorInputRegulatorComp;
import com.jharter.game.ecs.components.Components.CursorTargetComp;
import com.jharter.game.ecs.components.Components.DescriptionComp;
import com.jharter.game.ecs.components.Components.DisabledComp;
import com.jharter.game.ecs.components.Components.EnemyComp;
import com.jharter.game.ecs.components.Components.FocusComp;
import com.jharter.game.ecs.components.Components.FriendComp;
import com.jharter.game.ecs.components.Components.IDComp;
import com.jharter.game.ecs.components.Components.InputComp;
import com.jharter.game.ecs.components.Components.InteractComp;
import com.jharter.game.ecs.components.Components.InvisibleComp;
import com.jharter.game.ecs.components.Components.MultiSpriteComp;
import com.jharter.game.ecs.components.Components.NextTurnPhaseComp;
import com.jharter.game.ecs.components.Components.PlayerComp;
import com.jharter.game.ecs.components.Components.RemoveComp;
import com.jharter.game.ecs.components.Components.SensorComp;
import com.jharter.game.ecs.components.Components.ShapeRenderComp;
import com.jharter.game.ecs.components.Components.SpriteComp;
import com.jharter.game.ecs.components.Components.SpriteCompUtil;
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
import com.jharter.game.ecs.components.Components.TurnPhasePerformActionsComp;
import com.jharter.game.ecs.components.Components.TurnPhaseSelectActionsComp;
import com.jharter.game.ecs.components.Components.TurnPhaseStartBattleComp;
import com.jharter.game.ecs.components.Components.TurnPhaseStartTurnComp;
import com.jharter.game.ecs.components.Components.TurnTimerComp;
import com.jharter.game.ecs.components.Components.TypeComp;
import com.jharter.game.ecs.components.Components.UntargetableComp;
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
	public final CompMapper<CardOwnerComp> CardOwnerComp = getFor(CardOwnerComp.class);
	public final CompMapper<CardComp> CardComp = getFor(CardComp.class);
	public final CompMapper<ActiveTurnActionComp> ActiveTurnActionComp = getFor(ActiveTurnActionComp.class);
	public final CompMapper<TurnActionComp> TurnActionComp = getFor(TurnActionComp.class);
	public final CompMapper<AutoSelectTurnActionComp> AutoSelectTurnActionComp = getFor(AutoSelectTurnActionComp.class);
	public final CompMapper<DescriptionComp> DescriptionComp = getFor(DescriptionComp.class);
	public final CompMapper<VitalsComp> VitalsComp = getFor(VitalsComp.class);
	public final CompMapper<StatsComp> StatsComp = getFor(StatsComp.class);
	public final CompMapper<MultiSpriteComp> MultiSpriteComp = getFor(MultiSpriteComp.class);
	public final CompMapper<ActionReadyComp> ActionReadyComp = getFor(ActionReadyComp.class);
	public final CompMapper<ActionQueueableComp> ActionQueueableComp = getFor(ActionQueueableComp.class);
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
	public final CompMapper<NextTurnPhaseComp> NextTurnPhaseComp = getFor(NextTurnPhaseComp.class);
	public final CompMapper<TurnPhaseComp> TurnPhaseComp = getFor(TurnPhaseComp.class);
	public final CompMapper<TurnPhaseStartBattleComp> TurnPhaseStartBattleComp = getFor(TurnPhaseStartBattleComp.class);
	public final CompMapper<TurnPhaseStartTurnComp> TurnPhaseStartTurnComp = getFor(TurnPhaseStartTurnComp.class);
	public final CompMapper<TurnPhaseSelectActionsComp> TurnPhaseSelectFriendActionsComp = getFor(TurnPhaseSelectActionsComp.class);
	public final CompMapper<TurnPhasePerformActionsComp> TurnPhasePerformFriendActionsComp = getFor(TurnPhasePerformActionsComp.class);
	public final CompMapper<TurnPhaseEndTurnComp> TurnPhaseEndTurnComp = getFor(TurnPhaseEndTurnComp.class);
	public final CompMapper<TurnPhaseEndBattleComp> TurnPhaseEndBattleComp = getFor(TurnPhaseEndBattleComp.class);
	public final CompMapper<TurnPhaseNoneComp> TurnPhaseNoneComp = getFor(TurnPhaseNoneComp.class);
	public final CompMapper<ActivePlayerComp> ActivePlayerComp = getFor(ActivePlayerComp.class);
	public final CompMapper<FriendComp> FriendComp = getFor(FriendComp.class);
	public final CompMapper<EnemyComp> EnemyComp = getFor(EnemyComp.class);
	public final CompMapper<CursorTargetComp> CursorTargetComp = getFor(CursorTargetComp.class);

	// Component Util Methods
	public SpriteCompUtil util(SpriteComp comp) { return utilManager.get(SpriteCompUtil.class, comp); }
	public ZoneCompUtil util(ZoneComp comp) { return utilManager.get(ZoneCompUtil.class, comp); }
	public VitalsCompUtil util(VitalsComp comp) { return utilManager.get(VitalsCompUtil.class, comp); }
	public VitalsCompUtil util(Entity entity, Class<VitalsComp> compClass) { return utilManager.get(VitalsCompUtil.class, VitalsComp.class, entity); }
	public ChangeZoneCompUtil util(ChangeZoneComp comp) { return utilManager.get(ChangeZoneCompUtil.class, comp) ; }
	
}
