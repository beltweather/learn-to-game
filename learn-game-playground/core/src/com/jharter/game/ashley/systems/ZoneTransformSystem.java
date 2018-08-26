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

@Deprecated
public class ZoneTransformSystem extends IteratingSystem {

	private Entity entity;
	private TypeComp ty;
	private TextureComp te;
	private ZonePositionComp zp;
	private ZoneComp z;
	private PositionComp p;
	private SizeComp s;
	private AlphaComp a;
	private ChangeZoneComp cz;
	
	private ZoneType targetZone;
	private int targetIndex;
	
	@SuppressWarnings("unchecked")
	public ZoneTransformSystem() {
		super(Family.all(TypeComp.class, TextureComp.class, ZonePositionComp.class, PositionComp.class, SizeComp.class, ChangeZoneComp.class).get());
	}
	
	public void processEntity(Entity entity, float deltaTime) {
		this.entity = entity;
		ty = Mapper.TypeComp.get(entity);
		te = Mapper.TextureComp.get(entity);
		zp = Mapper.ZonePositionComp.get(entity);
		z = Mapper.ZoneComp.get(zp);
		p = Mapper.PositionComp.get(entity);
		s = Mapper.SizeComp.get(entity);
		a = Mapper.AlphaComp.get(entity);
		cz = Mapper.ChangeZoneComp.get(entity);
		
		if(!z.hasIndex(zp.index)) {
			hide();
			return;
		}
		
		switch(ty.type) {
			case CARD:
				transformCard(deltaTime);
				break;
			case CURSOR:
				transformCursor();
				break;
			case FRIEND:
				
				break;
			case ENEMY:
				
				break;
			default:
				break;
		}
		
		if(a != null) {
			if(Mapper.UntargetableComp.has(entity)) {
				a.alpha = 0.25f;
			} else {
				a.alpha = 1f;
			}
		}
		
	}
	
	private boolean isHidden() {
		return Mapper.InvisibleComp.has(entity);
	}
	
	private void hide() {
		if(!isHidden()) {
			entity.add(Mapper.Comp.get(InvisibleComp.class));
		}
	}
	
	private void show() {
		if(isHidden()) {
			entity.remove(InvisibleComp.class);
		}
	}
	
	private void transformCursor() {
		if(Mapper.ActionQueuedComp.has(entity) || Mapper.TurnEntity.TurnTimerComp().isStopped()) {
			hide();
			return;
		}
		
		show();
		te.region = getCursorForZone(zp.zoneType);
		
		CursorComp c = Mapper.CursorComp.get(entity);
		TurnAction ta = c.getTurnAction();
		
		if(ta != null && ta.all) {
			
			MultiPositionComp mp;
			if(Mapper.MultiPositionComp.has(entity)) {
				mp = Mapper.MultiPositionComp.get(entity);
			} else {
				mp = Mapper.Comp.get(MultiPositionComp.class);
				entity.add(mp);
			}
			mp.positions.clear();
			for(int i = 0; i < z.objectIDs.size(); i++) {
				Entity target = Mapper.Entity.get(z.objectIDs.get(i));
				PositionComp tp = Mapper.PositionComp.get(target);
				SizeComp ts = Mapper.SizeComp.get(target);
				Vector3 position = new Vector3(0, 0, 0);
				setCursorPositionForTarget(position, target, tp, ts);
				mp.positions.add(position);
			}
			
		} else {
			if(Mapper.MultiPositionComp.has(entity)) {
				entity.remove(MultiPositionComp.class);
			}
			
			Entity target = Mapper.Entity.get(z.objectIDs.get(zp.index));
			PositionComp tp = Mapper.PositionComp.get(target);
			SizeComp ts = Mapper.SizeComp.get(target);
			setCursorPositionForTarget(p.position, target, tp, ts);
		}
		
	}
	
