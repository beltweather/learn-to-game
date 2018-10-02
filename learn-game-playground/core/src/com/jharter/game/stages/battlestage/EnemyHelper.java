package com.jharter.game.stages.battlestage;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.EntityBuilder;
import com.jharter.game.ashley.entities.EntityHandler;
import com.jharter.game.ashley.entities.EntityUtil;
import com.jharter.game.ashley.entities.IEntityHandler;
import com.jharter.game.util.U;
import com.jharter.game.util.id.ID;

import uk.co.carelesslabs.Enums.EntityType;
import uk.co.carelesslabs.Media;

public class EnemyHelper extends EntityHandler {
	
	private HealthBarHelper healthBarHelper; 

	public EnemyHelper(IEntityHandler handler) {
		super(handler);
		healthBarHelper = new HealthBarHelper(handler);
	}

	public void addAtma(ZoneComp enemyZone, ZoneComp infoZone) {
		EntityBuilder b = EntityUtil.buildBasicEntity(getEngine(), 
				  EntityType.ENEMY, 
				  new Vector3(U.u12(-65),0,0), 
				  Media.atma);
		ID id = b.IDComp().id;
		b.VitalsComp().maxHealth = 10000;
		b.VitalsComp().weakHealth = 1000;
		b.VitalsComp().health = 10000;
		b.DescriptionComp().name = "Atma";
		b.SpriteComp();
		b.StatsComp().level = 3;
		b.StatsComp().power = 20;
		b.StatsComp().defense = 15;
		b.StatsComp().mPower = 10;
		b.StatsComp().mDefense = 10;
		b.SpriteComp().scale = new Vector2(2f,2f);
		Comp.ZoneComp(enemyZone).add(b);
		getEngine().addEntity(b.Entity());
		b.free();
		
		healthBarHelper.addHealthBar(infoZone, id);
	}
	
	public void addCactar(ZoneComp enemyZone, ZoneComp infoZone) {
		EntityBuilder b = EntityUtil.buildBasicEntity(getEngine(), 
				  EntityType.ENEMY, 
				  new Vector3(U.u12(-20), U.u12(-10), 0), 
				  Media.cactar);
		ID id = b.IDComp().id;
		b.VitalsComp().maxHealth = 5000;
		b.VitalsComp().weakHealth = 500;
		b.VitalsComp().health = 5000;
		b.DescriptionComp().name = "Cactar";
		b.SpriteComp();
		b.StatsComp().level = 2;
		b.StatsComp().power = 2;
		b.StatsComp().defense = 5;
		b.StatsComp().mPower = 5;
		b.StatsComp().mDefense = 7;
		//b.SpriteComp().scale = new Vector2(2f,2f);
		Comp.ZoneComp(enemyZone).add(b);
		getEngine().addEntity(b.Entity());
		b.free();
		
		healthBarHelper.addHealthBar(infoZone, id);
	}
	
}
