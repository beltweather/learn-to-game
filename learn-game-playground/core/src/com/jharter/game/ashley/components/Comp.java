package com.jharter.game.ashley.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
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
 */
public class Comp {
	
	private Comp() {}
	
	public static PooledEngine engine;
	
	public static <T extends Component> T create(Class<T> klass) {
		if(engine == null) {
			return null;
		}
		return engine.createComponent(klass);
	}
	
	public static <T extends Component> T getOrAdd(Class<T> klass, Entity entity) {
		if(ComponentMapper.getFor(klass).has(entity)) {
			return ComponentMapper.getFor(klass).get(entity);
		}
		T comp = create(klass);
		entity.add(comp);
		return comp;
	}
	
	public static <T extends Component> boolean remove(Class<T> klass, Entity entity) {
		if(!ComponentMapper.getFor(klass).has(entity)) {
			return false;
		}
		entity.remove(klass);
		return true;
	}
	
	public static boolean has(Class<? extends Component> klass, Entity entity) {
		return ComponentMapper.getFor(klass).has(entity);
	}
	
	public static final ComponentMapper<SpriteComp> SpriteComp = ComponentMapper.getFor(SpriteComp.class);
	public static final ComponentMapper<BattleAvatarComp> BattleAvatarComp = ComponentMapper.getFor(BattleAvatarComp.class);
	public static final ComponentMapper<FocusComp> FocusComp = ComponentMapper.getFor(FocusComp.class);
	public static final ComponentMapper<IDComp> IDComp = ComponentMapper.getFor(IDComp.class);
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
	public static final ComponentMapper<ZoneComp> ZoneComp = ComponentMapper.getFor(ZoneComp.class);
	public static final ComponentMapper<ZonePositionComp> ZonePositionComp = ComponentMapper.getFor(ZonePositionComp.class);
	public static final ComponentMapper<CardComp> CardComp = ComponentMapper.getFor(CardComp.class);
	public static final ComponentMapper<ActiveCardComp> ActiveCardComp = ComponentMapper.getFor(ActiveCardComp.class);
	public static final ComponentMapper<TurnActionComp> TurnActionComp = ComponentMapper.getFor(TurnActionComp.class);
	public static final ComponentMapper<DescriptionComp> DescriptionComp = ComponentMapper.getFor(DescriptionComp.class);
	public static final ComponentMapper<VitalsComp> VitalsComp = ComponentMapper.getFor(VitalsComp.class);
	public static final ComponentMapper<StatsComp> StatsComp = ComponentMapper.getFor(StatsComp.class);
	public static final ComponentMapper<MultiSpriteComp> MultiSpriteComp = ComponentMapper.getFor(MultiSpriteComp.class);
	public static final ComponentMapper<ActionReadyComp> ActionReadyComp = ComponentMapper.getFor(ActionReadyComp.class);
	public static final ComponentMapper<ActionQueuedComp> ActionQueuedComp = ComponentMapper.getFor(ActionQueuedComp.class);
	public static final ComponentMapper<ActionSpentComp> ActionSpentComp = ComponentMapper.getFor(ActionSpentComp.class);
	public static final ComponentMapper<TurnTimerComp> TurnTimerComp = ComponentMapper.getFor(TurnTimerComp.class);
	public static final ComponentMapper<UntargetableComp> UntargetableComp = ComponentMapper.getFor(UntargetableComp.class);
	public static final ComponentMapper<TurnPhaseComp> TurnPhaseComp = ComponentMapper.getFor(TurnPhaseComp.class);
	public static final ComponentMapper<DisabledComp> DisabledComp = ComponentMapper.getFor(DisabledComp.class);
	public static final ComponentMapper<AnimatingComp> AnimatingComp = ComponentMapper.getFor(AnimatingComp.class);
	public static final ComponentMapper<ChangeZoneComp> ChangeZoneComp = ComponentMapper.getFor(ChangeZoneComp.class);
	public static final ComponentMapper<PlayerComp> PlayerComp = ComponentMapper.getFor(PlayerComp.class);
	public static final ComponentMapper<ActivePlayerComp> ActivePlayerComp = ComponentMapper.getFor(ActivePlayerComp.class);
	public static final ComponentMapper<ShapeRenderComp> ShapeRenderComp = ComponentMapper.getFor(ShapeRenderComp.class);
	
}
