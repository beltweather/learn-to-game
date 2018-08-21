package uk.co.carelesslabs;

public class Enums {
    
    public enum TileType {
        GRASS,
        WATER,
        CLIFF
    }
    
    public enum EntityType {
        HERO, 
        TREE, 
        BIRD,
        TILE,
        CARD,
        CHARACTER,
        BACKGROUND,
        ENEMY,
        HAND
    }
    
    public enum EnityState {
        NONE,
        IDLE,
        FEEDING,
        WALKING,
        FLYING,
        HOVERING, 
        LANDING
    }
    
    public enum MenuState {
        ACTIVE,
        DISABLED,
        HOVEROVER,
        CLICKED
    }
    
    public enum ZoneType {
    	HAND,
    	ENEMY,
    	FRIEND,
    	DISCARD,
    	TIME,
    	NONE
    }

}
