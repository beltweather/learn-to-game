package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.jharter.game.ashley.components.Components.CollisionComp;
import com.jharter.game.ashley.components.Components.InteractComp;
import com.jharter.game.ashley.components.Components.TypeComp;
import com.jharter.game.ashley.components.Mapper;

public class CollisionSystem extends IteratingSystem {

	@SuppressWarnings("unchecked")
	public CollisionSystem() {
		super(Family.all(CollisionComp.class, TypeComp.class).get()); 
	}
	
	@Override
	public void processEntity(Entity entity, float deltaTime) {
		CollisionComp c = Mapper.CollisionComp.get(entity);
		TypeComp t = Mapper.TypeComp.get(entity);
		if(c.id != null) {
			// For now, just process collisions for heroes. We'll want to generalize this though!
			switch(t.type) {
				case HERO:
					InteractComp interactComp = Mapper.InteractComp.get(entity);
					if(interactComp != null) {
						interactComp.interactables.add(c.id);
					}
					break;
				default:
					break;
			}
			
		}
	}
	
}
