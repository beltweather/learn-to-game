package com.jharter.game.ecs.helpers;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.jharter.game.ecs.components.Components.ActivePlayerComp;
import com.jharter.game.ecs.components.Components.CursorComp;
import com.jharter.game.ecs.components.Components.DisabledComp;
import com.jharter.game.ecs.components.Components.ZoneComp;
import com.jharter.game.ecs.components.subcomponents.RelativePositionRules.RelativeToIDGetter;
import com.jharter.game.ecs.entities.EntityBuilder;
import com.jharter.game.ecs.entities.EntityHandler;
import com.jharter.game.ecs.entities.IEntityHandler;
import com.jharter.game.util.U;
import com.jharter.game.util.id.ID;
import com.jharter.game.util.id.IDUtil;

import uk.co.carelesslabs.Enums.Direction;
import uk.co.carelesslabs.Media;

public class ArrowHelper extends EntityHandler {

	public ArrowHelper(IEntityHandler handler) {
		super(handler);
	}

	public void addArrow(ZoneComp infoZone) {
		EntityBuilder b = EntityBuilder.create(getEngine());
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
			
			private ImmutableArray<Entity> cursors = getEngine().getEntitiesFor(Family.all(CursorComp.class).exclude(DisabledComp.class).get());
			private ImmutableArray<Entity> activePlayers = getEngine().getEntitiesFor(Family.all(ActivePlayerComp.class).get());
			
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
		getEngine().addEntity(b.Entity());
		b.free();
	}
	
}
