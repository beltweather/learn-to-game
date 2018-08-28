package com.jharter.game.ashley.systems;

import java.util.Comparator;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;
import com.jharter.game.ashley.components.Components.CursorComp;
import com.jharter.game.ashley.components.Components.DisabledComp;
import com.jharter.game.ashley.components.Components.InvisibleComp;
import com.jharter.game.ashley.components.Components.SpriteComp;
import com.jharter.game.ashley.components.Components.TextureComp;
import com.jharter.game.ashley.components.Components.TileComp;
import com.jharter.game.ashley.components.Components.TypeComp;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.Components.ZonePositionComp;
import com.jharter.game.ashley.components.Mapper;
import com.jharter.game.ashley.components.subcomponents.TurnAction;

import uk.co.carelesslabs.Enums.EntityType;
import uk.co.carelesslabs.Enums.ZoneType;

public class RenderEntitiesSystem extends SortedIteratingSystem {
	
	private SpriteBatch batch;
	private ShapeRenderer shapeRenderer;
	private OrthographicCamera camera;

	@SuppressWarnings("unchecked")
	public RenderEntitiesSystem (OrthographicCamera camera) {
		super(Family.all(SpriteComp.class, TextureComp.class).exclude(InvisibleComp.class, TileComp.class, DisabledComp.class).get(), new PositionSort());
		this.camera = camera;
		this.batch = new SpriteBatch();
		this.shapeRenderer = new ShapeRenderer();
	}

	@Override
	public void update (float deltaTime) {
		shapeRenderer.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.setProjectionMatrix(camera.combined);
		forceSort();
		super.update(deltaTime);
		batch.end();
	}
	
	@Override
	public void processEntity(Entity entity, float deltaTime) {
		TextureComp t = Mapper.TextureComp.get(entity);
		SpriteComp s = Mapper.SpriteComp.get(entity);
		
		// XXX DEBUG CODE!!!!!
		TypeComp ty = Mapper.TypeComp.get(entity);
		boolean isCursor = ty != null && ty.type == EntityType.CURSOR;
		////////////////////////////////
				
		boolean hasAlpha = s.alpha != 1f;
		
		if(t.region == null) {
			t.region = t.defaultRegion;
		}
		if(t.region != null) {
			
			Color c = null;
			if(hasAlpha) {
				c = batch.getColor();
				batch.setColor(c.r, c.g, c.b, s.alpha);
			}
			
			float offsetX = (s.scaledWidth() - s.width)/2;
			float offsetY = (s.scaledHeight() - s.height)/2;
			float originX = s.width/2;
			float originY = s.height/2;
			if(Mapper.MultiPositionComp.has(entity)) {
				for(Vector3 position : Mapper.MultiPositionComp.get(entity).positions) {
					batch.draw(t.region, round(position.x + offsetX), round(position.y + offsetY), round(originX), round(originY), s.width, s.height, s.scale.x, s.scale.y, s.angleDegrees);
					if(isCursor) {
						drawLines(entity, s, position);
					}
				}
			} else {
				batch.draw(t.region, round(s.position.x + offsetX), round(s.position.y + offsetY), round(originX), round(originY), s.width, s.height, s.scale.x, s.scale.y, s.angleDegrees);
				if(isCursor) {
					drawLines(entity, s, s.position);
				}
			}
			
			if(hasAlpha) {
				batch.setColor(c);
			}
		}
	}
	
	private float round(float v) {
		return Math.round(v);
	}
	
	private static class PositionSort implements Comparator<Entity> {
		private ComponentMapper<SpriteComp> sm = ComponentMapper.getFor(SpriteComp.class);
		
		@Override
		public int compare(Entity entityA, Entity entityB) {
			Vector3 posA = sm.get(entityA).position;
			Vector3 posB = sm.get(entityB).position;
			if(posA.z == posB.z) {
				return (int) (posB.y - posA.y);
			}
			return (int) (posA.z - posB.z);
		}
	}
	