	private void setCursorPositionForTarget(Vector3 position, Entity target, PositionComp tp, SizeComp ts) {
		switch(zp.zoneType) {
			case HAND:
				position.x = tp.position.x + (ts.scaledWidth() - s.scaledWidth()) / 2;
				position.y = tp.position.y + ts.scaledHeight() - (int) (s.scaledHeight() * 0.25);
				break;
			case FRIEND:
				ActiveCardComp ac = Mapper.ActiveCardComp.get(target);
				if(ac != null && ac.activeCardID != null) {
					Entity card = Mapper.Entity.get(ac.activeCardID);
					if(card != null) {
						tp = Mapper.PositionComp.get(card);
						ts = Mapper.SizeComp.get(card);
					}
				}
				position.x = tp.position.x - s.scaledWidth() - 20;
				position.y = tp.position.y + (ts.scaledHeight() - s.scaledHeight()) / 2;
				break;
			case ACTIVE_CARD:
				tp = Mapper.PositionComp.get(target);
				ts = Mapper.SizeComp.get(target);
				position.x = tp.position.x - s.scaledWidth() - 20;
				position.y = tp.position.y + (ts.scaledHeight() - s.scaledHeight()) / 2;
				break;
			case ENEMY:
				position.x = tp.position.x + ts.scaledWidth() + 20;  
				position.y = tp.position.y + (ts.scaledHeight() - s.scaledHeight()) / 2;
				break;
			default:
				break;
		}
	}
	
