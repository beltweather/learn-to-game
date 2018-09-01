package com.jharter.game.stages.battlestage;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.EntityBuilder;
import com.jharter.game.ashley.components.Mapper;
import com.jharter.game.ashley.components.subcomponents.RelativePositionRules.RelativeToIDGetter;
import com.jharter.game.util.Units;
import com.jharter.game.util.id.ID;
import com.jharter.game.util.id.IDGenerator;

import uk.co.carelesslabs.Enums.Direction;
import uk.co.carelesslabs.Media;

public class ArrowHelper {

	private ArrowHelper() {}
	
	public static void addArrow(PooledEngine engine, ZoneComp infoZone) {
		EntityBuilder b = EntityBuilder.create(engine);
		b.IDComp().id = IDGenerator.newID();
		b.SpriteComp().position.x = 200;
		b.SpriteComp().position.y = 200;
		b.SpriteComp().position.z = 1;
		b.SpriteComp().width = Media.arrowFrames[0].getRegionWidth() / Units.PIXELS_PER_UNIT;
		b.SpriteComp().height = Media.arrowFrames[0].getRegionHeight() / Units.PIXELS_PER_UNIT; 
		b.SpriteComp().relativePositionRules.relative = true;
		b.SpriteComp().relativePositionRules.relativeXAlign = Direction.CENTER;
		b.SpriteComp().relativePositionRules.relativeYAlign = Direction.NORTH;
		b.SpriteComp().relativePositionRules.relativeOffset.y = Units.u12(1);
		b.SpriteComp().relativePositionRules.setRelativeToIDGetter(new RelativeToIDGetter() {

			@Override
			public ID getRelativeToID() {
				if(Mapper.CursorEntity.isDisabled()) {
					return null;
				}
				return Mapper.PlayerComp.get(Mapper.Entity.get(Mapper.getPlayerEntityID())).battleAvatarID;
			}
			
		});
		b.AnimationComp().looping = true;
		b.AnimationComp().animation = Media.arrowAnim;
		b.AnimationComp().animation.setPlayMode(PlayMode.LOOP);
		b.TextureComp();
		infoZone.add(b);
		engine.addEntity(b.Entity());
		b.free();
	}
	
}
