package com.jharter.game.ashley.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.utils.ObjectMap;
import com.jharter.game.ashley.components.Components.ActionQueuedComp;
import com.jharter.game.ashley.components.Components.ActionReadyComp;
import com.jharter.game.ashley.components.Components.ActionSpentComp;
import com.jharter.game.ashley.components.Components.ActiveCardComp;
import com.jharter.game.ashley.components.Components.ActivePlayerComp;
import com.jharter.game.ashley.components.Components.AnimatingComp;
import com.jharter.game.ashley.components.Components.AnimationComp;
import com.jharter.game.ashley.components.Components.BattleAvatarComp;
import com.jharter.game.ashley.components.Components.BodyComp;
import com.jharter.game.ashley.components.Components.CardComp;
import com.jharter.game.ashley.components.Components.ChangeZoneComp;
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

/**
 * Main mapper class for all components that maps ids to comps and entities directly.
 * It is the sister class of "L" the linker class which maps relationships accross components.
 * This class should remain fully stateless.
 */
public class Comp {
	
	private static final ObjectMap<Class, ComponentMapper> componentMappersByClass = new ObjectMap<Class, ComponentMapper>();
	
	private Comp() {}
	
	public static <T extends Component> ComponentMapper<T> getFor(Class<T> klass) {
		if(!componentMappersByClass.containsKey(klass)) {
			componentMappersByClass.put(klass, ComponentMapper.getFor(klass));
		}
		return componentMappersByClass.get(klass);
	}
	
	public static <T extends Component> T create(Engine engine, Class<T> klass) {
		if(engine == null || !(engine instanceof PooledEngine)) {
			return null;
		}
		return ((PooledEngine) engine).createComponent(klass);
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
		if(!getFor(klass).has(entity)) {
			return false;
		}
		entity.remove(klass);
		return true;
	}
	
	public static boolean has(Class<? extends Component> klass, Entity entity) {
		return getFor(klass).has(entity);
	}
	
