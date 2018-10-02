package com.jharter.game.ashley.components.subcomponents;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector3;
import com.jharter.game.ashley.components.CompManager;
import com.jharter.game.ashley.components.Components.SpriteComp;
import com.jharter.game.ashley.entities.IEntityFactory;
import com.jharter.game.layout.TweenTarget;
import com.jharter.game.util.id.ID;

import uk.co.carelesslabs.Enums.Direction;

public class RelativePositionRules {
	
	public boolean enabled = false;
	public boolean tween = true;
	private ID relativeToID = null;
	private RelativeToIDGetter relativeToIDGetter = null;
	public Vector3 offset = new Vector3();
	public Direction xAlign = Direction.NONE;
	public Direction yAlign = Direction.NONE;
	
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
	
	public boolean setToRelativePosition(IEntityFactory factory, SpriteComp s, TweenTarget target) {
		return setToRelativePosition(factory, s, target.scale.x, target.scale.y, target.position);
	}
	
	public boolean setToRelativePosition(IEntityFactory factory, SpriteComp s, Vector3 positionToSet) {
		return setToRelativePosition(factory, s, 1f, 1f, positionToSet);
	}
	
	public boolean setToRelativePosition(IEntityFactory factory, SpriteComp s, float scaleX, float scaleY, Vector3 positionToSet) {
		ID relativeToID = getRelativeToID();
		if(s == null || !enabled || relativeToID == null) {
			return false;
		}
		
		CompManager Comp = factory.getToolBox().getCompManager();
		
		Entity baselineEntity = Comp.Entity.get(relativeToID);
		SpriteComp sBaseline = Comp.SpriteComp.get(baselineEntity);
		if(baselineEntity == null || sBaseline == null) {
			return false;
		}
		
		if(Comp.InvisibleComp.has(baselineEntity)) {
			return false;
		}
		
		float x = sBaseline.position.x;
		float y = sBaseline.position.y;
		float z = s.position.z;
		
		switch(xAlign) {
			case WEST:
			case NORTH_WEST:
			case SOUTH_WEST:
				x -= Comp.SpriteComp(s).scaledWidth(scaleX);
				break;
			case EAST:
			case NORTH_EAST:
			case SOUTH_EAST:
				x += Comp.SpriteComp(sBaseline).scaledWidth();
				break;
			case CENTER:
				x += (Comp.SpriteComp(sBaseline).scaledWidth() - Comp.SpriteComp(s).scaledWidth(scaleX)) / 2f;
				break;
			default:
				break;
		}
		
		switch(yAlign) {
			case SOUTH:
			case SOUTH_WEST:
			case SOUTH_EAST:
				y -= Comp.SpriteComp(s).scaledHeight(scaleY);
				break;
			case NORTH:
			case NORTH_WEST:
			case NORTH_EAST:
				y += Comp.SpriteComp(sBaseline).scaledHeight();
				break;
			case CENTER:
				y += (Comp.SpriteComp(sBaseline).scaledHeight() - Comp.SpriteComp(s).scaledHeight(scaleY)) / 2f;
				break;
			default:
				break;
		}
		
		positionToSet.set(x + offset.x, y + offset.y, z + offset.z);
		return true;
	}
	
	public void reset() {
		enabled = false;
		relativeToID = null;
		relativeToIDGetter = null;
		offset.set(0,0,0);
		xAlign = Direction.NONE;
		yAlign = Direction.NONE;
		tween = true;
	}
	
	public abstract static class RelativeToIDGetter {
		
		public RelativeToIDGetter() {}
		
		public abstract ID getRelativeToID();
		
	}
}