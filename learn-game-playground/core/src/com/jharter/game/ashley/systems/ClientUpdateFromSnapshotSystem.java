package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.badlogic.gdx.utils.TimeUtils;
import com.jharter.game.ashley.components.Components.InputComp;
import com.jharter.game.ashley.components.Components.TargetPositionComp;
import com.jharter.game.ashley.components.Mapper;
import com.jharter.game.ashley.entities.EntityUtil;
import com.jharter.game.control.Input;
import com.jharter.game.server.GameClient;
import com.jharter.game.server.GameNetwork.EntityData;
import com.jharter.game.server.GameNetwork.SnapshotPacket;

public class ClientUpdateFromSnapshotSystem extends EntitySystem {

	private GameClient client;
	
	public ClientUpdateFromSnapshotSystem(GameClient client) {
		this.client = client;
	}
	
	private long getRenderTime(long currentTime) {
    	return currentTime - (client.getPing() + 50);
    }
	
	@Override
	public void update (float deltaTime) {
		DelayedRemovalArray<SnapshotPacket> packets = client.getSnapshotPackets();
        if(packets.size <= 1) {
        	return;
        }
    	packets.begin();
    	long renderTime = getRenderTime(TimeUtils.millis());
    	int newestIdx = -1;
    	for(int i = 0; i < packets.size; i++) {
    		if(i > 0 && packets.get(i).time >= renderTime) {
    			newestIdx = i;
    			for(int j = 0; j < newestIdx - 1; j++) {
    				packets.removeIndex(j); // Delayed removal won't happen until "end" is called
    			}
    			break;
    		}
    	}
    	if(newestIdx > -1) {
    		SnapshotPacket snapshot1 = packets.get(newestIdx - 1);
    		SnapshotPacket snapshot2 = packets.get(newestIdx);
    		if(snapshot2.time - snapshot1.time > 0) {
    			for(int i = 0; i < snapshot1.entityDatas.size(); i++) {
    				EntityData entityData1 = snapshot1.entityDatas.get(i);
    				EntityData entityData2 = snapshot2.entityDatas.get(i);
    				String entityId = entityData1.id;
    				
    				Entity entity = EntityUtil.findEntity(entityId);
    				if(entity != null) {
    					InputComp inputComp = Mapper.InputComp.get(entity);
    					boolean focus = Mapper.FocusComp.has(entity);
    					if(inputComp != null) {
    						Input in = inputComp.input;
    						if(entityData1.input != null && !focus) {
    							in.setInputState(entityData1.input);
    						}
    						if(entityData2.input != null && !focus) {
    							in.setInputState(entityData2.input);
    						}
    					}
    					
    					TargetPositionComp t = Mapper.TargetPositionComp.get(entity);
    					if(t.position == null) {
    						t.position = new Vector3();
    					}
    					
    					t.position.x = getInterpolatedValue(snapshot1.time, entityData1.x,
								   							snapshot2.time, entityData2.x,
								   							renderTime);
    					t.position.y = getInterpolatedValue(snapshot1.time, entityData1.y,
    														snapshot2.time, entityData2.y,
    														renderTime);
    				}
    			}
    		}
    	}
    	packets.end();
	}
	
	private float getInterpolatedValue(long timeSnapshot1, float valueSnapshot1, long timeSnapshot2, float valueSnapshot2, long timeCurrent) {
    	return (timeCurrent - timeSnapshot1) / (timeSnapshot2 - timeSnapshot1) * (valueSnapshot2 - valueSnapshot1) + valueSnapshot1;
    }
	
	/*public void update(Entity entity, Input input) {
		PositionComp p = Mapper.PositionComp.get(entity);
		BodyComp b = Mapper.BodyComp.get(entity);
		VelocityComp v = Mapper.VelocityComp.get(entity);
		SizeComp s = Mapper.SizeComp.get(entity);
		InteractComp i = Mapper.InteractComp.get(entity);
		if(p == null || b == null || v == null || s == null) {
			return;
		}
		
		p.direction.x = 0;
		p.direction.y = 0;
		
        if (input.isDown())  p.direction.y = -1;
        if (input.isUp())    p.direction.y = 1;
        if (input.isLeft())  p.direction.x = -1;
        if (input.isRight()) p.direction.x = 1;    
        
        b.body.setLinearVelocity(p.direction.x * v.speed, p.direction.y * v.speed);
        p.position.x = b.body.getPosition().x - s.width/2;
        p.position.y = b.body.getPosition().y - s.height/4;
        
        // If interact key pressed and interactEntities present interact with first in list.
        if(input.isInteract() && i != null && i.interactables.size > 0){
        	i.interaction.interact(entity, EntityUtil.findEntity(i.interactables.get(0)));
        }
        
        // Reset interact
        input.setInteract(false);
    }*/
	
}
