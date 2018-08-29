package com.jharter.game.stages.battlestage;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.jharter.game.ashley.components.EntityBuilder;
import com.jharter.game.ashley.components.Mapper;
import com.jharter.game.util.Units;
import com.jharter.game.util.id.IDGenerator;

import uk.co.carelesslabs.Enums.ZoneType;
import uk.co.carelesslabs.Media;

public class ArrowHelper {

	private ArrowHelper() {}
	
	public static void addArrow(PooledEngine engine) {
		EntityBuilder b = EntityBuilder.create(engine);
		b.IDComp().id = IDGenerator.newID();
		b.SpriteComp().position.x = 200;
		b.SpriteComp().position.y = 200;
		b.SpriteComp().position.z = 1;
		b.SpriteComp().width = Media.arrowFrames[0].getRegionWidth() / Units.PIXELS_PER_UNIT;
		b.SpriteComp().height = Media.arrowFrames[0].getRegionHeight() / Units.PIXELS_PER_UNIT; 
		b.AnimationComp().looping = true;
		b.AnimationComp().animation = Media.arrowAnim;
		b.AnimationComp().animation.setPlayMode(PlayMode.LOOP);
		b.TextureComp();
		b.ZonePositionComp().index = 0;
		b.ZonePositionComp().zoneID = Mapper.ZoneComp.getID(null, ZoneType.ARROW);
		b.ZonePositionComp().getZoneComp().add(b);
		engine.addEntity(b.Entity());
		b.free();
	}
	
}
