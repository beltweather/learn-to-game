package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.jharter.game.ashley.components.Components.AnimationComp;
import com.jharter.game.ashley.components.Components.InvisibleComp;
import com.jharter.game.ashley.components.Components.TextureComp;
import com.jharter.game.ashley.systems.boilerplate.CustomIteratingSystem;

public class AnimationSystem extends CustomIteratingSystem {
	
	@SuppressWarnings("unchecked")
	public AnimationSystem () {
		super(Family.all(TextureComp.class, AnimationComp.class).exclude(InvisibleComp.class).get());
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		AnimationComp a = Comp.AnimationComp.get(entity);
		TextureComp v = Comp.TextureComp.get(entity);
		if(!a.looping && a.animation.isAnimationFinished(a.time)) {
			v.region = v.defaultRegion;
			entity.remove(AnimationComp.class);
		} else {
			v.region = (TextureRegion) a.animation.getKeyFrame(a.time, a.looping);
			a.time += deltaTime;
		}
	}
	
}