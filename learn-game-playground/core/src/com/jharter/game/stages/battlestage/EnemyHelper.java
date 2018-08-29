package com.jharter.game.stages.battlestage;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.EntityBuilder;
import com.jharter.game.ashley.entities.EntityUtil;
import com.jharter.game.util.Units;

import uk.co.carelesslabs.Enums.EntityType;
import uk.co.carelesslabs.Media;

public class EnemyHelper {

	public static void addAtma(PooledEngine engine, ZoneComp enemyZone) {
		EntityBuilder b = EntityUtil.buildBasicEntity(engine, 
				  EntityType.ENEMY, 
				  new Vector3(Units.u12(-65),0,0), 
				  Media.atma);
		b.VitalsComp().maxHealth = 500;
		b.VitalsComp().weakHealth = 50;
		b.VitalsComp().health = 500;
		b.DescriptionComp().name = "Atma";
		b.SpriteComp();
		b.StatsComp().level = 3;
		b.StatsComp().power = 20;
		b.StatsComp().defense = 15;
		b.StatsComp().mPower = 10;
		b.StatsComp().mDefense = 10;
		b.SpriteComp().scale = new Vector2(2f,2f);
		enemyZone.add(b);
		engine.addEntity(b.Entity());
		b.free();
	}
	
	public static void addCactar(PooledEngine engine, ZoneComp enemyZone) {
		EntityBuilder b = EntityUtil.buildBasicEntity(engine, 
				  EntityType.ENEMY, 
				  new Vector3(Units.u12(-20), Units.u12(-10), 0), 
				  Media.cactar);
		b.VitalsComp().maxHealth = 1000;
		b.VitalsComp().weakHealth = 100;
		b.VitalsComp().health = 1000;
		b.DescriptionComp().name = "Cactar";
		b.SpriteComp();
		b.StatsComp().level = 2;
		b.StatsComp().power = 2;
		b.StatsComp().defense = 5;
		b.StatsComp().mPower = 5;
		b.StatsComp().mDefense = 7;
		//b.SpriteComp().scale = new Vector2(2f,2f);
		enemyZone.add(b);
		engine.addEntity(b.Entity());
		b.free();
		
	}
	
}
