package rakaneth.wolfsden.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import squidpony.squidgrid.Direction;
import squidpony.squidgrid.gui.gdx.DefaultResources;
import squidpony.squidgrid.gui.gdx.GDXMarkup;
import squidpony.squidgrid.gui.gdx.MapUtility;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidgrid.gui.gdx.SparseLayers;
import squidpony.squidgrid.gui.gdx.SquidInput;
import squidpony.squidgrid.gui.gdx.SquidMessageBox;
import squidpony.squidgrid.gui.gdx.SquidPanel;
import squidpony.squidgrid.gui.gdx.TextCellFactory;
import squidpony.squidgrid.mapping.DungeonGenerator;
import squidpony.squidmath.Coord;
import squidpony.squidmath.GreasedRegion;
import rakaneth.wolfsden.Game;
import rakaneth.wolfsden.systems.RenderingSystem;

public class PlayScreen extends WolfScreen
{
	private final int							gridWidth		= 80;
	private final int							gridHeight	= 32;
	private final int							msgWidth		= 80;
	private final int							msgHeight		= 8;
	private final int							pixelWidth	= 120 * cellWidth;
	private final int							pixelHeight	= 40 * cellHeight;
	private final int							statWidth		= 40;
	private final int							statHeight	= 40;
	private SparseLayers					display;
	private SquidMessageBox				msgs;
	private SquidPanel						statPanel;
	private char[][]							testDungeon;
	private DungeonGenerator			dunGen			= new DungeonGenerator(80, 32, Game.rng);
	private Color[][]							testColors;
	private TextCellFactory.Glyph	gl;
	private Coord									player;

	public PlayScreen(WolfMap map)
	{
		vport = new StretchViewport(pixelWidth, pixelHeight);
		vport.setScreenBounds(0, 0, pixelWidth, pixelHeight);
		stage = new Stage(vport, batch);
		display = new SparseLayers(gridWidth, gridHeight, cellWidth, cellHeight, DefaultResources.getStretchableSlabFont());
		input = new SquidInput((char key, boolean alt, boolean ctrl, boolean shift) ->
		{

			switch (key) {
			case SquidInput.ESCAPE:
				Game.engine.getSystem(RenderingSystem.class).setScreen(new TitleScreen());
				break;
			case SquidInput.UP_ARROW:
				move(Direction.UP);
				break;
			case SquidInput.UP_RIGHT_ARROW:
				move(Direction.UP_RIGHT);
				break;
			case SquidInput.RIGHT_ARROW:
				move(Direction.RIGHT);
				break;
			case SquidInput.DOWN_RIGHT_ARROW:
				move(Direction.DOWN_RIGHT);
				break;
			case SquidInput.DOWN_ARROW:
				move(Direction.DOWN);
				break;
			case SquidInput.DOWN_LEFT_ARROW:
				move(Direction.DOWN_LEFT);
				break;
			case SquidInput.LEFT_ARROW:
				move(Direction.LEFT);
				break;
			case SquidInput.UP_LEFT_ARROW:
				move(Direction.UP_LEFT);
				break;
			}
		});
		TextCellFactory tcf = DefaultResources.getStretchableWideSlabFont();
		tcf.width(cellWidth)
			 .height(cellHeight)
			 .tweakWidth(1.1f * cellWidth)
			 .tweakHeight(1.1f * cellHeight)
			 .initBySize();

		msgs = new SquidMessageBox(msgWidth, msgHeight, tcf);

		display.setBounds(0, msgHeight * cellHeight, gridWidth * cellWidth, gridHeight * cellHeight);
		msgs.setBounds(0, 0, msgWidth * cellWidth, msgHeight * cellHeight);
		statPanel = new SquidPanel(statWidth, statHeight, tcf.copy());
		statPanel.setPosition(960, 0);
		stage.addActor(display);
		stage.addActor(msgs);
		stage.addActor(statPanel);
		Gdx.input.setInputProcessor(new InputMultiplexer(stage, input));
		msgs.appendMessage(GDXMarkup.instance.colorString("[Green]Welcome[] to Wolf's Den II!"));
		msgs.appendMessage(GDXMarkup.instance.colorString("Press [Yellow][[Esc][] to return to the title screen."));
		testDungeon = dunGen.generate();
		testColors = MapUtility.generateDefaultBGColors(testDungeon);
		GreasedRegion gr = new GreasedRegion(testDungeon, '.');
		player = gr.singleRandom(Game.rng);
		gl = display.glyph('@', SColor.BLUE.toFloatBits(), player.x, player.y);
	}

	private void drawDungeon()
	{
		char[][] decoDungeon = new char[gridWidth][gridHeight];
		for (int x = 0; x < testDungeon.length; x++)
		{
			for (int y = 0; y < testDungeon[x].length; y++)
			{
				if (testDungeon[x][y] == '#')
					decoDungeon[x][y] = (char) 0x2592;
				else
					decoDungeon[x][y] = (char) 0x00A0;
			}
		}
		display.put(decoDungeon, testColors);
	}

	private void move(Direction d)
	{
		int newX = player.x + d.deltaX, newY = player.y + d.deltaY;

		if (testDungeon[newX][newY] != '#')
		{
			display.slide(gl, player.x, player.y, newX, newY, 0.1f, null);
			player = player.translate(d);
		}
	}

	@Override
	public void render()
	{
		super.render();
		if (input.hasNext())
			input.next();
		drawDungeon();
		statPanel.put(0, 0, "Test String");
		stage.draw();
		stage.act();
	}
}
