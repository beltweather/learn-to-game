package com.jharter.game.util.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Align;

public class GraphicsUtil {
	
	private static SpriteBatch batch = new SpriteBatch();
	private static BitmapFont font = new BitmapFont();
	
	private GraphicsUtil() {}
	
	public static TextureRegion combineWithText(Texture texture, String text, int fontSize, int x, int y) {
		FrameBuffer fbo = new FrameBuffer(Format.RGBA8888, texture.getWidth(), texture.getHeight(), false);
		fbo.begin();
		
		font.setColor(Color.RED);
		
		// Set up an ortho projection matrix
        Matrix4 projMat = new Matrix4();
        projMat.setToOrtho2D(0, 0, fbo.getWidth(), fbo.getHeight());
        batch.setProjectionMatrix(projMat);
		
		batch.begin();
		batch.draw(texture, 0, 0);
		font.draw(batch, text, x, y + texture.getHeight());
		batch.end();
		
		fbo.end();
		
		TextureRegion fboTexture = new TextureRegion(fbo.getColorBufferTexture());
		fboTexture.flip(false, true);
		
		return fboTexture;
	}
	
	public static TextureRegion buildCardTexture(Texture cardBackTexture, Texture cardIconTexture, String text) {
		FrameBuffer fbo = new FrameBuffer(Format.RGBA8888, cardBackTexture.getWidth(), cardBackTexture.getHeight(), false);
		font.getData().setScale(2);
		int margin = 10;
		int textWidth = cardBackTexture.getWidth() - 2*margin;
		GlyphLayout layout = new GlyphLayout(font, text, Color.WHITE, textWidth, Align.center, true);
		
		float fontX = margin;
		float fontY = layout.height + 30;
		
		fbo.begin();
		
		Matrix4 projMat = new Matrix4();
        projMat.setToOrtho2D(0, 0, fbo.getWidth(), fbo.getHeight());
        batch.setProjectionMatrix(projMat);
		
		batch.begin();
		batch.draw(cardBackTexture, 0, 0);
		if(cardIconTexture != null) {
			batch.draw(cardIconTexture, 
					   (cardBackTexture.getWidth() - cardIconTexture.getWidth()) / 2, 
					   cardBackTexture.getHeight() - cardIconTexture.getHeight() - margin);
		}
		font.draw(batch, layout, fontX, fontY);
		batch.end();
		
		fbo.end();
		
		TextureRegion fboTexture = new TextureRegion(fbo.getColorBufferTexture());
		fboTexture.flip(false, true);
		
		return fboTexture;
	}

}
