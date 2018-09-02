package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.ObjectMap;
import com.jharter.game.ashley.components.Comp;
import com.jharter.game.ashley.components.Components.AnimatingComp;
import com.jharter.game.ashley.components.Components.TurnPhaseComp;
import com.jharter.game.ashley.components.Ent;

public abstract class TurnPhaseSystem extends IteratingSystem {

	private Class<? extends Component> phaseClass;
	private Class<? extends Component> nextPhaseClass;
	private Class<? extends Component> alternativeNextPhaseClass;
	private boolean phaseStarted = false;
	private boolean phaseShouldEnd = false;
	private boolean phaseIsFamily = false;
	private boolean endPhaseWhenEntitiesGone = false;
	private ObjectMap<Class, Family> families = new ObjectMap<Class, Family>();
	private boolean hasEntities = false;
	
	@SuppressWarnings("unchecked")
	public TurnPhaseSystem(Class<? extends Component> phaseClass, Class<? extends Component> nextPhaseClass) {
		this(phaseClass, nextPhaseClass, null);
	}
	
	@SuppressWarnings("unchecked")
	public TurnPhaseSystem(Class<? extends Component> phaseClass, Class<? extends Component> nextPhaseClass, Family family) {
		super(family == null ? Family.all(TurnPhaseComp.class, phaseClass).get() : family);
		this.phaseIsFamily = family == null;
		this.phaseClass = phaseClass;
		this.nextPhaseClass = nextPhaseClass;
	}
	
	@Override
	public void update (float deltaTime) {
		if(!Comp.has(phaseClass, Ent.TurnEntity.Entity())) {
			return;
		}
		if(!phaseStarted) {
			startPhase(Ent.TurnEntity.Entity(), deltaTime);
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
			endPhase(Ent.TurnEntity.Entity(), deltaTime);
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
		//Sys.out.println("Waiting to Start Phase: " + phaseClass.getSimpleName());
		phaseStarted = processEntityPhaseStart(entity, deltaTime);
		if(phaseStarted) {
			//Sys.out.println("Start Phase: " + phaseClass.getSimpleName());
		}
	}
	
	protected void endPhase(Entity entity, float deltaTime) {
		processEntityPhaseEnd(entity, deltaTime);
		entity.remove(phaseClass);
		
		if(alternativeNextPhaseClass != null) {
			entity.add(Comp.create(getEngine(), alternativeNextPhaseClass));
			alternativeNextPhaseClass = null;
		} else {
			entity.add(Comp.create(getEngine(), nextPhaseClass));
		}
		
		phaseStarted = false;
		phaseShouldEnd = false;
		//endPhaseWhenEntitiesGone = false;
		//Sys.out.println("End Phase: " + phaseClass.getSimpleName() + " (Next Phase: " + nextPhaseClass.getSimpleName() + ")");
	}
	
	protected boolean endAndReroute(Class<? extends Component> nextPhaseClass) {
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
	
	protected void resetCursor() {
		Ent.CursorEntity.reset();
	}
	
	protected void enableCursor() {
		Ent.CursorEntity.enable();
	}
	
	protected void disableCursor() {
		Ent.CursorEntity.disable(getEngine());
	}
	
	protected boolean has(Class<? extends Component> klass) {
		return count(klass) > 0;
	}
	
	protected boolean isDoneAnimating() {
		return !has(AnimatingComp.class);
	}

	protected int count(Class<? extends Component> klass) {
		return getEntities(klass).size();
	}
	
	protected ImmutableArray<Entity> getEntities(Class<? extends Component> klass) {
		if(!families.containsKey(klass)) {
			families.put(klass, Family.all(klass).get());
		}
		return getEngine().getEntitiesFor(families.get(klass));
	}
	
	
}
