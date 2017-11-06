package rakaneth.wolfsden.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import squidpony.squidgrid.Direction;
import squidpony.squidgrid.FOV;
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
import squidpony.squidgrid.mapping.DungeonUtility;
import squidpony.squidmath.Coord;
import squidpony.squidmath.GreasedRegion;
import rakaneth.wolfsden.Game;
import rakaneth.wolfsden.Swatch;
import rakaneth.wolfsden.systems.RenderingSystem;

public class PlayScreen extends WolfScreen
{
	private final int							gridWidth			 = 80;
	private final int							gridHeight		 = 32;
	private final int							msgWidth			 = 80;
	private final int							msgHeight			 = 8;
	private final int							pixelWidth		 = gridWidth * cellWidth;
	private final int							pixelHeight		 = gridHeight * cellHeight;
	private final int							msgPixelHeight = msgHeight * cellHeight;
	private final int							statWidth			 = 40;
	private final int							statHeight		 = 40;
	private SparseLayers					display;
	private SquidMessageBox				msgs;
	private SquidPanel						statPanel;
	private char[][]							testDungeon;
	private DungeonGenerator			dunGen				 = new DungeonGenerator(160, 64, Game.rng);
	private Color[][]							testColors;
	private TextCellFactory.Glyph	gl;
	private Coord									player;
	private FOV										fov;
	private double[][]						visible;
	private double[][]						resistances;
	private char[][]							decoDungeon;
	private Color[][]							testFG;
	private StretchViewport				msgPort;
	private Stage									msgStage;

	public PlayScreen()
	{
		TextCellFactory tcf = DefaultResources.getStretchableSlabFont();
		vport = new StretchViewport(120 * cellWidth, 40 * cellHeight);
		vport.setScreenBounds(0, 0, pixelWidth, pixelHeight);
		msgPort = new StretchViewport(120 * cellWidth, 40 * cellHeight);
		msgPort.setScreenBounds(0, 0, pixelWidth, msgPixelHeight);
		stage = new Stage(vport, batch);
		msgStage = new Stage(msgPort, batch);
		display = new SparseLayers(160, 64, cellWidth, cellHeight, tcf);
		display.getFont()
					 .tweakHeight(1.1f * cellHeight)
					 .tweakWidth(1.1f * cellWidth)
					 .initBySize();
		display.setPosition(0, msgPixelHeight);
		msgs = new SquidMessageBox(msgWidth, msgHeight, tcf);
		msgs.setBounds(0, 0, pixelWidth, msgPixelHeight);
		statPanel = new SquidPanel(statWidth, statHeight, tcf);
		statPanel.setBounds(pixelWidth, 0, statWidth * cellWidth, statHeight * cellHeight);

		input = new SquidInput((char key, boolean alt, boolean ctrl, boolean shift) ->
		{
			switch (key) {
			case SquidInput.ESCAPE:
				Game.setScreen(new TitleScreen());
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
		stage.addActor(display);
		msgStage.addActor(msgs);
		msgStage.addActor(statPanel);
		Gdx.input.setInputProcessor(new InputMultiplexer(stage, input));
		testDungeon = dunGen.generate();
		testColors = MapUtility.generateDefaultBGColors(testDungeon);
		testFG = MapUtility.generateDefaultColors(testDungeon);
		resistances = DungeonUtility.generateResistances(testDungeon);
		GreasedRegion gr = new GreasedRegion(testDungeon, '.');
		decoDungeon = new char[160][64];
		for (int x = 0; x < testDungeon.length; x++)
		{
			for (int y = 0; y < testDungeon[x].length; y++)
			{
				if (testDungeon[x][y] == '#')
					decoDungeon[x][y] = Swatch.CHAR_WALL;
				else
					decoDungeon[x][y] = Swatch.CHAR_FLOOR;
			}
		}
		player = gr.singleRandom(Game.rng);
		gl = display.glyph('@', SColor.BLUE.toFloatBits(), player.x, player.y);
		fov = new FOV();
		visible = fov.calculateFOV(resistances, player.x, player.y, 10);
		msgs.appendMessage("Message Box");
		statPanel.put(0, 0, "Stats");
	}

	private void drawDungeon()
	{

		for (int dx = 0; dx < decoDungeon.length; dx++)
		{
			for (int dy = 0; dy < decoDungeon[dx].length; dy++)
			{
				if (visible[dx][dy] > 0.0)
				{
					display.put(dx, dy, decoDungeon[dx][dy], testFG[dx][dy], testColors[dx][dy]);
				}
			}
		}
	}

	private void move(Direction d)
	{
		int newX = player.x + d.deltaX, newY = player.y + d.deltaY;

		if (testDungeon[newX][newY] != '#')
		{
			display.slide(gl, player.x, player.y, newX, newY, 0.1f, null);
			player = player.translate(d);
			FOV.reuseFOV(resistances, visible, player.x, player.y, 10);
		}
	}

	@Override
	public void render()
	{
		super.render();
		display.clear();
		stage.getCamera().position.x = gl.getX();
		stage.getCamera().position.y = gl.getY();
		drawDungeon();
		if (input.hasNext())
			input.next();
		msgStage.getViewport()
						.apply(false);
		msgStage.draw();
		stage.act();
		stage.getViewport()
				 .apply(false);
		stage.draw();
	}
}
