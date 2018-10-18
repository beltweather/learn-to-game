package com.jharter.game.ecs.helpers;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.jharter.game.ecs.components.Components.ZoneComp;
import com.jharter.game.ecs.entities.EntityBuildUtil;
import com.jharter.game.ecs.entities.EntityBuilder;
import com.jharter.game.ecs.entities.EntityHandler;
import com.jharter.game.ecs.entities.IEntityHandler;
import com.jharter.game.util.U;
import com.jharter.game.util.id.ID;

import uk.co.carelesslabs.Enums.EntityType;
import uk.co.carelesslabs.Media;

public class EnemyHelper extends EntityHandler {

	private HealthBarHelper healthBarHelper;
	private StatusEffectBarHelper statusEffectBarHelper;

	public EnemyHelper(IEntityHandler handler) {
		super(handler);
		healthBarHelper = new HealthBarHelper(handler);
		statusEffectBarHelper = new StatusEffectBarHelper(handler);
	}

	public ID addAtma(ZoneComp enemyZone, ZoneComp infoZone) {
		EntityBuilder b = EntityBuildUtil.buildBasicEntity(getEngine(),
				  EntityType.ENEMY,
				  new Vector3(U.u12(-65),0,0),
				  Media.atma);
		ID id = b.IDComp().id;
		b.EnemyComp();
		b.AssociatedTurnActionsComp();
		b.VitalsComp().vitals.maxHealth = 500; //10000;
		b.VitalsComp().vitals.weakHealth = 20; //1000;
		b.VitalsComp().vitals.health = 500; //10000;
		b.StatusEffectsComp().effects.maxEffects = 3;
		b.DescriptionComp().name = "Atma";
		b.SpriteComp();
		b.StatsComp().level = 3;
		b.StatsComp().power = 20;
		b.StatsComp().defense = 15;
		b.StatsComp().mPower = 10;
		b.StatsComp().mDefense = 10;
		b.SpriteComp().scale = new Vector2(2f,2f);
		b.CardOwnerComp().handSize = 1;
		b.AutoSelectTurnActionComp();
		Comp.util(enemyZone).add(b);
		getEngine().addEntity(b.Entity());
		b.free();

		healthBarHelper.addHealthBar(infoZone, id, false);
		statusEffectBarHelper.addStatusEffectBar(infoZone, id, false);
		return id;
	}

	public ID addCactar(ZoneComp enemyZone, ZoneComp infoZone) {
		EntityBuilder b = EntityBuildUtil.buildBasicEntity(getEngine(),
				  EntityType.ENEMY,
				  new Vector3(U.u12(-20), U.u12(-10), 0),
				  Media.cactar);
		ID id = b.IDComp().id;
		b.EnemyComp();
		b.AssociatedTurnActionsComp();
		b.VitalsComp().vitals.maxHealth = 500;
		b.VitalsComp().vitals.weakHealth = 20;
		b.VitalsComp().vitals.health = 500;
		b.StatusEffectsComp().effects.maxEffects = 3;
		b.DescriptionComp().name = "Cactar";
		b.SpriteComp();
		b.StatsComp().level = 2;
		b.StatsComp().power = 2;
		b.StatsComp().defense = 5;
		b.StatsComp().mPower = 5;
		b.StatsComp().mDefense = 7;
		b.CardOwnerComp().handSize = 1;
		b.AutoSelectTurnActionComp();
		//b.SpriteComp().scale = new Vector2(2f,2f);
		Comp.util(enemyZone).add(b);
		getEngine().addEntity(b.Entity());
		b.free();

		healthBarHelper.addHealthBar(infoZone, id, false);
		statusEffectBarHelper.addStatusEffectBar(infoZone, id, false);
		return id;
	}

}
