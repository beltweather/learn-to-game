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
        FRIEND,
        CURSOR,
        BACKGROUND,
        ENEMY,
        PLAYER
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
    	ACTIVE_CARD,
    	DECK,
    	DISCARD,
    	INFO,
    	NONE
    }
    
    public enum CardType {
    	TARGET_ENEMY,
    	TARGET_FRIEND,
    	TARGET_CARD,
    	TARGET_FRIEND_THEN_ENEMY,
    	NONE
    }
    
    /*public enum TurnPhase {
    	START_BATTLE,
    	START_TURN,
    	SELECT_ENEMY_ACTIONS,
    	SELECT_FRIEND_ACTIONS,
    	PERFORM_FRIEND_ACTIONS,
    	PERFORM_ENEMY_ACTIONS,
    	END_TURN,
    	END_BATTLE,
    	NONE
    }*/
    
    public enum Direction {
    	NORTH,
    	SOUTH,
    	EAST,
    	WEST,
    	NORTH_EAST,
    	NORTH_WEST,
    	SOUTH_EAST,
    	SOUTH_WEST,
    	CENTER,
    	NONE,
    }

}
