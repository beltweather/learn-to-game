package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.jharter.game.ashley.components.Components.InputComp;
import com.jharter.game.ashley.components.Components.InteractComp;
import com.jharter.game.ashley.systems.boilerplate.GameIteratingSystem;
import com.jharter.game.util.Sys;
import com.jharter.game.util.id.ID;

public class InteractSystem extends GameIteratingSystem {

	@SuppressWarnings("unchecked")
	public InteractSystem() {
		super(Family.all(InputComp.class, 
 			  InteractComp.class).get()); 
	}
	
	@Override
	public void processEntity(Entity entity, float deltaTime) {
		InputComp in = Comp.InputComp.get(entity);
		InteractComp interactComp = Comp.InteractComp.get(entity);
		
		if(in.input.isAccept()) {
			Sys.out.println("interact!");
		}
		
		if(in.input.isAccept() && interactComp.interactables.size > 0) {
			ID id = interactComp.interactables.get(0);
			Entity targetEntity = Comp.Entity.get(id);
			if(targetEntity != null) {
				InteractComp targetInteractComp = Comp.InteractComp.get(targetEntity);
				if(targetInteractComp != null && targetInteractComp.interaction != null) {
					targetInteractComp.interaction.interact(entity, targetEntity);
				}
			}
		}
		
		in.input.setAccept(false);
	}
	
}