	private void transformCard(float deltaTime) {
		
		switch(zp.zoneType) {
			case DECK:
				hide();
				break;
			case HAND:
				show();
				if(Mapper.AnimatedPathComp.has(entity)) {
					entity.remove(AnimatedPathComp.class);
				}
				int anchorX = -700;
				int anchorY = -475;
				s.scale.set(1f, 1f);
				p.position.x = anchorX + (s.scaledWidth() + 20) * zp.index;
				p.position.y = anchorY;
				break;
			case ACTIVE_CARD:
				show();
				CardComp c = Mapper.CardComp.get(entity);
				TurnActionComp t = Mapper.TurnActionComp.get(entity);
				Entity owner = Mapper.Entity.get(c.ownerID);
				PositionComp pOwner = Mapper.PositionComp.get(owner);
				PositionComp pCard = Mapper.PositionComp.get(entity);
				IDComp id = Mapper.IDComp.get(entity);
				SizeComp sOwner = Mapper.SizeComp.get(owner);
				SizeComp sCard = Mapper.SizeComp.get(entity);
				VelocityComp v = Mapper.VelocityComp.get(entity);
				v.speed = 3500;
				float targetScale = 0.25f;
				
				float targetX = (pOwner.position.x - sCard.scaledWidth(targetScale) - 20);
				float targetY = (pOwner.position.y + (sOwner.scaledHeight() - sCard.scaledHeight(targetScale)) / 2);
				
				boolean animating = Mapper.AnimatingComp.has(entity); 
				if(!animating && !isCloseEnough(pCard.position.x, pCard.position.y, targetX, targetY, v.speed, 10, deltaTime)) {
					TweenUtil.start(Tween.to(id.id, TweenType.POSITION_XY.asInt(), 1).target(targetX, targetY));
				} else if(!animating){
					sCard.scale.set(targetScale, targetScale);
					pCard.position.x = pOwner.position.x - sCard.scaledWidth() - 20;
					pCard.position.y = pOwner.position.y + (sOwner.scaledHeight() - sCard.scaledHeight()) / 2;
					v.velocity.set(0, 0);
					v.speed = 0;
					
					if(t.turnAction.multiplicity > 1) {
						if(!Mapper.MultiPositionComp.has(entity)) {
							entity.add(Mapper.Comp.get(MultiPositionComp.class));
						}
						MultiPositionComp m = Mapper.MultiPositionComp.get(entity);
						m.positions.clear();
						for(int i = 0; i < t.turnAction.multiplicity; i++) {
							m.positions.add(new Vector3(pCard.position.x - 10*i, pCard.position.y + 10*i, pCard.position.z));
						}
					} else if(Mapper.MultiPositionComp.has(entity)) {
						entity.remove(MultiPositionComp.class);
					}
				}
				break;
				
				/*case ACTIVE_CARD:
				show();
				CardComp c = Mapper.CardComp.get(entity);
				TurnActionComp t = Mapper.TurnActionComp.get(entity);
				Entity owner = Mapper.Entity.get(c.ownerID);
				PositionComp pOwner = Mapper.PositionComp.get(owner);
				PositionComp pCard = Mapper.PositionComp.get(entity);
				SizeComp sOwner = Mapper.SizeComp.get(owner);
				SizeComp sCard = Mapper.SizeComp.get(entity);
				VelocityComp v = Mapper.VelocityComp.get(entity);
				v.speed = 3500;
				float targetScale = 0.25f;
				
				float targetX = (pOwner.position.x - sCard.scaledWidth(targetScale) - 20);
				float targetY = (pOwner.position.y + (sOwner.scaledHeight() - sCard.scaledHeight(targetScale)) / 2);
				
				AnimatedPathComp ma;
				if((targetX != pCard.position.x || targetY != pCard.position.y) && !Mapper.AnimatedPathComp.has(entity)) {
					ma = Mapper.Comp.get(AnimatedPathComp.class);
					entity.add(ma);
					ma.startPosition.x = pCard.position.x;
					ma.startPosition.y = pCard.position.y;
					//pCard.renderPosition = new Vector3();
					//pCard.renderPosition.set(p.position.x, p.position.y, 0);
					//sCard.renderScale = new Vector2();
					//sCard.renderScale.x = sCard.scale.x;
					//sCard.renderScale.y = sCard.scale.y;
				} else {
					ma = Mapper.AnimatedPathComp.get(entity);
				}
				
				if(ma == null || ma.isCloseEnough(pCard, v, deltaTime)) {
					sCard.scale.set(targetScale, targetScale);
					pCard.position.x = pOwner.position.x - sCard.scaledWidth() - 20;
					pCard.position.y = pOwner.position.y + (sOwner.scaledHeight() - sCard.scaledHeight()) / 2;
					//pCard.renderPosition = null;
					//sCard.renderScale = null;
					v.velocity.set(0, 0);
					v.speed = 0;
					
					if(t.turnAction.multiplicity > 1) {
						if(!Mapper.MultiPositionComp.has(entity)) {
							entity.add(Mapper.Comp.get(MultiPositionComp.class));
						}
						MultiPositionComp m = Mapper.MultiPositionComp.get(entity);
						m.positions.clear();
						for(int i = 0; i < t.turnAction.multiplicity; i++) {
							m.positions.add(new Vector3(pCard.position.x - 10*i, pCard.position.y + 10*i, pCard.position.z));
						}
					} else if(Mapper.MultiPositionComp.has(entity)) {
						entity.remove(MultiPositionComp.class);
					}
					
					if(ma != null) {
						entity.remove(AnimatedPathComp.class);
					}
					
				} else if(ma != null) {
					ma.targetPosition.x = targetX;
					ma.targetPosition.y = targetY + targetX - pCard.position.x;
					ma.setVelocityFromPath(pCard, v, deltaTime);
					float scale = 1f - (1f-targetScale)*ma.getProgress(pCard);
					sCard.scale.set(scale, scale);
				}
				break;*/
				
				
				
				
				
				
				
			case DISCARD:
				hide();
				break;
			default:
				break;
		}
			
	}
	
	private boolean isCloseEnough(float x, float y, float targetX, float targetY, float speed, float tolerance, float deltaTime) {
		return targetX - x <= speed / tolerance * deltaTime && targetY - y <= speed / tolerance * deltaTime;
	}
	
	private TextureRegion getCursorForZone(ZoneType zoneType) {
		switch(zoneType) {
			case HAND:
				return Media.handPointDown;
			case FRIEND:
			case ACTIVE_CARD:
				 return Media.handPointRight;
			case ENEMY:
				return Media.handPointLeft;
			default:
				return Media.handPointDown;
		}
	}

}

