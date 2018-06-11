package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.jharter.game.ashley.components.Components.InputComp;
import com.jharter.game.ashley.components.Components.InteractComp;
import com.jharter.game.ashley.components.Mapper;
import com.jharter.game.ashley.entities.EntityUtil;
import com.jharter.game.util.id.ID;

public class InteractSystem extends IteratingSystem {

	@SuppressWarnings("unchecked")
	public InteractSystem() {
		super(Family.all(InputComp.class, 
 			  InteractComp.class).get()); 
	}
	
	@Override
	public void processEntity(Entity entity, float deltaTime) {
		InputComp in = Mapper.InputComp.get(entity);
		InteractComp interactComp = Mapper.InteractComp.get(entity);
		
		if(in.input.isInteract()) {
			System.out.println("interact!");
		}
		
		if(in.input.isInteract() && interactComp.interactables.size > 0) {
			ID id = interactComp.interactables.get(0);
			Entity targetEntity = EntityUtil.findEntity(id);
			if(targetEntity != null) {
				InteractComp targetInteractComp = Mapper.InteractComp.get(targetEntity);
				if(targetInteractComp != null && targetInteractComp.interaction != null) {
					targetInteractComp.interaction.interact(entity, targetEntity);
				}
			}
		}
		
		in.input.setInteract(false);
	}
	
}
