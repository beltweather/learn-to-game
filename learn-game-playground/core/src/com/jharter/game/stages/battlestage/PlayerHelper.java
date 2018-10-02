package com.jharter.game.stages.battlestage;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.EntityBuilder;
import com.jharter.game.ashley.entities.EntityFactory;
import com.jharter.game.ashley.entities.EntityUtil;
import com.jharter.game.ashley.entities.IEntityFactory;
import com.jharter.game.util.id.ID;

import uk.co.carelesslabs.Enums.EntityType;

public class PlayerHelper extends EntityFactory {
	
	private HealthBarHelper healthBarHelper;
	
	public PlayerHelper(IEntityFactory factory) {
		super(factory);
		healthBarHelper = new HealthBarHelper(factory);
	}

	public void addPlayer(ZoneComp zone, ZoneComp infoZone, ID playerID, Texture texture, String name) {
		EntityBuilder b = EntityUtil.buildBasicEntity(getEngine(),
													  playerID,
				  									  EntityType.FRIEND, 
				  									  new Vector3(660,140,0), 
				  									  new TextureRegion(texture));
		b.PlayerComp();
		b.VitalsComp().maxHealth = 100;
		b.VitalsComp().weakHealth = 25;
		b.VitalsComp().health = 10;
		b.StatsComp().level = 1;
		b.StatsComp().power = 10;
		b.StatsComp().defense = 10;
		b.StatsComp().mPower = 2;
		b.StatsComp().mDefense = 2;
		b.DescriptionComp().name = name;
		b.SpriteComp();
		Comp.ZoneComp(zone).add(b);
		getEngine().addEntity(b.Entity());
		b.free();
		healthBarHelper.addHealthBar(infoZone, playerID);
	}
	
}
