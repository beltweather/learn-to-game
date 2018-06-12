package com.jharter.game.screens;

import com.jharter.game.game.GameDescription;
import com.jharter.game.stages.GameStage;
import com.jharter.game.stages.TestStageA;

public class TestStageScreen extends GameScreen {

	public TestStageScreen(GameDescription gameDescription) {
		super(gameDescription);
	}
	
	@Override
	protected GameStage create(GameDescription gameDescription) {
		GameStage testStageA = new TestStageA(gameDescription);
	 	addStage(testStageA);
	 	//addStage(new TestStageA(gameDescription));
	 	return testStageA;
	}

}