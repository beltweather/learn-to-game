package com.jharter.game.stages.battlestage;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.jharter.game.ashley.components.Comp;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.EntityBuilder;
import com.jharter.game.ashley.components.subcomponents.RelativePositionRules.RelativeToIDGetter;
import com.jharter.game.util.U;
import com.jharter.game.util.id.ID;
import com.jharter.game.util.id.IDUtil;

import uk.co.carelesslabs.Enums.Direction;
import uk.co.carelesslabs.Media;

public class ArrowHelper {

	private ArrowHelper() {}
	
	public static void addArrow(PooledEngine engine, ZoneComp infoZone) {
		EntityBuilder b = EntityBuilder.create(engine);
		b.IDComp().id = IDUtil.newID();
		b.SpriteComp().position.x = 200;
		b.SpriteComp().position.y = 200;
		b.SpriteComp().position.z = 1;
		b.SpriteComp().width = Media.arrowFrames[0].getRegionWidth() / U.PIXELS_PER_UNIT;
		b.SpriteComp().height = Media.arrowFrames[0].getRegionHeight() / U.PIXELS_PER_UNIT; 
		b.SpriteComp().relativePositionRules.enabled = true;
		b.SpriteComp().relativePositionRules.xAlign = Direction.CENTER;
		b.SpriteComp().relativePositionRules.yAlign = Direction.NORTH;
		b.SpriteComp().relativePositionRules.offset.y = U.u12(1);
		b.SpriteComp().relativePositionRules.setRelativeToIDGetter(new RelativeToIDGetter() {

			@Override
			public ID getRelativeToID() {
				if(Comp.Entity.DefaultCursor().isDisabled()) {
					return null;
				}
				return Comp.PlayerComp.get(Comp.Entity.get(Comp.Entity.DefaultTurn().ActivePlayerComp().activePlayerID)).battleAvatarID;
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
