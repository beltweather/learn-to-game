package com.jharter.game.ashley.systems.network.client;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.MathUtils;
import com.jharter.game.ashley.components.Components.FocusComp;
import com.jharter.game.ashley.components.Components.InputComp;
import com.jharter.game.ashley.systems.boilerplate.GameIteratingSystem;

public class ClientRandomMovementSystem extends GameIteratingSystem {

	private float time = 0;
	private int dir = 4;
	
	public ClientRandomMovementSystem() {
		super(Family.all(FocusComp.class, InputComp.class).get());
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		time += deltaTime;
		if(time > 0.1) {
			time = 0;
			dir = MathUtils.random(5);
		}
		
		InputComp i = Comp.InputComp.get(entity);
		i.input.reset();
		switch(dir) {
			case 0:
				i.input.setUp(true);
				break;
			case 1:
				i.input.setDown(true);
				break;
			case 2:
				i.input.setLeft(true);
				break;
			case 3:
				i.input.setRight(true);
				break;
			default:
				break;
		}
	}
	
}
