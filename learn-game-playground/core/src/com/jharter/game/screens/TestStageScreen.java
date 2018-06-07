package com.jharter.game.screens;

import com.jharter.game.stages.GameStage;
import com.jharter.game.stages.TestStageA;

public class TestStageScreen extends StageScreen {

	@Override
	protected GameStage create() {
		GameStage testStageA = new TestStageA();
	 	addStage(testStageA);
	 	addStage(new TestStageA());
	 	return testStageA;
	}

}