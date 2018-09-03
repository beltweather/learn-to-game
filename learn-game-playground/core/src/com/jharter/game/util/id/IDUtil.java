package com.jharter.game.util.id;

import java.util.UUID;

import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.jharter.game.ashley.components.Ent;

import uk.co.carelesslabs.Enums.ZoneType;

public class IDUtil {

	private IDUtil() {}

	private static ID globalPlayerID;
	private static ID battleEntityID;
	private static ID turnEntityID;
	private static ID cursorEntityID;
	
	/**
	 * Should really hang these player IDS off of the Game instead of static
	 */
	private static final Array<ID> playerIDs = new Array<ID>();
	private static final ImmutableArray<ID> immPlayerIDs = new ImmutableArray(playerIDs);
	
	/**
	 * I think this is ok to leave static since its just a cache for ids
	 */
	private static final ObjectMap<ID, ObjectMap<ZoneType, ID>> zoneIDsByOwnerIDAndType = new ObjectMap<ID, ObjectMap<ZoneType, ID>>();
	
	public static ImmutableArray<ID> getPlayerIDs() {
		return immPlayerIDs;
	}
	
	/**
	 * Actually, can probably still use this to hang the turn entity and cursor entity
	 * off of, it would just have an id for each of them. Turn timer would need to be its
	 * own entity I would think, turn phase could live off of battle though. Have to see
	 * if this is easy or hard to move these components around.
	 * @return
	 */
	@Deprecated
	public static ID getBattleEntityID() {
		if(battleEntityID == null) {
			battleEntityID = IDUtil.newID();
		}
		return battleEntityID;
	}
	
	public static ID getTurnEntityID() {
		if(turnEntityID == null) {
			turnEntityID = IDUtil.newID();
		}
		return turnEntityID;
	}
	
	public static ID getPlayerEntityID(int activePlayerIndex) {
		if(activePlayerIndex < 0 || activePlayerIndex >= playerIDs.size) {
			return null;
		}
		return playerIDs.get(activePlayerIndex);
	}
	
	public static ID buildPlayerEntityID() {
		ID id = IDUtil.newID();
		playerIDs.add(id);
		return id;
	}
	
	public static ID getCursorEntityID() {
		if(cursorEntityID == null) {
			cursorEntityID = IDUtil.newID();
		}
		return cursorEntityID;
	}
	
	public static ID getGlobalPlayerEntityID() {
		if(globalPlayerID == null) {
			globalPlayerID = IDUtil.newID();
		}
		return globalPlayerID;
	}
	
	public static ID generateZoneID(ID ownerID, ZoneType type) {
		if(!zoneIDsByOwnerIDAndType.containsKey(ownerID)) {
			zoneIDsByOwnerIDAndType.put(ownerID, new ObjectMap<ZoneType, ID>());
		}
		if(!zoneIDsByOwnerIDAndType.get(ownerID).containsKey(type)) {
			zoneIDsByOwnerIDAndType.get(ownerID).put(type, IDUtil.newID());
		}
		return zoneIDsByOwnerIDAndType.get(ownerID).get(type);
 	}
	
	public static ID getZoneID(ID ownerID, ZoneType type) {
		if(ownerID == null) {
			return getZoneID(getGlobalPlayerEntityID(), type);
		}
		if(ownerID != getGlobalPlayerEntityID() && (!zoneIDsByOwnerIDAndType.containsKey(ownerID) || !zoneIDsByOwnerIDAndType.get(ownerID).containsKey(type))) {
			return getZoneID(getGlobalPlayerEntityID(), type);
		}
		if(!zoneIDsByOwnerIDAndType.containsKey(ownerID)) {
			return null;
		}
		return zoneIDsByOwnerIDAndType.get(ownerID).get(type);
 	}
	
	public static ID newID() {
		return new ID(UUID.randomUUID());
	}
	
}
