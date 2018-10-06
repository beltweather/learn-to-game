package com.jharter.game.tween.tweenstudio.demo;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.jharter.game.tween.tweenstudio.src.Editor;
import com.jharter.game.tween.tweenstudio.src.Property.Field;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com
 */
public class LibGdxTweenStudioEditor extends Editor {
	@Override
	public void initialize() {
		registerProperty(Sprite.class, SpriteTweenAccessor.POSITION_XY, "position", new Field("x", 1), new Field("y", 1));
		registerProperty(Sprite.class, SpriteTweenAccessor.ROTATION, "rotation", new Field("rotation", 5));
		registerProperty(Sprite.class, SpriteTweenAccessor.OPACITY, "opacity", new Field("opacity", 0, 1, 0.1f));
		registerProperty(Sprite.class, SpriteTweenAccessor.SCALE_XY, "scale", new Field("scaleX", 0.1f), new Field("scaleY", 0.1f));
	}
}
