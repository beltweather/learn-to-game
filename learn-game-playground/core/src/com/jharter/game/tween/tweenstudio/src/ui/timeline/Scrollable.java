package com.jharter.game.tween.tweenstudio.src.ui.timeline;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
interface Scrollable {
	public int getViewLength();
	public int getLength();
	public int getOffset();
	public void setOffset(int offset);
}
