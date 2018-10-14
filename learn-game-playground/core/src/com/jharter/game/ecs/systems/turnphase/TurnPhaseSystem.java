package com.jharter.game.ecs.systems.turnphase;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.jharter.game.ecs.components.Components.AnimatingComp;
import com.jharter.game.ecs.components.Components.CursorComp;
import com.jharter.game.ecs.components.Components.NextTurnPhaseComp;
import com.jharter.game.ecs.components.Components.TurnPhaseTag;
import com.jharter.game.ecs.components.Components.TurnTimerComp;
import com.jharter.game.ecs.components.subcomponents.TurnTimer;
import com.jharter.game.ecs.systems.boilerplate.FirstSystem;

public abstract class TurnPhaseSystem extends FirstSystem {

	private Class<? extends Component> phaseClass;
	private Class<? extends Component> nextPhaseClass;
	private Class<? extends Component> alternativeNextPhaseClass;
	private boolean phaseStarted = false;
	private boolean phaseShouldEnd = false;
	
	public TurnPhaseSystem(Class<? extends Component> phaseClass, Class<? extends Component> nextPhaseClass) {
		super(Family.all(TurnPhaseTag.class, phaseClass).exclude(NextTurnPhaseComp.class).get());
		add(CursorComp.class);
		add(TurnTimerComp.class);
		add(TurnPhaseTag.class);
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
		Comp.getFor(phaseClass).remove(turnPhase);
		
		NextTurnPhaseComp n = Comp.NextTurnPhaseComp.add(turnPhase);
		if(alternativeNextPhaseClass != null) {
			n.next = alternativeNextPhaseClass;
			alternativeNextPhaseClass = null;
		} else {
			n.next = nextPhaseClass;
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
		return entity(TurnPhaseTag.class);
	}
	
	protected CursorComp getCursorComp() {
		return comp(CursorComp.class);
	}
	
	protected TurnTimer getTurnTimer() {
		return comp(TurnTimerComp.class).turnTimer;
	}
	
	protected void resetCursor() {
		Entity cursor = entity(CursorComp.class);
		if(cursor == null) {
			return;
		}
		Comp.MultiSpriteComp.remove(cursor);
		Comp.CursorComp.get(cursor).reset();
	}
	
	protected void enableCursor() {
		Entity cursor = entity(CursorComp.class);
		if(cursor == null) {
			return;
		}
		Comp.DisabledTag.remove(cursor);
		Comp.InputComp.get(cursor).input.reset();
	}
	
	protected void disableCursor() {
		Entity cursor = entity(CursorComp.class);
		if(cursor == null) {
			return;
		}
		Comp.DisabledTag.add(cursor);
		Comp.InputComp.get(cursor).input.reset();
	}
	
	protected boolean isDoneAnimating() {
		return !hasEntities(AnimatingComp.class);
	}
	
}
