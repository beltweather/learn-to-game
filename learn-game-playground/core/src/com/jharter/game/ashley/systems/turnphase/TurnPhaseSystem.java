package com.jharter.game.ashley.systems.turnphase;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.jharter.game.ashley.components.Comp;
import com.jharter.game.ashley.components.Components.AnimatingComp;
import com.jharter.game.ashley.components.Components.CursorComp;
import com.jharter.game.ashley.components.Components.DisabledComp;
import com.jharter.game.ashley.components.Components.MultiSpriteComp;
import com.jharter.game.ashley.components.Components.TurnPhaseComp;
import com.jharter.game.ashley.components.Components.TurnTimerComp;
import com.jharter.game.ashley.components.subcomponents.TurnTimer;
import com.jharter.game.ashley.systems.boilerplate.FirstSystem;

public abstract class TurnPhaseSystem extends FirstSystem {

	private Class<? extends Component> phaseClass;
	private Class<? extends Component> nextPhaseClass;
	private Class<? extends Component> alternativeNextPhaseClass;
	private boolean phaseStarted = false;
	private boolean phaseShouldEnd = false;
	
	@SuppressWarnings("unchecked")
	public TurnPhaseSystem(Class<? extends Component> phaseClass, Class<? extends Component> nextPhaseClass) {
		super(Family.all(TurnPhaseComp.class, phaseClass).get());
		add(CursorComp.class);
		add(TurnTimerComp.class);
		add(TurnPhaseComp.class);
		add(AnimatingComp.class);
		
		this.phaseClass = phaseClass;
		this.nextPhaseClass = nextPhaseClass;
	}
	
	@Override
	protected void processEntity(Entity turnPhase, float deltaTime) {
		if(!phaseStarted) {
			startPhase(turnPhase, deltaTime);
			if(!phaseStarted) {
				return;
			}
		}
		phaseShouldEnd = processEntityPhaseMiddle(turnPhase, deltaTime);
		if(phaseShouldEnd) {
			endPhase(turnPhase, deltaTime);
		}
	}
	
	/**
	 * @return True if the phase should start, false if it shouldn't yet.
	 */
	protected abstract boolean processEntityPhaseStart(Entity turnPhase, float deltaTime);

	/**
	 * @return True if the phase should end, false if it should not.
	 */
	protected abstract boolean processEntityPhaseMiddle(Entity turnPhase, float deltaTime);
	
	protected abstract void processEntityPhaseEnd(Entity turnPhase, float deltaTime);
	
	protected void startPhase(Entity turnPhase, float deltaTime) {
		//Sys.out.println("Waiting to Start Phase: " + phaseClass.getSimpleName());
		phaseStarted = processEntityPhaseStart(turnPhase, deltaTime);
		if(phaseStarted) {
			//Sys.out.println("Start Phase: " + phaseClass.getSimpleName());
		}
	}
	
	protected void endPhase(Entity turnPhase, float deltaTime) {
		processEntityPhaseEnd(turnPhase, deltaTime);
		turnPhase.remove(phaseClass);
		
		if(alternativeNextPhaseClass != null) {
			turnPhase.add(Comp.create(getEngine(), alternativeNextPhaseClass));
			alternativeNextPhaseClass = null;
		} else {
			turnPhase.add(Comp.create(getEngine(), nextPhaseClass));
		}
		
		phaseStarted = false;
		phaseShouldEnd = false;
		//Sys.out.println("End Phase: " + phaseClass.getSimpleName() + " (Next Phase: " + nextPhaseClass.getSimpleName() + ")");
	}
	
	protected boolean endAndReroute(Class<? extends Component> nextPhaseClass) {
		alternativeNextPhaseClass = nextPhaseClass;
		return true;
	}
	
	protected Entity getTurnPhaseEntity() {
		return getFirstEntity(TurnPhaseComp.class);
	}
	
	protected CursorComp getCursorComp() {
		return getFirstComponent(CursorComp.class);
	}
	
	protected TurnTimer getTurnTimer() {
		return getFirstComponent(TurnTimerComp.class).turnTimer;
	}
	
	protected void resetCursor() {
		Entity cursor = getFirstEntity(CursorComp.class);
		if(cursor == null) {
			return;
		}
		Comp.remove(MultiSpriteComp.class, cursor);
		Comp.CursorComp.get(cursor).reset();
	}
	
	protected void enableCursor() {
		Entity cursor = getFirstEntity(CursorComp.class);
		if(cursor == null) {
			return;
		}
		Comp.remove(DisabledComp.class, cursor);
		Comp.InputComp.get(cursor).input.reset();
	}
	
	protected void disableCursor() {
		Entity cursor = getFirstEntity(CursorComp.class);
		if(cursor == null) {
			return;
		}
		Comp.add(getEngine(), DisabledComp.class, cursor);
		Comp.InputComp.get(cursor).input.reset();
	}
	
	protected boolean isDoneAnimating() {
		return !hasEntities(AnimatingComp.class);
	}
	
}
