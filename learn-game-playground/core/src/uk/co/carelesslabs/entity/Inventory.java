package uk.co.carelesslabs.entity;

import com.badlogic.gdx.utils.IntMap;

import uk.co.carelesslabs.Enums.EntityType;

public class Inventory {
    IntMap<GameEntity> entities;
    
    public Inventory(){
        reset();
    }
    
    public int getInventorySize(){
        return entities.size;
    }

    public void addEntity(GameEntity entity) {
        entities.put(getInventorySize(), entity);
    }
    
    public IntMap<GameEntity> getInventory(){
        return entities;
    }

    public void print() {
        System.out.println("*** Inventory ***");
        for(int i = 0 ; i < entities.size; i++){
            GameEntity e = entities.get(i);
            System.out.println("* ["+i+"] " + e.type.toString());
        }    
        System.out.println("*****************");
    }
    
    public void reset() {
        entities.clear();
    }
    
    public boolean hasWood(){
        for(int i = 0 ; i < entities.size; i++){
            GameEntity e = entities.get(i);
            if( e.type == EntityType.TREE){
                return true;
            }
        }      
        return false;
    }
}
