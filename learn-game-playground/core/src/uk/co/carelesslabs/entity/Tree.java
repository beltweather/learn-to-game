package uk.co.carelesslabs.entity;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;

import uk.co.carelesslabs.Enums.EntityType;
import uk.co.carelesslabs.Media;
import uk.co.carelesslabs.Rumble;
import uk.co.carelesslabs.box2d.Box2DHelper;
import uk.co.carelesslabs.box2d.Box2DWorld;

public class Tree extends GameEntity{
    
    public Tree(Vector3 pos, Box2DWorld box2d){
        super();
        type = EntityType.TREE;
        width = 8;
        height = 8;
        this.pos = pos;
        texture = Media.tree;
        body = Box2DHelper.createBody(box2d.world, width/2, height/2, width/4, 0, pos, BodyDef.BodyType.StaticBody);
        sensor = Box2DHelper.createSensor(box2d.world, width, height*.85f, width/2, height/3, pos, BodyDef.BodyType.DynamicBody);     
        hashcode = sensor.getFixtureList().get(0).hashCode();
    }
    
    @Override
    public void interact(GameEntity entity){
        if(entity.inventory != null){
            entity.inventory.addEntity(this);
            //remove = true;
            requestRemove = true;
            Rumble.rumble(1, .2f);
        }
    }
    

}