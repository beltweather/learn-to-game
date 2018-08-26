package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.jharter.game.ashley.components.Components.ActiveCardComp;
import com.jharter.game.ashley.components.Components.AlphaComp;
import com.jharter.game.ashley.components.Components.AnimatedPathComp;
import com.jharter.game.ashley.components.Components.CardComp;
import com.jharter.game.ashley.components.Components.ChangeZoneComp;
import com.jharter.game.ashley.components.Components.CursorComp;
import com.jharter.game.ashley.components.Components.IDComp;
import com.jharter.game.ashley.components.Components.InvisibleComp;
import com.jharter.game.ashley.components.Components.MultiPositionComp;
import com.jharter.game.ashley.components.Components.PositionComp;
import com.jharter.game.ashley.components.Components.SizeComp;
import com.jharter.game.ashley.components.Components.TextureComp;
import com.jharter.game.ashley.components.Components.TurnActionComp;
import com.jharter.game.ashley.components.Components.TypeComp;
import com.jharter.game.ashley.components.Components.VelocityComp;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.Components.ZonePositionComp;
import com.jharter.game.ashley.components.Mapper;
import com.jharter.game.ashley.components.subcomponents.TurnAction;
import com.jharter.game.tween.TweenType;
import com.jharter.game.tween.TweenUtil;

import aurelienribon.tweenengine.Tween;
import uk.co.carelesslabs.Enums.ZoneType;
import uk.co.carelesslabs.Media;

public class ZoneChangeSystem extends IteratingSystem {

	@SuppressWarnings("unchecked")
	public ZoneChangeSystem() {
		super(Family.all(IDComp.class, TypeComp.class, ZonePositionComp.class, ChangeZoneComp.class).get());
	}
	
	public void processEntity(Entity entity, float deltaTime) {
		IDComp id = Mapper.IDComp.get(entity);
		TypeComp ty = Mapper.TypeComp.get(entity);
		ZonePositionComp zp = Mapper.ZonePositionComp.get(entity);
		ChangeZoneComp cz = Mapper.ChangeZoneComp.get(entity);
		
		ZoneType targetZoneType;
		if(cz.newZoneType != null && cz.newZoneType != ZoneType.NONE) {
			targetZoneType = cz.newZoneType;
		} else {
			targetZoneType = zp.zoneType();
		}
		
		ZoneComp z = Mapper.ZoneComp.get(targetZoneType);
		
		int targetIndex;
		if(cz.useNextIndex) {
			targetIndex = z.size();
		} else if(cz.newIndex >= 0) {
			targetIndex = cz.newIndex;
		} else {
			targetIndex = zp.index();
		}
		
		if(!cz.useNextIndex && !z.hasIndex(targetIndex)) {
			return;
		}
		
		switch(ty.type) {
			case CARD:
				ZoneComp zOld = Mapper.ZoneComp.get(zp);
				zOld.remove(id.id);
				z.add(id, zp);
				zp.index(targetIndex);
				entity.remove(ChangeZoneComp.class);
				break;
			case CURSOR:
				if(cz.checkpoint) {
					zp.checkpoint();
				}
				zp.zoneType(targetZoneType);
				zp.index(targetIndex);
				entity.remove(ChangeZoneComp.class);
				break;
			case FRIEND:
				
				break;
			case ENEMY:
				
				break;
			default:
				break;
		}
	}
	
}

