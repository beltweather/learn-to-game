package com.jharter.game.util.id;

import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import uk.co.carelesslabs.Enums.ZoneType;

/**
 * Managers ID relationships between players and zones. Eventually, this whole thing
 * could be replaced by the right components and engine families.
 * 
 * @author Jon
 *
 */
public class IDManager {

	private ID globalPlayerID;
	
	private final Array<ID> playerIDs = new Array<ID>();
	private final ImmutableArray<ID> immPlayerIDs = new ImmutableArray<ID>(playerIDs);
	
	/**
	 * I think this is ok to leave since its just a cache for ids
	 */
	private final ObjectMap<ID, ObjectMap<ZoneType, ID>> zoneIDsByOwnerIDAndType = new ObjectMap<ID, ObjectMap<ZoneType, ID>>();
	
	public IDManager() {
		
	}
	
	public ImmutableArray<ID> getPlayerIDs() {
		return immPlayerIDs;
	}
	
	public ID getPlayerEntityID(int activePlayerIndex) {
		if(activePlayerIndex < 0 || activePlayerIndex >= playerIDs.size) {
			return null;
		}
		return playerIDs.get(activePlayerIndex);
	}
	
	public ID buildPlayerEntityID() {
		ID id = IDUtil.newID();
		playerIDs.add(id);
		return id;
	}
	
	public ID getGlobalPlayerEntityID() {
		if(globalPlayerID == null) {
			globalPlayerID = IDUtil.newID();
		}
		return globalPlayerID;
	}
	
	public ID generateZoneID(ID ownerID, ZoneType type) {
		if(!zoneIDsByOwnerIDAndType.containsKey(ownerID)) {
			zoneIDsByOwnerIDAndType.put(ownerID, new ObjectMap<ZoneType, ID>());
		}
		if(!zoneIDsByOwnerIDAndType.get(ownerID).containsKey(type)) {
			zoneIDsByOwnerIDAndType.get(ownerID).put(type, IDUtil.newID());
		}
		return zoneIDsByOwnerIDAndType.get(ownerID).get(type);
 	}
	
	public ID getZoneID(ID ownerID, ZoneType type) {
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
	
	/*private static ID getActivePlayerID(PooledEngine engine) {
		return Comp.ActivePlayerComp.get(engine.getEntitiesFor(Family.all(ActivePlayerComp.class).get()).first()).activePlayerID;
	}*/
}

