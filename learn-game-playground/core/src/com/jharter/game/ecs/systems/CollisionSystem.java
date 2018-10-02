package com.jharter.game.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.jharter.game.ecs.components.Components.CollisionComp;
import com.jharter.game.ecs.components.Components.InteractComp;
import com.jharter.game.ecs.components.Components.TypeComp;
import com.jharter.game.ecs.systems.boilerplate.GameIteratingSystem;

public class CollisionSystem extends GameIteratingSystem {

	@SuppressWarnings("unchecked")
	public CollisionSystem() {
		super(Family.all(CollisionComp.class, TypeComp.class).get()); 
	}
	
	@Override
	public void processEntity(Entity entity, float deltaTime) {
		CollisionComp c = Comp.CollisionComp.get(entity);
		TypeComp t = Comp.TypeComp.get(entity);
		if(c.collisionWithId != null) {
			// For now, just process collisions for heroes. We'll want to generalize this though!
			switch(t.type) {
				case HERO:
					InteractComp interactComp = Comp.InteractComp.get(entity);
					if(interactComp != null) {
						interactComp.interactables.add(c.collisionWithId);
					}
					break;
				default:
					break;
			}
			
		}
	}
	
}
