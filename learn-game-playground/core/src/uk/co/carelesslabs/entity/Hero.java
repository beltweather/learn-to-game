package uk.co.carelesslabs.entity;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;
import com.jharter.game.control.GameInput;
import com.jharter.game.network.endpoints.GameNetwork.EntityData;
import com.jharter.game.util.id.ID;

import uk.co.carelesslabs.Enums.EntityType;
import uk.co.carelesslabs.Media;
import uk.co.carelesslabs.box2d.Box2DHelper;
import uk.co.carelesslabs.box2d.Box2DWorld;

public class Hero extends GameEntity{
	
	private static final float ALPHA = 0.01f;
	
    Array<GameEntity> interactEntities;
    
    public Vector3 targetPos;
    
    public Hero(ID id, Vector3 pos, Box2DWorld box2d){
        this.id = id;
        targetPos = null;
    	type = EntityType.HERO;
        width = 8;
        height = 8;
        texture = Media.hero;
        speed = 90;
        inventory = new Inventory();
        reset(box2d, pos);
    }

    public boolean hasTarget() {
    	return targetPos != null;
    }
    
    public boolean isOnTarget() {
    	return Math.abs(pos.x - targetPos.x) < 1f && Math.abs(pos.y - targetPos.y) < 1f;
    }
    
    public void clearTarget() {
    	targetPos = null;
    }
    
    public void setTarget(float x, float y) {
    	if(targetPos == null) {
    		targetPos = new Vector3();
    	}
    	targetPos.x = x;
    	targetPos.y = y;
    }
    
    public void moveTowardTarget() {
    	if(!hasTarget()) {
    		return;
    	}
    	float alpha = 0.01f;
    	float newX = Interpolation.linear.apply(pos.x, targetPos.x, alpha);
    	float newY = Interpolation.linear.apply(pos.y, targetPos.y, alpha);
    	setPosition(newX, newY);
    	if(isOnTarget()) {
    		clearTarget();
    	}
    }
    
    public void reset(Box2DWorld box2d, Vector3 pos) {
        this.pos.set(pos);
        body = Box2DHelper.createBody(box2d.world, width/2, height/2, width/4, 0, pos, BodyType.DynamicBody);  
        hashcode = body.getFixtureList().get(0).hashCode();
        interactEntities = new Array<GameEntity>();
        inventory.reset();
    }
    
    public void update(GameInput input) {
    	dirX = 0;
        dirY = 0;
        
        if (input.isDown())  dirY = -1;
        if (input.isUp())    dirY = 1;
        if (input.isLeft())  dirX = -1;
        if (input.isRight()) dirX = 1;    
        
        body.setLinearVelocity(dirX * speed, dirY * speed);
        pos.x = body.getPosition().x - width/2;
        pos.y = body.getPosition().y - height/4;
        
        // If interact key pressed and interactEntities present interact with first in list.
        if(input.isInteract() && interactEntities.size > 0){
        	interactEntities.get(0).interact(this);
        }
        
        // Reset interact
        input.setInteract(false);
    }
    
    private float getInterpolatedValue(long timeSnapshot1, float valueSnapshot1, long timeSnapshot2, float valueSnapshot2, long timeCurrent) {
    	return (timeCurrent - timeSnapshot1) / (timeSnapshot2 - timeSnapshot1) * (valueSnapshot2 - valueSnapshot1) + valueSnapshot1;
    }
    
    public void update(long snapshotTime1, EntityData entitySnapshot1, long snapshotTime2, EntityData entitySnapshot2, long renderTime) {
    	float x = getInterpolatedValue(snapshotTime1, entitySnapshot1.x,
    								   snapshotTime2, entitySnapshot2.x,
    								   renderTime);
    	float y = getInterpolatedValue(snapshotTime1, entitySnapshot1.y,
				   					   snapshotTime2, entitySnapshot2.y,
				   					   renderTime);
    	setTarget(x, y);
    	if(isOnTarget()) {
    		clearTarget();
    	}
    }
    
    public void setPosition(float x, float y) {
    	body.setTransform(x+width/2, y+height/4, body.getAngle());
    	pos.x = body.getPosition().x - width/2;
        pos.y = body.getPosition().y - height/4;
    }
    
    @Override
    public void collision(GameEntity entity, boolean begin){
    	if(begin){
    		// Hero entered hitbox
    		interactEntities.add(entity);   		
    	} else {
    		// Hero Left hitbox
    		interactEntities.removeValue(entity, false);
    	}    	
    }
 
}