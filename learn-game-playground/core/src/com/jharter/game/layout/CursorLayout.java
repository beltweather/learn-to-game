package com.jharter.game.layout;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector3;
import com.jharter.game.ecs.components.Components.CursorComp;
import com.jharter.game.ecs.components.Components.SpriteComp;
import com.jharter.game.ecs.components.Components.ZoneComp;
import com.jharter.game.ecs.components.Components.ZonePositionComp;
import com.jharter.game.ecs.entities.IEntityHandler;
import com.jharter.game.util.CursorManager;
import com.jharter.game.util.id.ID;

public class CursorLayout extends ZoneLayout {
	
	private CursorManager cursorManager;
	
	public CursorLayout(IEntityHandler handler) {
		super(handler);
		this.cursorManager = new CursorManager(this);
	}
	
	@Override
	protected TweenTarget getTarget(ID id, int index, Entity cursor, TweenTarget tt) {
		CursorComp c = Comp.CursorComp.get(cursor);
		SpriteComp s = Comp.SpriteComp.get(cursor);
		
		if(c.targetID == null) {
			return null;
		}
		
		Entity target = Comp.Entity.get(c.targetID);
		
		ZonePositionComp zp = Comp.ZonePositionComp.get(target);
		ZoneComp z = Comp.ZoneComp.get(zp.zoneID);
		float targetAngle = cursorManager.getCursorAngle(z.zoneType);
		
		Vector3 targetPosition = cursorManager.getCursorPosition(cursor, c.targetID, z);
		if(targetPosition == null) {
			return null;
		}
		
		tt.setFromSpriteComp(s);
		tt.position.x = targetPosition.x;
		tt.position.y = targetPosition.y;
		tt.angleDegrees = targetAngle;
		
		if(tt.matchesTarget(s)) {
			return null;
		}
		
		// If we haven't changed zones since our last target, increase our cursor
		// speed for better responsiveness.
		if(!cursorManager.changedZones(cursor)) { 
			tt.duration = 0.10f;
		}
		
		return tt;
	}
	
}
