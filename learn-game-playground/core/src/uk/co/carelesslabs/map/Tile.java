package uk.co.carelesslabs.map;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.jharter.game.ashley.components.Components.BodyComp;
import com.jharter.game.ashley.components.Components.TileComp;
import com.jharter.game.ashley.components.EntityBuilder;
import com.jharter.game.ashley.entities.EntityUtil;
import com.jharter.game.util.id.IDGenerator;

import uk.co.carelesslabs.Enums.EntityType;
import uk.co.carelesslabs.Enums.TileType;
import uk.co.carelesslabs.box2d.Box2DHelper;
import uk.co.carelesslabs.box2d.Box2DWorld;
import uk.co.carelesslabs.entity.GameEntity;

public class Tile extends GameEntity {
    public int size;
    public int row;
    public int col;
    public String code;
    public Texture secondaryTexture;
    public Texture texture;
    public TileType type;
    
    public Tile(float x, float y, int size, TileType type, Texture texture){
        super();
        pos.x = x*size;
        pos.y = y*size;
        this.size = size;
        this.texture = texture;
        this.col = (int) x;
        this.row = (int) y;
        this.type = type;
        this.code = "";
    }

    public String details(){
        return "x: " + pos.x + " y: " + pos.y + " row: " + row + " col: " + col + " code: " + code + " type: " + type.toString();
    }

    public boolean isGrass() {
        return type == TileType.GRASS;
    }
    
    public boolean isWater() {
        return type == TileType.WATER;
    }
    
    public boolean isCliff() {
        return type == TileType.CLIFF;
    }
    
    public boolean isPassable() {
        return !isWater() && !isCliff();
    }
    
    public boolean isNotPassable() {
        return !isPassable();
    }
    
    public boolean isAllWater() {
        return code.equals("000000000");
    }
    
    public boolean notIsAllWater() {
        return !isAllWater();
    }
    
    public Entity toEntity(PooledEngine engine, Box2DWorld box2D) {
    	EntityBuilder b = EntityUtil.buildBasicEntity(engine, IDGenerator.newID(), EntityType.TILE, pos, size, size, new TextureRegion(texture));
    	Entity entity = b.Entity();
    	b.free();
    	
    	TileComp t = engine.createComponent(TileComp.class);
    	t.code = code;
    	t.row = row;
    	t.col = col;
    	t.size = size;
    	t.secondaryTexture = secondaryTexture == null ? null : new TextureRegion(secondaryTexture);
    	t.type = type;
    	entity.add(t);
    	
    	if(isNotPassable() && notIsAllWater()){
            BodyComp bodyComp = engine.createComponent(BodyComp.class);
            bodyComp.body = Box2DHelper.createBody(box2D.world, size, size, 0, 0, pos, BodyType.StaticBody);
            entity.add(bodyComp);
    	}
    	
    	return entity;
    }
}
