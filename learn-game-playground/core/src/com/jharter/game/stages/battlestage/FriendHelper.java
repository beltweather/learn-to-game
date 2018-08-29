package com.jharter.game.stages.battlestage;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.EntityBuilder;
import com.jharter.game.ashley.entities.EntityUtil;
import com.jharter.game.util.id.ID;

import uk.co.carelesslabs.Enums.EntityType;

public class FriendHelper {

	private FriendHelper() {}
	
	public static ID addFriend(PooledEngine engine, ID playerID, ZoneComp zone, Texture texture, String name) {
		EntityBuilder b = EntityUtil.buildBasicEntity(engine, 
				  EntityType.FRIEND, 
				  new Vector3(660,140,0), 
				  texture);
		b.BattleAvatarComp().playerID = playerID;
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
		ID id = b.IDComp().id;
		zone.add(b);
		engine.addEntity(b.Entity());
		b.free();
		
		return id;
	}
	
}