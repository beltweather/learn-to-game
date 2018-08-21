package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.jharter.game.ashley.components.Components.CursorInputComp;
import com.jharter.game.ashley.components.Components.CursorInputRegulatorComp;
import com.jharter.game.ashley.components.Components.InputComp;
import com.jharter.game.ashley.components.Components.PositionComp;
import com.jharter.game.ashley.components.Components.SizeComp;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.Components.ZonePositionComp;
import com.jharter.game.ashley.components.Mapper;
import com.jharter.game.ashley.entities.EntityUtil;

public class CursorInputSystem extends IteratingSystem {

	@SuppressWarnings("unchecked")
	public CursorInputSystem() {
		super(Family.all(CursorInputComp.class,
						 CursorInputRegulatorComp.class,
						 InputComp.class).get());
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		InputComp in = Mapper.InputComp.get(entity);
		CursorInputRegulatorComp cir = Mapper.CursorInputRegulatorComp.get(entity);
		CursorInputComp ci = Mapper.CursorInputComp.get(entity);
		
		if (in.input.isDown()) {
			ci.direction.y++;
		}
		if (in.input.isUp()) {
			ci.direction.y--;
		}
		if (in.input.isLeft()) {
			ci.direction.x--;
		}
		if (in.input.isRight()) {
			ci.direction.x++;
		}
		
		if(cir.ignoreMovement(ci.move(), deltaTime)) {
			ci.reset();
		}
		
		if(in.input.isAccept()) {
			ci.accept = true;
			in.input.setAccept(false);
		}
		
		if(in.input.isCancel()) {
			ci.cancel = true;
			in.input.setCancel(false);
		}
	}
	
}