	// DEBUG!!!!!!!!!!!!!!!!!!
	private void drawLines(Entity cursor, SpriteComp s, Vector3 position) {
		CursorComp c = Mapper.CursorComp.get(cursor);
		ZonePositionComp zp = Mapper.ZonePositionComp.get(cursor);
		ZoneComp z = Mapper.ZoneComp.get(zp);
		TextureComp te = Mapper.TextureComp.get(cursor);
		
		// Make sure cursor is in a valid place
		if(!z.hasIndex(zp.index) || z.zoneType != ZoneType.ACTIVE_CARD) {
			return;
		}
		
		// See if cursor is modifying an action
		TurnAction t = c.getTurnAction();
		if(t == null) {
			return;
		}
		
		// See if the cursor has already selected a card from hand
		// that will force the card it targets to target all
		boolean forceAll = t.makesTargetAll;
		int forceMultiplicity = t.makesTargetMultiplicity;
		
		//batch.end();
		//shapeRenderer.begin(ShapeType.Line);
		//shapeRenderer.setColor(0, 0, 0, 1f);
		
		// Get the card that the cursor is above and verify it has a turn action associated with it
		Entity activeCard = Mapper.Entity.get(z.objectIDs.get(zp.index));
		if(Mapper.TurnActionComp.has(activeCard)) {
			
			TurnAction turnAction = Mapper.TurnActionComp.get(activeCard).turnAction;
			if(turnAction.targetIDs.size > 1) {
				
				int multiplicity = Math.max(forceMultiplicity, turnAction.multiplicity);
				
				// Iterate through all targets of this card, looking in particular for the last two pairs
				// of targets so we can handle their "all" status or lack thereof
				for(int j = 0; j < turnAction.targetIDs.size - 1; j++) {
					SpriteComp sTargetA = Mapper.SpriteComp.get(Mapper.Entity.get(turnAction.targetIDs.get(j)));
					Entity subTargetEntity = Mapper.Entity.get(turnAction.targetIDs.get(j+1));
					
					// If the last pairs have an "all connection", find all targets within that zone and
					// render lines to them.
					if((turnAction.all || forceAll) && j == turnAction.targetIDs.size - 2) {
						ZonePositionComp subTargetZone = Mapper.ZonePositionComp.get(subTargetEntity);
						ZoneComp zone = subTargetZone.getZoneComp();
						for(int k = 0; k < zone.objectIDs.size(); k++) {
							SpriteComp sTargetB = Mapper.SpriteComp.get(Mapper.Entity.get(zone.objectIDs.get(k)));
							//curve(sTargetA.position, sTargetB.position);
							
							for(int m = 0; m < multiplicity; m++) {
								batch.draw(te.region, sTargetB.position.x - 25 * m, sTargetB.position.y - 10 * m, 0, 0, s.width, s.height, 0.5f*s.scale.x, 0.5f*s.scale.y, s.angleDegrees);
							}
							
						}
					
					// Otherwise, connect the pairs as usual
					} else if(j == turnAction.targetIDs.size - 2) {
						SpriteComp sTargetB = Mapper.SpriteComp.get(Mapper.Entity.get(turnAction.targetIDs.get(j+1)));
						//curve(sTargetA.position, sTargetB.position);
						
						for(int m = 0; m < multiplicity; m++) {
							batch.draw(te.region, sTargetB.position.x - 30 * m, sTargetB.position.y - 10 * m, 0, 0, s.width, s.height, 0.5f*s.scale.x, 0.5f*s.scale.y, s.angleDegrees);
						}
					}
				}
			}
		}

		//shapeRenderer.end();
		//batch.begin();
		//batch.setProjectionMatrix(camera.combined);
	}
	
	private void curve(Vector3 from, Vector3 to) {
		int segments = 30;
		float maxY = Math.max(to.y, from.y) * 1.5f;
		shapeRenderer.curve(from.x, from.y, from.x, maxY, to.x, maxY, to.x, to.y, segments);
		shapeRenderer.curve(from.x, from.y-10, from.x, maxY-10, to.x, maxY-10, to.x, to.y-10, segments);
	}
	
}