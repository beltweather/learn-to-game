package com.jharter.game.ashley.components;

import com.badlogic.ashley.core.ComponentMapper;
import com.jharter.game.ashley.components.Components.AnimationComp;
import com.jharter.game.ashley.components.Components.BodyComp;
import com.jharter.game.ashley.components.Components.CollisionComp;
import com.jharter.game.ashley.components.Components.FocusComp;
import com.jharter.game.ashley.components.Components.IDComp;
import com.jharter.game.ashley.components.Components.InputComp;
import com.jharter.game.ashley.components.Components.InteractComp;
import com.jharter.game.ashley.components.Components.InvisibleComp;
import com.jharter.game.ashley.components.Components.PositionComp;
import com.jharter.game.ashley.components.Components.RemoveComp;
import com.jharter.game.ashley.components.Components.SensorComp;
import com.jharter.game.ashley.components.Components.SizeComp;
import com.jharter.game.ashley.components.Components.TargetPositionComp;
import com.jharter.game.ashley.components.Components.TileComp;
import com.jharter.game.ashley.components.Components.TypeComp;
import com.jharter.game.ashley.components.Components.VelocityComp;
import com.jharter.game.ashley.components.Components.VisualComp;

public class Mapper {
	
	public static final ComponentMapper<FocusComp> FocusComp = ComponentMapper.getFor(FocusComp.class);
	public static final ComponentMapper<IDComp> IDComp = ComponentMapper.getFor(IDComp.class);
	public static final ComponentMapper<PositionComp> PositionComp = ComponentMapper.getFor(PositionComp.class);
	public static final ComponentMapper<SizeComp> SizeComp = ComponentMapper.getFor(SizeComp.class);
	public static final ComponentMapper<TypeComp> TypeComp = ComponentMapper.getFor(TypeComp.class);
	public static final ComponentMapper<TileComp> TileComp = ComponentMapper.getFor(TileComp.class);
	public static final ComponentMapper<VisualComp> VisualComp = ComponentMapper.getFor(VisualComp.class);
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
	
}