	public static final ComponentMapper<SpriteComp> SpriteComp = getFor(SpriteComp.class);
	public static final ComponentMapper<BattleAvatarComp> BattleAvatarComp = getFor(BattleAvatarComp.class);
	public static final ComponentMapper<FocusComp> FocusComp = getFor(FocusComp.class);
	public static final ComponentMapper<IDComp> IDComp = getFor(IDComp.class);
	public static final ComponentMapper<TypeComp> TypeComp = getFor(TypeComp.class);
	public static final ComponentMapper<TileComp> TileComp = getFor(TileComp.class);
	public static final ComponentMapper<TextureComp> TextureComp = getFor(TextureComp.class);
	public static final ComponentMapper<AnimationComp> AnimationComp = getFor(AnimationComp.class);
	public static final ComponentMapper<BodyComp> BodyComp = getFor(BodyComp.class);
	public static final ComponentMapper<SensorComp> SensorComp = getFor(SensorComp.class);
	public static final ComponentMapper<TargetPositionComp> TargetPositionComp = getFor(TargetPositionComp.class);
	public static final ComponentMapper<VelocityComp> VelocityComp = getFor(VelocityComp.class);
	public static final ComponentMapper<CollisionComp> CollisionComp = getFor(CollisionComp.class);
	public static final ComponentMapper<RemoveComp> RemoveComp = getFor(RemoveComp.class);
	public static final ComponentMapper<InputComp> InputComp = getFor(InputComp.class);
	public static final ComponentMapper<InvisibleComp> InvisibleComp = getFor(InvisibleComp.class);
	public static final ComponentMapper<InteractComp> InteractComp = getFor(InteractComp.class);
	public static final ComponentMapper<CursorComp> CursorComp = getFor(CursorComp.class);
	public static final ComponentMapper<CursorInputComp> CursorInputComp = getFor(CursorInputComp.class);
	public static final ComponentMapper<CursorInputRegulatorComp> CursorInputRegulatorComp = getFor(CursorInputRegulatorComp.class);
	public static final ComponentMapper<ZoneComp> ZoneComp = getFor(ZoneComp.class);
	public static final ComponentMapper<ZonePositionComp> ZonePositionComp = getFor(ZonePositionComp.class);
	public static final ComponentMapper<CardComp> CardComp = getFor(CardComp.class);
	public static final ComponentMapper<ActiveCardComp> ActiveCardComp = getFor(ActiveCardComp.class);
	public static final ComponentMapper<TurnActionComp> TurnActionComp = getFor(TurnActionComp.class);
	public static final ComponentMapper<DescriptionComp> DescriptionComp = getFor(DescriptionComp.class);
	public static final ComponentMapper<VitalsComp> VitalsComp = getFor(VitalsComp.class);
	public static final ComponentMapper<StatsComp> StatsComp = getFor(StatsComp.class);
	public static final ComponentMapper<MultiSpriteComp> MultiSpriteComp = getFor(MultiSpriteComp.class);
	public static final ComponentMapper<ActionReadyComp> ActionReadyComp = getFor(ActionReadyComp.class);
	public static final ComponentMapper<ActionQueuedComp> ActionQueuedComp = getFor(ActionQueuedComp.class);
	public static final ComponentMapper<ActionSpentComp> ActionSpentComp = getFor(ActionSpentComp.class);
	public static final ComponentMapper<TurnTimerComp> TurnTimerComp = getFor(TurnTimerComp.class);
	public static final ComponentMapper<UntargetableComp> UntargetableComp = getFor(UntargetableComp.class);
	public static final ComponentMapper<DisabledComp> DisabledComp = getFor(DisabledComp.class);
	public static final ComponentMapper<AnimatingComp> AnimatingComp = getFor(AnimatingComp.class);
	public static final ComponentMapper<ChangeZoneComp> ChangeZoneComp = getFor(ChangeZoneComp.class);
	public static final ComponentMapper<PlayerComp> PlayerComp = getFor(PlayerComp.class);
	public static final ComponentMapper<ActivePlayerComp> ActivePlayerComp = getFor(ActivePlayerComp.class);
	public static final ComponentMapper<ShapeRenderComp> ShapeRenderComp = getFor(ShapeRenderComp.class);
	public static final ComponentMapper<TurnPhaseComp> TurnPhaseComp = getFor(TurnPhaseComp.class);
	public static final ComponentMapper<TurnPhaseStartBattleComp> TurnPhaseStartBattleComp = getFor(TurnPhaseStartBattleComp.class);
	public static final ComponentMapper<TurnPhaseStartTurnComp> TurnPhaseStartTurnComp = getFor(TurnPhaseStartTurnComp.class);
	public static final ComponentMapper<TurnPhaseSelectEnemyActionsComp> TurnPhaseSelectEnemyActionsComp = getFor(TurnPhaseSelectEnemyActionsComp.class);
	public static final ComponentMapper<TurnPhaseSelectFriendActionsComp> TurnPhaseSelectFriendActionsComp = getFor(TurnPhaseSelectFriendActionsComp.class);
	public static final ComponentMapper<TurnPhasePerformFriendActionsComp> TurnPhasePerformFriendActionsComp = getFor(TurnPhasePerformFriendActionsComp.class);
	public static final ComponentMapper<TurnPhasePerformEnemyActionsComp> TurnPhasePerformEnemyActionsComp = getFor(TurnPhasePerformEnemyActionsComp.class);
	public static final ComponentMapper<TurnPhaseEndTurnComp> TurnPhaseEndTurnComp = getFor(TurnPhaseEndTurnComp.class);
	public static final ComponentMapper<TurnPhaseEndBattleComp> TurnPhaseEndBattleComp = getFor(TurnPhaseEndBattleComp.class);
	public static final ComponentMapper<TurnPhaseNoneComp> TurnPhaseNoneComp = getFor(TurnPhaseNoneComp.class);
	
}
