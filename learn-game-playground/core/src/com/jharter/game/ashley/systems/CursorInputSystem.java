package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.jharter.game.ashley.components.Components.CursorComp;
import com.jharter.game.ashley.components.Components.CursorInputComp;
import com.jharter.game.ashley.components.Components.CursorInputRegulatorComp;
import com.jharter.game.ashley.components.Components.DisabledComp;
import com.jharter.game.ashley.components.Components.InputComp;
import com.jharter.game.ashley.components.Components.InvisibleComp;
import com.jharter.game.ashley.components.Ent;
import com.jharter.game.ashley.components.Comp;

public class CursorInputSystem extends IteratingSystem {

	@SuppressWarnings("unchecked")
	public CursorInputSystem() {
		super(Family.all(CursorComp.class,
						 CursorInputComp.class,
						 CursorInputRegulatorComp.class,
						 InputComp.class).exclude(InvisibleComp.class, DisabledComp.class).get());
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		InputComp in = Comp.InputComp.get(entity);
		if(Ent.TurnEntity.TurnTimerComp().turnTimer.isStopped() || Comp.AnimatingComp.has(entity)) {
			in.input.reset();
			return;
		}
		
		CursorInputRegulatorComp cir = Comp.CursorInputRegulatorComp.get(entity);
		CursorInputComp ci = Comp.CursorInputComp.get(entity);
		
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