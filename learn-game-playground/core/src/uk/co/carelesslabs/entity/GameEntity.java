package uk.co.carelesslabs.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.jharter.game.util.id.ID;
import com.jharter.game.util.id.IDGenerator;

import uk.co.carelesslabs.Enums.EnityState;
import uk.co.carelesslabs.Enums.EntityType;
import uk.co.carelesslabs.box2d.Box2DWorld;
import uk.co.carelesslabs.map.Chunk;
import uk.co.carelesslabs.map.Tile;

public class GameEntity implements Comparable<GameEntity> {
    
	public int hashcode;
    public Vector3 pos;
    public Vector3 destVec;
    public Texture texture;
    public Texture shadow;
    public float width;
    public float height;
    public EntityType type;
    public EnityState state;
    public float speed;
    public Body body;
    public Body sensor;
    public boolean remove;
    public boolean requestRemove;
    public Inventory inventory;
    public Boolean ticks;
    public float time;
    public float coolDown;
    public Tile currentTile;
    public ID id;
    
    float dirX = 0;
    float dirY = 0;
    
    public GameEntity(){
        pos = new Vector3();
        id = IDGenerator.newID();
    }
    
    public void draw(SpriteBatch batch){
        if(shadow != null) batch.draw(shadow, pos.x, pos.y, width, height);
        if(texture != null) batch.draw(texture, pos.x, pos.y, width, height);
    }
    
    public void tick(float delta){
        time += delta;
    }
    
    public void tick(float delta, Chunk chunk){
    
    }
    
    public int compareTo(GameEntity e) {
        float tempY =  e.pos.y;
        float compareY = pos.y;
        
        return (tempY < compareY ) ? -1: (tempY > compareY) ? 1:0 ;
    }
    
    public void collision(GameEntity entity, boolean begin){}

    public void interact(GameEntity entity){}

    public void removeBodies(Box2DWorld box2D) {
        if(sensor != null) box2D.world.destroyBody(sensor);
        if(body != null) box2D.world.destroyBody(body);
    }
    
    public void getVector(Vector3 dest){
        float dx = dest.x - pos.x;
        float dy = dest.y - pos.y;
        double h = Math.sqrt(dx * dx + dy * dy);
        float dn = (float)(h / 1.4142135623730951);
              
        destVec = new Vector3(dx / dn, dy / dn, 0);
    }
    
    // Temporarily used to map static objects between client and server.
    // Ideally, the client game is generated from the server so that all
    // objects have unique ids that map back and forth.
    @Deprecated
    public String getPositionId() {
    	return pos.x + ":" + pos.y;
    }

}
