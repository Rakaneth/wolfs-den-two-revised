package rakaneth.wolfsden.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidgrid.gui.gdx.SparseLayers;
import squidpony.squidgrid.gui.gdx.SquidInput;

public abstract class WolfScreen {
	protected final SColor bgColor = SColor.DARK_SLATE_GRAY;
	protected final int cellWidth = 12;
	protected final int cellHeight = 20;
	protected Stage stage;
	protected SpriteBatch batch = new SpriteBatch();
	protected SquidInput input;
	protected StretchViewport vport;
	
	public void render() 
	{
    Gdx.gl.glClearColor(bgColor.r / 255.0f, bgColor.g / 255.0f, bgColor.b / 255.0f, 1.0f);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}
	
	protected void putCenter(SparseLayers display, String text, int y, SColor color)
	{
		int l = text.length();
		int w = display.gridWidth();
		if (l > w) return;
		int startX =  (w - l) / 2;
		display.put(startX,  y, text, color);
	}
}
