package com.jharter.game.ashley.components.subcomponents;

public class TurnTimer {

	public float accumulator = 0;
	public float maxTurnTimeSec = 0;
	public boolean play = true;
	
	public TurnTimer() {}
	
	public void stop() {
		play = false;
		accumulator = 0;
	}
	
	public void start() {
		play = true;
		accumulator = 0;
	}
	
	public boolean isStopped() {
		return !play;
	}
	
	public boolean isOvertime() {
		return accumulator > maxTurnTimeSec;
	}
	
	public void increment(float deltaTime) {
		accumulator += deltaTime;
	}
	
	public void reset() {
		accumulator = 0;
		maxTurnTimeSec = 0;
		play = true;
	}
	
}
