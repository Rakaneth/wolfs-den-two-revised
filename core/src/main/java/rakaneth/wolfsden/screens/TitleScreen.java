package rakaneth.wolfsden.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import squidpony.squidgrid.gui.gdx.DefaultResources;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidgrid.gui.gdx.SparseLayers;
import squidpony.squidgrid.gui.gdx.SquidInput;

import rakaneth.wolfsden.Game;

public class TitleScreen extends WolfScreen {
	
	private final int gridWidth = 120;
	private final int gridHeight = 40;
	private SparseLayers display;
	
	public TitleScreen()
	{
		vport = new StretchViewport(gridWidth * cellWidth, gridHeight * cellHeight);
		vport.setScreenBounds(0, 0, gridWidth * cellWidth, gridHeight * cellHeight);
		stage = new Stage(vport, batch);
		display = new SparseLayers(gridWidth, gridHeight, cellWidth, cellHeight, 
		                           DefaultResources.getStretchableSlabFont());
		display.setPosition(0, 0);
		input = new SquidInput((char key, boolean alt, boolean ctrl, boolean shift) -> 
		{
			switch (key) {
			case 'Q':
			case 'q':
			case SquidInput.ESCAPE:
				Gdx.app.exit();
				break;
			case 'N':
			case 'n':
				Game.setScreen(new PlayScreen());
				break;
			}
		});
		stage.addActor(display);
		Gdx.input.setInputProcessor(new InputMultiplexer(stage, input));
	}
	
	@Override
	public void render() 
	{
		super.render();
		if (input.hasNext()) input.next();
		putCenter(display, "Wolf's Den II", 10, SColor.AMBER);
		putCenter(display, "by Rakaneth", 11, SColor.AMBER);
		stage.act();
		stage.draw();
	}
}
