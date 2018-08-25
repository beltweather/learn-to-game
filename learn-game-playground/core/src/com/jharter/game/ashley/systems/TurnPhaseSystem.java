package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.ObjectMap;
import com.jharter.game.ashley.components.Components.Comp;
import com.jharter.game.ashley.components.Components.TurnPhaseComp;
import com.jharter.game.ashley.components.Mapper;

public abstract class TurnPhaseSystem extends IteratingSystem {

	private Class<? extends Comp> phaseClass;
	private Class<? extends Comp> nextPhaseClass;
	private Class<? extends Comp> alternativeNextPhaseClass;
	private boolean phaseStarted = false;
	private boolean phaseShouldEnd = false;
	private boolean phaseIsFamily = false;
	private boolean endPhaseWhenEntitiesGone = false;
	private ObjectMap<Class, Family> families = new ObjectMap<Class, Family>();
	private boolean hasEntities = false;
	
	@SuppressWarnings("unchecked")
	public TurnPhaseSystem(Class<? extends Comp> phaseClass, Class<? extends Comp> nextPhaseClass) {
		this(phaseClass, nextPhaseClass, null);
	}
	
	@SuppressWarnings("unchecked")
	public TurnPhaseSystem(Class<? extends Comp> phaseClass, Class<? extends Comp> nextPhaseClass, Family family) {
		super(family == null ? Family.all(TurnPhaseComp.class, phaseClass).get() : family);
		this.phaseIsFamily = family == null;
		this.phaseClass = phaseClass;
		this.nextPhaseClass = nextPhaseClass;
	}
	
	@Override
	public void update (float deltaTime) {
		if(!Mapper.Comp.has(phaseClass, Mapper.TurnEntity.Entity())) {
			return;
		}
		if(!phaseStarted) {
			startPhase(Mapper.TurnEntity.Entity(), deltaTime);
			if(!phaseStarted) {
				return;
			}
		}
		hasEntities = false;
		super.update(deltaTime);
		if(!hasEntities && endPhaseWhenEntitiesGone) {
			phaseShouldEnd = true;
		}
		
		if(phaseShouldEnd) {
			endPhase(Mapper.TurnEntity.Entity(), deltaTime);
		}
	}
	
	/**
	 * @return True if the phase should start, false if it shouldn't yet.
	 */
	protected abstract boolean processEntityPhaseStart(Entity turnEntity, float deltaTime);

	/**
	 * @return True if the phase should end, false if it should not.
	 */
	protected abstract boolean processEntityPhaseMiddle(Entity entity, float deltaTime);
	
	protected abstract void processEntityPhaseEnd(Entity turnEntity, float deltaTime);
	
	protected void startPhase(Entity entity, float deltaTime) {
		System.out.println("Waiting to Start Phase: " + phaseClass.getSimpleName());
		phaseStarted = processEntityPhaseStart(entity, deltaTime);
		if(phaseStarted) {
			System.out.println("Start Phase: " + phaseClass.getSimpleName());
		}
	}
	
	protected void endPhase(Entity entity, float deltaTime) {
		processEntityPhaseEnd(entity, deltaTime);
		entity.remove(phaseClass);
		
		if(alternativeNextPhaseClass != null) {
			entity.add(Mapper.Comp.get(alternativeNextPhaseClass));
			alternativeNextPhaseClass = null;
		} else {
			entity.add(Mapper.Comp.get(nextPhaseClass));
		}
		
		phaseStarted = false;
		phaseShouldEnd = false;
		//endPhaseWhenEntitiesGone = false;
		System.out.println("End Phase: " + phaseClass.getSimpleName() + " (Next Phase: " + nextPhaseClass.getSimpleName() + ")");
	}
	
	protected boolean endAndReroute(Class<? extends Comp> nextPhaseClass) {
		alternativeNextPhaseClass = nextPhaseClass;
		return true;
	}
	
	protected void endIfNoMoreEntities() {
		endPhaseWhenEntitiesGone = true;
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		hasEntities = true;
		boolean shouldEnd = processEntityPhaseMiddle(entity, deltaTime);
		if(shouldEnd) {
			phaseShouldEnd = true;
		}
	}
	
	protected void enableCursor() {
		Mapper.CursorEntity.enable();
	}
	
	protected void disableCursor() {
		Mapper.CursorEntity.disable();
	}
	
	protected boolean has(Class<? extends Comp> klass) {
		return count(klass) > 0;
	}
	
	protected int count(Class<? extends Comp> klass) {
		if(!families.containsKey(klass)) {
			families.put(klass, Family.all(klass).get());
		}
		return getEngine().getEntitiesFor(families.get(klass)).size();
	}
	
}
