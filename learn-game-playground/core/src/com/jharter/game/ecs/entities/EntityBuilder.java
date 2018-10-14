package com.jharter.game.ecs.entities;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Pools;
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
import com.jharter.game.ecs.components.Components.CleanupTurnActionTag;
import com.jharter.game.ecs.components.Components.CollisionComp;
import com.jharter.game.ecs.components.Components.CursorComp;
import com.jharter.game.ecs.components.Components.CursorInputComp;
import com.jharter.game.ecs.components.Components.CursorInputRegulatorComp;
import com.jharter.game.ecs.components.Components.CursorTargetComp;
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
import com.jharter.game.ecs.components.Components.PendingVitalsComp;
import com.jharter.game.ecs.components.Components.PlayerTag;
import com.jharter.game.ecs.components.Components.RemoveComp;
import com.jharter.game.ecs.components.Components.SensorComp;
import com.jharter.game.ecs.components.Components.ShapeRenderComp;
import com.jharter.game.ecs.components.Components.SpriteComp;
import com.jharter.game.ecs.components.Components.StatsComp;
import com.jharter.game.ecs.components.Components.TargetPositionComp;
import com.jharter.game.ecs.components.Components.TextureComp;
import com.jharter.game.ecs.components.Components.TileComp;
import com.jharter.game.ecs.components.Components.TurnActionComp;
import com.jharter.game.ecs.components.Components.TurnPhaseStartBattleTag;
import com.jharter.game.ecs.components.Components.TurnPhaseTag;
import com.jharter.game.ecs.components.Components.TurnTimerComp;
import com.jharter.game.ecs.components.Components.TypeComp;
import com.jharter.game.ecs.components.Components.UntargetableTag;
import com.jharter.game.ecs.components.Components.VelocityComp;
import com.jharter.game.ecs.components.Components.VitalsComp;
import com.jharter.game.ecs.components.Components.ZoneComp;
import com.jharter.game.ecs.components.Components.ZonePositionComp;

public class EntityBuilder implements Poolable {
	
	public static EntityBuilder create(PooledEngine engine) {
		EntityBuilder builder = Pools.get(EntityBuilder.class).obtain();
		builder.init(engine);
		return builder;
	}
	 
	private ObjectMap<Class<? extends Component>, Component> comps = new ObjectMap<Class<? extends Component>, Component>();
	private PooledEngine engine;
	private Entity entity;
	
	private EntityBuilder() {}
	
	private void init(PooledEngine engine) {
		this.engine = engine;
		this.entity = engine.createEntity();
	}
	
	public Entity Entity() {
		return entity;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Component> T get(Class<T> componentClass) {
		if(!comps.containsKey(componentClass)) {
			T comp = engine.createComponent(componentClass);
			entity.add(comp);
			comps.put(componentClass, comp);
			return comp;
		}
		return (T) comps.get(componentClass);
	}
	
	public Family getFamily() {
		Array<Class<? extends Component>> classes = new Array<Class<? extends Component>>();
		for(Class<? extends Component> klass : comps.keys()) {
			if(comps.get(klass) != null) {
				classes.add(klass);
			}
		}
		return Family.all((Class<? extends Component>[])classes.toArray()).get();
	}
	
	public void free() {
		Pools.get(EntityBuilder.class).free(this);
	}
	
	@Override
	public void reset() {
		entity = null;
		engine = null;
		comps.clear();
	}

	public SpriteComp SpriteComp() { return get(SpriteComp.class); }
	public FocusTag FocusComp() { return get(FocusTag.class); }
	public IDComp IDComp() { return get(IDComp.class); }
	public TypeComp TypeComp() { return get(TypeComp.class); }
	public TileComp TileComp() { return get(TileComp.class); }
	public TextureComp TextureComp() { return get(TextureComp.class); }
	public AnimationComp AnimationComp() { return get(AnimationComp.class); }
	public BodyComp BodyComp() { return get(BodyComp.class); }
	public SensorComp SensorComp() { return get(SensorComp.class); }
	public TargetPositionComp TargetPositionComp() { return get(TargetPositionComp.class); }
	public VelocityComp VelocityComp() { return get(VelocityComp.class); }
	public CollisionComp CollisionComp() { return get(CollisionComp.class); }
	public RemoveComp RemoveComp() { return get(RemoveComp.class); }
	public InputComp InputComp() { return get(InputComp.class); }
	public InteractComp InteractComp() { return get(InteractComp.class); }
	public InvisibleTag InvisibleComp() { return get(InvisibleTag.class); }
	public CursorComp CursorComp() { return get(CursorComp.class); }
	public CursorInputComp CursorInputComp() { return get(CursorInputComp.class); }
	public CursorInputRegulatorComp CursorInputRegulatorComp() { return get(CursorInputRegulatorComp.class); }
	public ZoneComp ZoneComp() { return get(ZoneComp.class); }
	public ZonePositionComp ZonePositionComp() { return get(ZonePositionComp.class); }
	public CardOwnerComp CardOwnerComp() { return get(CardOwnerComp.class); }
	public CardComp CardComp() { return get(CardComp.class); }
	public ActiveTurnActionComp ActiveTurnActionComp() { return get(ActiveTurnActionComp.class); }
	public TurnActionComp TurnActionComp() { return get(TurnActionComp.class); }
	public AutoSelectTurnActionComp AutoSelectTurnActionComp() { return get(AutoSelectTurnActionComp.class); }
	public DescriptionComp DescriptionComp() { return get(DescriptionComp.class); }
	public VitalsComp VitalsComp() { return get(VitalsComp.class); }
	public PendingVitalsComp PendingVitalsComp() { return get(PendingVitalsComp.class); }
	public StatsComp StatsComp() { return get(StatsComp.class); }
	public MultiSpriteComp MultiSpriteComp() { return get(MultiSpriteComp.class); }
	public ActionReadyTag ActionReadyComp() { return get(ActionReadyTag.class); }
	public ActionQueuedComp ActionQueuedComp() { return get(ActionQueuedComp.class); }
	public CleanupTurnActionTag CleanupTurnActionComp() { return get(CleanupTurnActionTag.class); }
	public TurnTimerComp TurnTimerComp() { return get(TurnTimerComp.class); }
	public UntargetableTag UntargetableComp() { return get(UntargetableTag.class); }
	public TurnPhaseTag TurnPhaseComp() { return get(TurnPhaseTag.class); }
	public TurnPhaseStartBattleTag TurnPhaseStartBattleComp() { return get(TurnPhaseStartBattleTag.class); }
	public DisabledTag DisabledComp() { return get(DisabledTag.class); }
	public AnimatingComp AnimatingComp() { return get(AnimatingComp.class); }
	public ChangeZoneComp ChangeZoneComp() { return get(ChangeZoneComp.class); }
	public PlayerTag PlayerComp() { return get(PlayerTag.class); }
	public ActivePlayerComp ActivePlayerComp() { return get(ActivePlayerComp.class); }
	public ShapeRenderComp ShapeRenderComp() { return get(ShapeRenderComp.class); }
	public FriendTag FriendComp() { return get(FriendTag.class); }
	public EnemyTag EnemyComp() { return get(EnemyTag.class); }
	public CursorTargetComp CursorTargetComp() { return get(CursorTargetComp.class); }
	public AssociatedTurnActionsComp AssociatedTurnActionsComp() { return get(AssociatedTurnActionsComp.class); }
	
}
