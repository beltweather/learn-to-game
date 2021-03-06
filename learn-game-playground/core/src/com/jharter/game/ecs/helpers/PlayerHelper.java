package com.jharter.game.ecs.helpers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.jharter.game.ecs.components.Components.ZoneComp;
import com.jharter.game.ecs.entities.EntityBuildUtil;
import com.jharter.game.ecs.entities.EntityBuilder;
import com.jharter.game.ecs.entities.EntityHandler;
import com.jharter.game.ecs.entities.IEntityHandler;
import com.jharter.game.util.id.ID;

import uk.co.carelesslabs.Enums.EntityType;

public class PlayerHelper extends EntityHandler {

	private HealthBarHelper healthBarHelper;
	private StatusEffectBarHelper statusEffectBarHelper;

	public PlayerHelper(IEntityHandler handler) {
		super(handler);
		healthBarHelper = new HealthBarHelper(handler);
		statusEffectBarHelper = new StatusEffectBarHelper(handler);
	}

	public void addPlayer(ZoneComp zone, ZoneComp infoZone, ID playerID, Texture texture, String name) {
		EntityBuilder b = EntityBuildUtil.buildBasicEntity(getEngine(),
													  playerID,
				  									  EntityType.FRIEND,
				  									  new Vector3(660,140,0),
				  									  new TextureRegion(texture));
		b.PlayerComp();
		b.FriendComp();
		b.AssociatedTurnActionsComp();
		b.VitalsComp().vitals.maxHealth.d(100);
		b.VitalsComp().vitals.health.d(10);
		b.StatusEffectsComp().effects.maxEffects = 3;
		b.StatsComp().level = 1;
		b.StatsComp().power = 10;
		b.StatsComp().defense = 10;
		b.StatsComp().mPower = 2;
		b.StatsComp().mDefense = 2;
		b.DescriptionComp().name = name;
		b.SpriteComp();
		b.CardOwnerComp();
		Comp.util(zone).add(b);
		getEngine().addEntity(b.Entity());
		b.free();
		healthBarHelper.addHealthBar(infoZone, playerID, true);
		statusEffectBarHelper.addStatusEffectBar(infoZone, playerID, true);
	}

}
