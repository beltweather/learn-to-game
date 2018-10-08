package com.jharter.game.ecs.systems.network.client;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.jharter.game.ecs.components.Components.FocusTag;
import com.jharter.game.ecs.components.Components.IDComp;
import com.jharter.game.ecs.components.Components.InputComp;
import com.jharter.game.ecs.systems.boilerplate.GameIteratingSystem;
import com.jharter.game.network.endpoints.GameClient;

public class ClientSendInputSystem extends GameIteratingSystem {

	private GameClient client;
	
	@SuppressWarnings("unchecked")
	public ClientSendInputSystem(GameClient client) {
		super(Family.all(FocusTag.class, InputComp.class, IDComp.class).get());
		this.client = client;
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		client.tick(deltaTime);
		
		IDComp id = Comp.IDComp.get(entity);
		InputComp in = Comp.InputComp.get(entity);
		
		// We only need to tick the input manager on the client since the server
		// will get input updates via messages.
		in.input.tick(deltaTime);
		
		// Send the input state to the server if there was any activity this tick.
		in.input.maybeSendInputState(client, id.id);
	}

}
