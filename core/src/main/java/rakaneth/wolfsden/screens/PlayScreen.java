package rakaneth.wolfsden.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import squidpony.panel.IColoredString;
import squidpony.squidgrid.Direction;
import squidpony.squidgrid.FOV;
import squidpony.squidgrid.gui.gdx.DefaultResources;
import squidpony.squidgrid.gui.gdx.GDXMarkup;
import squidpony.squidgrid.gui.gdx.LinesPanel;
import squidpony.squidgrid.gui.gdx.SparseLayers;
import squidpony.squidgrid.gui.gdx.SquidInput;
import squidpony.squidgrid.gui.gdx.SquidMessageBox;
import squidpony.squidgrid.gui.gdx.SquidPanel;
import squidpony.squidgrid.gui.gdx.TextCellFactory;
import squidpony.squidgrid.gui.gdx.TextFamily;
import squidpony.squidgrid.mapping.DungeonGenerator;
import squidpony.squidmath.Coord;
import rakaneth.wolfsden.CommandTypes;
import rakaneth.wolfsden.CreatureBuilder;
import rakaneth.wolfsden.Game;
import rakaneth.wolfsden.WolfMap;
import rakaneth.wolfsden.components.Drawing;
import rakaneth.wolfsden.components.ActionStack;
import rakaneth.wolfsden.components.Position;
import rakaneth.wolfsden.systems.ActionResolverSystem;
import rakaneth.wolfsden.systems.RenderingSystem;

public class PlayScreen extends WolfScreen
{
	private final int							 gridWidth			 = 80;
	private final int							 gridHeight			 = 32;
	private final int							 msgWidth				 = 80;
	private final int							 msgHeight			 = 8;
	private final int							 pixelWidth			 = gridWidth * cellWidth;
	private final int							 pixelHeight		 = gridHeight * cellHeight;
	private final int							 msgPixelHeight	 = msgHeight * cellHeight;
	private final int							 statWidth			 = 40;
	private final int							 statHeight			 = 40;
	private final int							 fullWidth			 = gridWidth + statWidth;
	private final int							 fullHeight			 = gridHeight + msgHeight;
	private final int							 fullPixelWidth	 = fullWidth * cellWidth;
	private final int							 fullPixelHeight = fullHeight * cellHeight;
	private final int							 statPixelWidth	 = statWidth * cellWidth;
	private final int							 statPixelHeight = statHeight * cellHeight;
	private SparseLayers					 display;
	private static SquidMessageBox msgs;
	private SquidPanel						 statPanel;
	private DungeonGenerator			 dunGen					 = new DungeonGenerator(120, 120, Game.rng);
	private FOV										 fov;
	private Viewport							 msgPort;
	private Stage									 msgStage;
	public final Engine						 engine					 = new Engine();
	private Entity								 player;
	private WolfMap								 curMap;
	private double[][]						 visible;
	private CreatureBuilder				 cb;
	private int										 counter				 = 1;
	// private static LinesPanel<Color> lp;

	public PlayScreen()
	{
		TextCellFactory tcf = DefaultResources.getStretchableSlabFont();
		vport = new StretchViewport(fullPixelWidth, fullPixelHeight);
		vport.setScreenBounds(0, 0, pixelWidth, pixelHeight);
		msgPort = new StretchViewport(fullPixelWidth, fullPixelHeight);
		msgPort.setScreenBounds(0, 0, pixelWidth, msgPixelHeight);
		stage = new Stage(vport, batch);
		msgStage = new Stage(msgPort, batch);
		display = new SparseLayers(200, 200, cellWidth, cellHeight, tcf);
		display.setBounds(0, msgPixelHeight, pixelWidth, pixelHeight);
		display.getFont()
					 .tweakHeight(1.1f * cellHeight)
					 .tweakWidth(1.1f * cellWidth)
					 .initBySize();
		display.setPosition(0, msgPixelHeight);
		TextFamily slab = DefaultResources.getSlabFamily();
		/*
		 * slab.width(cellWidth).height(cellHeight).initBySize(); final int nl =
		 * LinesPanel.computeMaxLines(slab.font(), msgPixelHeight); lp = new
		 * LinesPanel<Color>(GDXMarkup.instance, slab, nl);
		 */
		msgs = new SquidMessageBox(msgWidth, msgHeight, slab);
		msgs.getTextCellFactory()
				.width(cellWidth)
				.height(cellHeight)
				.tweakHeight(1.5f * cellHeight)
				.tweakWidth(1.2f * cellWidth)
				.initBySize();
		msgs.setBounds(0, 0, pixelWidth, msgPixelHeight);
		statPanel = new SquidPanel(statWidth, statHeight, tcf);
		statPanel.setBounds(pixelWidth, 0, statPixelWidth, statPixelHeight);

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
			case 'T':
				addMessage("[/]Should be italic,[] should be normal [Light Blue]%d[]", counter++);
				break;
			}
		});
		stage.addActor(display);
		msgStage.addActor(msgs);
		// msgStage.addActor(lp);
		msgStage.addActor(statPanel);
		Gdx.input.setInputProcessor(new InputMultiplexer(stage, input));
		buildEngine();
		buildDungeon();
		buildPlayer();
		setFOV();
		addMessage("Messages");
		// addMessageLP("[/]This text should be italic;[] this text is not.");
		statPanel.put(0, 0, "Stats");
	}

	private void buildEngine()
	{
		engine.addSystem(new ActionResolverSystem());
		engine.addSystem(new RenderingSystem(display));
		cb = new CreatureBuilder(engine, display);
	}

	private void buildDungeon()
	{
		char[][] testDungeon = dunGen.generate();
		curMap = new WolfMap(testDungeon, "base");
	}

	private void buildPlayer()
	{
		player = cb.build("fighter", curMap);
		cb.build("wolf", curMap);
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
				if (visible[dx][dy] > 0.0 || !curMap.dark)
				{
					display.put(dx, dy, decoDungeon[dx][dy], curMap.fgs[dx][dy], curMap.bgs[dx][dy]);
				}
			}
		}
	}

	public static void addMessage(String template, Object... args)
	{
		String rawText = String.format(template, args);
		IColoredString<Color> toWrite = GDXMarkup.instance.colorString(rawText);
		msgs.appendMessage(toWrite);
	}

	public static void addMessageLP(String template, Object... args)
	{
		String rawText = String.format(template, args);
		IColoredString<Color> toWrite = GDXMarkup.instance.colorString(rawText);
		// lp.addFirst(toWrite);
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
		msgStage.getViewport()
						.apply(false);
		msgStage.draw();
		Camera cam = stage.getCamera();
		System.out.println(cam.viewportWidth);
		cam.position.x = gl.getX();
		cam.position.y = gl.getY();

		stage.act();
		stage.getViewport()
				 .apply(false);
		stage.draw();
	}
}
