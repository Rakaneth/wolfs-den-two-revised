package rakaneth.wolfsden.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import squidpony.panel.IColoredString;
import squidpony.squidgrid.Direction;
import squidpony.squidgrid.FOV;
import squidpony.squidgrid.gui.gdx.DefaultResources;
import squidpony.squidgrid.gui.gdx.GDXMarkup;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidgrid.gui.gdx.SparseLayers;
import squidpony.squidgrid.gui.gdx.SquidInput;
import squidpony.squidgrid.gui.gdx.SquidMessageBox;
import squidpony.squidgrid.gui.gdx.SquidPanel;
import squidpony.squidgrid.gui.gdx.TextFamily;
import squidpony.squidmath.Coord;
import squidpony.squidmath.GreasedRegion;
import rakaneth.wolfsden.CommandTypes;
import rakaneth.wolfsden.CreatureBuilder;
import rakaneth.wolfsden.Game;
import rakaneth.wolfsden.MapBuilder;
import rakaneth.wolfsden.WolfMap;
import rakaneth.wolfsden.components.ActionStack;
import rakaneth.wolfsden.components.Position;
import rakaneth.wolfsden.systems.ActionResolverSystem;
import rakaneth.wolfsden.systems.RenderingSystem;

public class PlayScreen extends WolfScreen
{
	private final int							 gridWidth			 = 80;
	private final int							 gridHeight			 = 32;
	private final int							 msgWidth				 = 40;
	private final int							 msgHeight			 = 8;
	private final int							 invWidth				 = 40;
	private final int							 invHeight			 = 32;
	private final int							 statWidth			 = 40;
	private final int							 statHeight			 = 8;
	private final int							 ttWidth				 = 40;
	private final int							 ttHeight				 = 8;
	private final int							 pixelWidth			 = gridWidth * cellWidth;
	private final int							 pixelHeight		 = gridHeight * cellHeight;
	private final int							 msgPixelWidth	 = msgWidth * cellWidth;
	private final int							 msgPixelHeight	 = msgHeight * cellHeight;
	private final int							 statPixelWidth	 = statWidth * cellWidth;
	private final int							 statPixelHeight = statHeight * cellHeight;
	private final int							 invPixelWidth	 = invWidth * cellWidth;
	private final int							 invPixelHeight	 = invHeight * cellHeight;
	private final int							 ttPixelWidth		 = ttWidth * cellWidth;
	private final int							 ttPixelHeight	 = ttHeight * cellHeight;
	private final int							 fullWidth			 = gridWidth + invWidth + 10;
	private final int							 fullHeight			 = gridHeight + msgHeight;
	private final int							 fullPixelWidth	 = fullWidth * cellWidth;
	private final int							 fullPixelHeight = fullHeight * cellHeight;
	private final float						 grayFloat			 = SColor.SLATE_GRAY.toFloatBits();
	private SparseLayers					 display;
	private static SquidMessageBox msgs;
	private SquidPanel						 invPanel, statPanel, ttPanel;
	private FOV										 fov;
	public final Engine						 engine					 = new Engine();
	private Entity								 player;
	private WolfMap								 curMap;
	private double[][]						 visible;
	private CreatureBuilder				 cb;
	private int										 counter				 = 1;
	GreasedRegion									 seen;
	GreasedRegion									 currentlySeen;
	GreasedRegion									 blockage;
	public static final PlayScreen instance = new PlayScreen();

