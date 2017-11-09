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
import squidpony.squidgrid.gui.gdx.TextCellFactory;
import squidpony.squidgrid.gui.gdx.TextFamily;
import squidpony.squidmath.Coord;
import squidpony.squidmath.GreasedRegion;

import rakaneth.wolfsden.CommandTypes;
import rakaneth.wolfsden.CreatureBuilder;
import rakaneth.wolfsden.WolfGame;
import rakaneth.wolfsden.MapBuilder;
import rakaneth.wolfsden.Swatch;
import rakaneth.wolfsden.WolfMap;
import rakaneth.wolfsden.components.ActionStack;
import rakaneth.wolfsden.components.Mapper;
import rakaneth.wolfsden.components.Position;
import rakaneth.wolfsden.components.SecondaryStats;
import rakaneth.wolfsden.components.Stats;
import rakaneth.wolfsden.systems.ActionResolverSystem;
import rakaneth.wolfsden.systems.CalcSecondariesSystem;
import rakaneth.wolfsden.systems.LevelChangeSystem;
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
	private GreasedRegion					 seen;
	private GreasedRegion					 currentlySeen;
	private GreasedRegion					 blockage;
	private MapBuilder						 mb;
	private boolean								 changedLevel;

	public static final PlayScreen instance				 = new PlayScreen();

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
		initText(invPanel.getTextCellFactory(), 1.1f, 1.1f);
		statPanel = new SquidPanel(statWidth, statHeight, slab.copy());
		statPanel.setBounds(msgPixelWidth, 0, statPixelWidth, statPixelHeight);
		initText(statPanel.getTextCellFactory(), 1.2f, 1.5f);
		ttPanel = new SquidPanel(ttWidth, ttHeight, slab.copy());
		ttPanel.setBounds(msgPixelWidth + statPixelWidth, 0, ttPixelWidth, ttPixelHeight);
		msgs = new SquidMessageBox(msgWidth, msgHeight, slab.copy());
		initText(msgs.getTextCellFactory(), 1.2f, 1.5f);
		msgs.setBounds(0, 0, msgPixelWidth, msgPixelHeight);
		addMessage("Welcome to [/][Green]Wolf's Den II![]");

		input = new SquidInput((char key, boolean alt, boolean ctrl, boolean shift) ->
		{
			switch (key) {
			case SquidInput.ESCAPE:
				WolfGame.setScreen(TitleScreen.instance);
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
			case '>':
			case '<':
				sendCmd(CommandTypes.STAIRS);
				break;
			}
		});
		stage.addActor(display);
		stage.addActor(invPanel);
		stage.addActor(msgs);
		stage.addActor(statPanel);
		stage.addActor(ttPanel);
		init();
	}

	private void initText(TextCellFactory tcf, float tweakWidth, float tweakHeight)
	{
		tcf
		.width(cellWidth)
		.height(cellHeight)
		.tweakHeight(tweakHeight * cellHeight)
		.tweakWidth(tweakWidth * cellWidth)
		.initBySize();
	}
	
	private void init()
	{
		setInput();
		buildEngine();
		buildDungeon();
		buildPlayer();
		setFOV(curMap);
	}

	private void buildEngine()
	{
		engine.addSystem(new CalcSecondariesSystem());
		engine.addSystem(new ActionResolverSystem());
		engine.addSystem(new RenderingSystem(this, display));
		engine.addSystem(new LevelChangeSystem());
		cb = new CreatureBuilder(engine);
	}

	private void buildDungeon()
	{
		mb = MapBuilder.instance;
		mb.buildAll();
		curMap = mb.maps.get("wolfDen1");
	}

	private void buildPlayer()
	{
		player = cb.buildPlayer("fighter", curMap);
		cb.build("wolf", curMap);
	}

	private void setFOV(WolfMap map)
	{
		fov = new FOV();
		Coord pos = player.getComponent(Position.class).current;
		visible = fov.calculateFOV(map.resistanceMap, pos.x, pos.y, 10);
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

	private void sendCmd(CommandTypes cmd, Object... targets)
	{
		ActionStack ply = player.getComponent(ActionStack.class);
		for (Object target : targets)
		{
			ply.cmds.push(target);
		}
		ply.cmds.push(cmd);
	}

	private void drawHUD()
	{
		Coord pos = player.getComponent(Position.class).current, cam = cam();
		border(statPanel);
		border(invPanel); 
		invPanel.put(1, 1, String.format("player loc: %d,%d", pos.x, pos.y));
		invPanel.put(1, 2, String.format("cam: %d,%d", cam.x, cam.y));
		String info = Swatch.WARNING;
		String vit = Swatch.VIT;
		String arm = Swatch.ARM;
		String XP = Swatch.XP;
		Stats stats = Mapper.stats.get(player);
		SecondaryStats secs = Mapper.secondaries.get(player);
		IColoredString<Color> wStr = ICString("[%s]Str[] %-5d[%s]Dmg[] %5s [%s]Vit[] %5d/%5d", info, stats.str, info,
																					secs.dmg, vit, 50, 50),
				wStam = ICString("[%s]Sta[] %-5d[%s]End[] %5d [%s]Arm[] %5d/%5d", info, stats.stam, info, 50, arm, 100, 100),
				wSpd = ICString("[%s]Spd[] %-5d[%s]Def[] %5d [%s] XP[] %5d/%5d", info, stats.spd, info, secs.def, XP, 500, 500),
				wSkl = ICString("[%s]Skl[] %-5d[%s]Atk[] %5s", info, stats.skl, info, secs.atk);
		statPanel.put(1, 1, wStr);
		statPanel.put(1, 2, wStam);
		statPanel.put(1, 3, wSpd);
		statPanel.put(1, 4, wSkl);
	}

	private IColoredString<Color> ICString(String template, Object... args)
	{
		String rawText = String.format(template, args);
		return GDXMarkup.instance.colorString(rawText);
	}

	private void border(SquidPanel panel)
	{
		int w = panel.getGridWidth();
		int h = panel.getGridHeight();
		char[][] borders = new char[w][h];
		borders[0][0] = Swatch.BOX_TOP_LEFT;
		borders[w - 1][0] = Swatch.BOX_TOP_RIGHT;
		borders[0][h - 1] = Swatch.BOX_BOT_LEFT;
		borders[w - 1][h - 1] = Swatch.BOX_BOT_RIGHT;
		for (int i = 1; i < w - 1; i++)
		{
			borders[i][0] = Swatch.BOX_HORZ;
			borders[i][h - 1] = Swatch.BOX_HORZ;
		}
		for (int j = 1; j < h - 1; j++)
		{
			borders[0][j] = Swatch.BOX_VERT;
			borders[w - 1][j] = Swatch.BOX_VERT;
		}
		for (int x = 1; x < w - 1; x++)
		{
			for (int y = 1; y < h - 1; y++)
			{
				borders[x][y] = ' ';
			}
		}

		panel.put(borders);
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
		if (changedLevel)
		{
			setFOV(curMap);
			changedLevel = false;
		} else
		{
			updateFOV();
		}
		stage.act();
		stage.getViewport()
				 .apply(false);
		stage.draw();
	}

	public double[][] visible()
	{
		return visible;
	}

	public void changeMap(WolfMap newMap)
	{
		curMap = newMap;
		changedLevel = true;
	}
}
