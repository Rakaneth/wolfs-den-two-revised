package rakaneth.wolfsden.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
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
import rakaneth.wolfsden.CommandTypes;
import rakaneth.wolfsden.CreatureBuilder;
import rakaneth.wolfsden.Game;
import rakaneth.wolfsden.Swatch;
import rakaneth.wolfsden.WolfMap;
import rakaneth.wolfsden.components.Drawing;
import rakaneth.wolfsden.components.Player;
import rakaneth.wolfsden.components.ActionStack;
import rakaneth.wolfsden.components.Position;
import rakaneth.wolfsden.systems.ActionResolverSystem;
import rakaneth.wolfsden.systems.RenderingSystem;

public class PlayScreen extends WolfScreen
{
	private final int					 gridWidth			= 80;
	private final int					 gridHeight			= 32;
	private final int					 msgWidth				= 80;
	private final int					 msgHeight			= 8;
	private final int					 pixelWidth			= gridWidth * cellWidth;
	private final int					 pixelHeight		= gridHeight * cellHeight;
	private final int					 msgPixelHeight	= msgHeight * cellHeight;
	private final int					 statWidth			= 40;
	private final int					 statHeight			= 40;
	private SparseLayers			 display;
	private SquidMessageBox		 msgs;
	private SquidPanel				 statPanel;
	private DungeonGenerator	 dunGen					= new DungeonGenerator(20, 20, Game.rng);
	private FOV								 fov;
	private StretchViewport		 msgPort;
	private Stage							 msgStage;
	public static final Engine engine					= new Engine();
	private Entity						 player					= engine.createEntity();
	private WolfMap						 curMap;
	private double[][]				 visible;

	public PlayScreen()
	{
		TextCellFactory tcf = DefaultResources.getStretchableSlabFont();
		vport = new StretchViewport(120 * cellWidth, 40 * cellHeight);
		vport.setScreenBounds(0, 0, pixelWidth, pixelHeight);
		msgPort = new StretchViewport(120 * cellWidth, 40 * cellHeight);
		msgPort.setScreenBounds(0, 0, pixelWidth, msgPixelHeight);
		stage = new Stage(vport, batch);
		msgStage = new Stage(msgPort, batch);
		display = new SparseLayers(200, 200, cellWidth, cellHeight, tcf);
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
				sendCmd(CommandTypes.MOVE, Direction.UP);
				break;
			case SquidInput.UP_RIGHT_ARROW:
				sendCmd(CommandTypes.MOVE, Direction.UP_RIGHT);
				break;
			case SquidInput.RIGHT_ARROW:
				sendCmd(CommandTypes.MOVE, Direction.RIGHT);
				break;
			case SquidInput.DOWN_RIGHT_ARROW:
				sendCmd(CommandTypes.MOVE, Direction.DOWN_RIGHT);
				break;
			case SquidInput.DOWN_ARROW:
				sendCmd(CommandTypes.MOVE, Direction.DOWN);
				break;
			case SquidInput.DOWN_LEFT_ARROW:
				sendCmd(CommandTypes.MOVE, Direction.DOWN_LEFT);
				break;
			case SquidInput.LEFT_ARROW:
				sendCmd(CommandTypes.MOVE, Direction.LEFT);
				break;
			case SquidInput.UP_LEFT_ARROW:
				sendCmd(CommandTypes.MOVE, Direction.UP_LEFT);
				break;
			}
		});
		stage.addActor(display);
		msgStage.addActor(msgs);
		msgStage.addActor(statPanel);
		Gdx.input.setInputProcessor(new InputMultiplexer(stage, input));
		buildEngine();
		buildDungeon();
		buildPlayer();
		setFOV();
		msgs.appendMessage("Message Box");
		statPanel.put(0, 0, "Stats");
	}

	private void buildEngine()
	{
		engine.addSystem(new ActionResolverSystem());
		engine.addSystem(new RenderingSystem(display));
	}

	private void buildDungeon()
	{
		char[][] testDungeon = dunGen.generate();
		curMap = new WolfMap(testDungeon, "base");
	}

	private void buildPlayer()
	{
		CreatureBuilder cb = new CreatureBuilder(engine, display);
		cb.build("wolf", curMap);
		Coord start = curMap.getEmpty();
		player.add(new Position(start, curMap));
		player.add(new Drawing(display.glyph('@', SColor.LIGHT_BLUE, start.x, start.y)));
		player.add(new ActionStack());
		player.add(new Player());
		engine.addEntity(player);
	}

	private void setFOV()
	{
		fov = new FOV();
		Coord pos = player.getComponent(Position.class).current;
		visible = fov.calculateFOV(curMap.resistanceMap, pos.x, pos.y, 10);
	}

	private void updateFOV()
	{
		Coord pos = player.getComponent(Position.class).current;
		FOV.reuseFOV(curMap.resistanceMap, visible, pos.x, pos.y, 10);
	}

	private void sendCmd(CommandTypes cmd, Object target)
	{
		ActionStack ply = player.getComponent(ActionStack.class);
		ply.cmds.push(target);
		ply.cmds.push(cmd);
	}

	private void drawDungeon()
	{
		char[][] decoDungeon = curMap.displayMap;
		for (int dx = 0; dx < decoDungeon.length; dx++)
		{
			for (int dy = 0; dy < decoDungeon[dx].length; dy++)
			{
				if (visible[dx][dy] > 0.0)
				{
					display.put(dx, dy, decoDungeon[dx][dy], curMap.fgs[dx][dy], curMap.bgs[dx][dy]);
				}
			}
		}
	}

	@Override
	public void render()
	{
		super.render();
		display.clear();
		drawDungeon();
		if (input.hasNext())
			input.next();
		float dt = Gdx.graphics.getDeltaTime();
		engine.update(dt);
		updateFOV();
		TextCellFactory.Glyph gl = player.getComponent(Drawing.class).glyph;
		stage.getCamera().position.x = gl.getX();
		stage.getCamera().position.y = gl.getY();
		msgStage.getViewport()
						.apply(false);
		msgStage.draw();
		stage.act();
		stage.getViewport()
				 .apply(false);
		stage.draw();
	}
}
