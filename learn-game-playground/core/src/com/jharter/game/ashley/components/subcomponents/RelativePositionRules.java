package com.jharter.game.ashley.components.subcomponents;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector3;
import com.jharter.game.ashley.components.Components.SpriteComp;
import com.jharter.game.ashley.components.Mapper;
import com.jharter.game.util.id.ID;

import uk.co.carelesslabs.Enums.Direction;

public class RelativePositionRules {
	
	public boolean relative = false;
	private ID relativeToID = null;
	private RelativeToIDGetter relativeToIDGetter = null;
	public Vector3 relativeOffset = new Vector3();
	public Direction relativeXAlign = Direction.NONE;
	public Direction relativeYAlign = Direction.NONE;
	
	public RelativePositionRules() {

	}
	
	public ID getRelativeToID() {
		if(relativeToIDGetter != null) {
			return relativeToIDGetter.getRelativeToID();
		}
		return relativeToID;
	}
	
	public boolean hasRelativeToID() {
		return getRelativeToID() != null;
	}
	
	public void setRelativeToID(ID relativeToID) {
		this.relativeToID = relativeToID;
	}
	
	public void setRelativeToIDGetter(RelativeToIDGetter relativeToIDGetter) {
		this.relativeToIDGetter = relativeToIDGetter;
	}
	
	public boolean setToRelativePosition(SpriteComp s, Vector3 positionToSet) {
		ID relativeToID = getRelativeToID();
		if(s == null || !relative || relativeToID == null) {
			return false;
		}
		
		Entity baselineEntity = Mapper.Entity.get(relativeToID);
		SpriteComp sBaseline = Mapper.SpriteComp.get(baselineEntity);
		if(baselineEntity == null || sBaseline == null) {
			return false;
		}
		
		if(Mapper.InvisibleComp.has(baselineEntity)) {
			return false;
		}
		
		float x = sBaseline.position.x;
		float y = sBaseline.position.y;
		float z = s.position.z;
		
		switch(relativeXAlign) {
			case WEST:
			case NORTH_WEST:
			case SOUTH_WEST:
				x -= s.scaledWidth();
				break;
			case EAST:
			case NORTH_EAST:
			case SOUTH_EAST:
				x += sBaseline.scaledWidth();
				break;
			case CENTER:
				x += (sBaseline.scaledWidth() - s.scaledWidth()) / 2f;
				break;
			default:
				break;
		}
		
		switch(relativeYAlign) {
			case SOUTH:
			case SOUTH_WEST:
			case SOUTH_EAST:
				y -= s.scaledHeight();
				break;
			case NORTH:
			case NORTH_WEST:
			case NORTH_EAST:
				y += sBaseline.scaledHeight();
				break;
			case CENTER:
				y += (sBaseline.scaledHeight() - s.scaledHeight()) / 2f;
				break;
			default:
				break;
		}
		
		positionToSet.set(x + relativeOffset.x, y + relativeOffset.y, z + relativeOffset.z);
		return true;
	}
	
	public void reset() {
		relative = false;
		relativeToID = null;
		relativeToIDGetter = null;
		relativeOffset.set(0,0,0);
		relativeXAlign = Direction.NONE;
		relativeYAlign = Direction.NONE;
	}
	
	public abstract static class RelativeToIDGetter {
		
		public RelativeToIDGetter() {}
		
		public abstract ID getRelativeToID();
		
	}
}