package com.jharter.game.stages.battlestage;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.jharter.game.ashley.components.Comp;
import com.jharter.game.ashley.components.Components.ActivePlayerComp;
import com.jharter.game.ashley.components.Components.CursorComp;
import com.jharter.game.ashley.components.Components.DisabledComp;
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
			
			private ImmutableArray<Entity> cursors = engine.getEntitiesFor(Family.all(CursorComp.class).exclude(DisabledComp.class).get());
			private ImmutableArray<Entity> activePlayers = engine.getEntitiesFor(Family.all(ActivePlayerComp.class).get());
			
			@Override
			public ID getRelativeToID() {
				if(cursors.size() == 0 || activePlayers.size() == 0) {
					return null;
				}
				return Comp.ActivePlayerComp.get(activePlayers.first()).activePlayerID;
			}
			
		});
		b.AnimationComp().looping = true;
		b.AnimationComp().animation = Media.arrowAnim;
		b.AnimationComp().animation.setPlayMode(PlayMode.LOOP);
		b.TextureComp();
		Comp.ZoneComp(infoZone).add(b);
		engine.addEntity(b.Entity());
		b.free();
	}
	
}