	private PlayScreen()
	{
		TextFamily slab = DefaultResources.getSlabFamily();
		vport = new StretchViewport(fullPixelWidth, fullPixelHeight);
		stage = new Stage(vport, batch);
		display = new SparseLayers(gridWidth, gridHeight, cellWidth, cellHeight, slab);
		display.setBounds(0, msgPixelHeight, pixelWidth, pixelHeight);
		display.getFont()
					 .tweakHeight(1.1f * cellHeight)
					 .tweakWidth(1.1f * cellWidth)
					 .initBySize();
		display.setPosition(0, msgPixelHeight);
		invPanel = new SquidPanel(invWidth, invHeight, slab.copy());
		invPanel.setBounds(pixelWidth, msgPixelHeight, invPixelWidth, invPixelHeight);
		statPanel = new SquidPanel(statWidth, statHeight, slab.copy());
		statPanel.setBounds(msgPixelWidth, 0, statPixelWidth, statPixelHeight);
		ttPanel = new SquidPanel(ttWidth, ttHeight, slab.copy());
		ttPanel.setBounds(msgPixelWidth + statPixelWidth, 0, ttPixelWidth, ttPixelHeight);
		msgs = new SquidMessageBox(msgWidth, msgHeight, slab.copy());
		msgs.getTextCellFactory()
				.width(cellWidth)
				.height(cellHeight)
				.tweakHeight(1.5f * cellHeight)
				.tweakWidth(1.2f * cellWidth)
				.initBySize();
		msgs.setBounds(0, 0, msgPixelWidth, msgPixelHeight);

		input = new SquidInput((char key, boolean alt, boolean ctrl, boolean shift) ->
		{
			switch (key) {
			case SquidInput.ESCAPE:
				Game.setScreen(TitleScreen.instance);
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
		stage.addActor(invPanel);
		stage.addActor(msgs);
		stage.addActor(statPanel);
		stage.addActor(ttPanel);
		setInput();
		buildEngine();
		buildDungeon();
		buildPlayer();
		setFOV();
	}

	private void buildEngine()
	{
		engine.addSystem(new ActionResolverSystem());
		engine.addSystem(new RenderingSystem(this, display));
		cb = new CreatureBuilder(engine);
	}

	private void buildDungeon()
	{
		MapBuilder mb = MapBuilder.instance;
		curMap = mb.buildMap("wolfDen2");
	}

	private void buildPlayer()
	{
		player = cb.build("fighter", curMap);
		cb.build("wolf", curMap);
		System.out.println(engine.getEntities()
														 .size());
	}

	private void setFOV()
	{
		fov = new FOV();
		Coord pos = player.getComponent(Position.class).current;
		visible = fov.calculateFOV(curMap.resistanceMap, pos.x, pos.y, 10);
		blockage = new GreasedRegion(visible, 0.0);
		seen = blockage.not()
									 .copy();
		currentlySeen = seen.copy();
		blockage.fringe8way();
	}

	private void updateFOV()
	{
		Coord pos = player.getComponent(Position.class).current;
		FOV.reuseFOV(curMap.resistanceMap, visible, pos.x, pos.y, 10);
		blockage.refill(visible, 0.0);
		seen.or(currentlySeen.remake(blockage.not()));
		blockage.fringe8way();
	}

	private void sendCmd(CommandTypes cmd, Object target)
	{
		ActionStack ply = player.getComponent(ActionStack.class);
		ply.cmds.push(target);
		ply.cmds.push(cmd);
	}

	private void drawHUD()
	{
		Coord pos = player.getComponent(Position.class).current, cam = cam();
		invPanel.put(0, 0, String.format("player loc: %d,%d", pos.x, pos.y));
		invPanel.put(0, 1, String.format("cam: %d,%d", cam.x, cam.y));
		statPanel.put(0, 0, "Stats");
		ttPanel.put(0, 0, "Tooltips");
	}

	public Coord cam()
	{
		Coord pos = player.getComponent(Position.class).current;
		int left = MathUtils.clamp(pos.x - gridWidth / 2, 0, Math.max(curMap.getWidth() - gridWidth, 0));
		int top = MathUtils.clamp(pos.y - gridHeight / 2, 0, Math.max(curMap.getHeight() - gridHeight, 0));
		return Coord.get(left, top);
	}

	private void cls()
	{
		display.clear();
		invPanel.erase();
		msgs.erase();
		statPanel.erase();
		ttPanel.erase();
	}

	private void drawDungeon()
	{
		Coord cam = cam();
		char[][] decoDungeon = curMap.displayMap;

		int wx, wy;
		for (int dx = 0; dx < gridWidth; dx++)
		{
			for (int dy = 0; dy < gridHeight; dy++)
			{
				wx = dx + cam.x;
				wy = dy + cam.y;
				if (!curMap.isOOB(wx, wy))
				{
					if (curMap.isDark())
					{
						if (visible[wx][wy] > 0.0)
							display.put(dx, dy, decoDungeon[wx][wy], curMap.fgFloats[wx][wy], curMap.bgFloats[wx][wy]);
						else if (seen.contains(wx, wy))
							display.put(dx, dy, decoDungeon[wx][wy], curMap.fgFloats[wx][wy],
													SColor.lerpFloatColors(curMap.bgFloats[wx][wy], grayFloat, -0.5f));
						else
							display.put(dx, dy, ' ', SColor.TRANSPARENT, SColor.BLACK_DYE);
					} else
					{
						if (visible[wx][wy] > 0.0)
							display.put(dx, dy, decoDungeon[wx][wy], curMap.fgFloats[wx][wy], curMap.bgFloats[wx][wy]);
						else
							display.put(dx, dy, decoDungeon[wx][wy], curMap.fgFloats[wx][wy],
													SColor.lerpFloatColors(curMap.bgFloats[wx][wy], grayFloat, -0.5f));
					}
				} else
					display.put(dx, dy, ' ', SColor.TRANSPARENT, SColor.BLACK_DYE);
			}
		}
	}

	public static void addMessage(String template, Object... args)
	{
		String rawText = String.format(template, args);
		IColoredString<Color> toWrite = GDXMarkup.instance.colorString(rawText);
		msgs.appendMessage(toWrite);
	}

	@Override
	public void render()
	{
		super.render();
		cls();
		drawDungeon();
		drawHUD();
		if (input.hasNext())
			input.next();
		float dt = Gdx.graphics.getDeltaTime();
		engine.update(dt);
		updateFOV();
		// msgStage.act();
		// msgStage.getViewport()
		// .apply(false);
		// msgStage.draw();
		stage.act();
		stage.getViewport()
				 .apply(false);
		stage.draw();
	}

	public double[][] visible()
	{
		return visible;
	}
}
