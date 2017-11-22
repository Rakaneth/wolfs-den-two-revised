package rakaneth.wolfsden.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.GdxAI;
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
import rakaneth.wolfsden.FactionManager;
import rakaneth.wolfsden.GameInfo;
import rakaneth.wolfsden.ItemBuilder;
import rakaneth.wolfsden.WolfGame;
import rakaneth.wolfsden.MapBuilder;
import rakaneth.wolfsden.Swatch;
import rakaneth.wolfsden.WolfMap;
import rakaneth.wolfsden.components.AI;
import rakaneth.wolfsden.components.Armor;
import rakaneth.wolfsden.components.Identity;
import rakaneth.wolfsden.components.Mainhand;
import rakaneth.wolfsden.components.Mapper;
import rakaneth.wolfsden.components.Offhand;
import rakaneth.wolfsden.components.Position;
import rakaneth.wolfsden.components.SecondaryStats;
import rakaneth.wolfsden.components.Stats;
import rakaneth.wolfsden.components.Trinket;
import rakaneth.wolfsden.components.Vitals;
import rakaneth.wolfsden.systems.ActionResolverSystem;
import rakaneth.wolfsden.systems.AttackResolverSystem;
import rakaneth.wolfsden.systems.CalcSecondariesSystem;
import rakaneth.wolfsden.systems.CreatureSetupSystem;
import rakaneth.wolfsden.systems.EndStepSystem;
import rakaneth.wolfsden.systems.LevelChangeSystem;
import rakaneth.wolfsden.systems.RenderingSystem;
import rakaneth.wolfsden.systems.VisionSystem;

public class PlayScreen extends WolfScreen
{
  private final int              gridWidth       = 80;
  private final int              gridHeight      = 32;
  private final int              msgWidth        = 40;
  private final int              msgHeight       = 8;
  private final int              invWidth        = 40;
  private final int              invHeight       = 16;
  private final int              ablWidth        = 40;
  private final int              ablHeight       = 16;
  private final int              statWidth       = 40;
  private final int              statHeight      = 8;
  private final int              ttWidth         = 40;
  private final int              ttHeight        = 8;
  private final int              pixelWidth      = gridWidth * cellWidth;
  private final int              pixelHeight     = gridHeight * cellHeight;
  private final int              msgPixelWidth   = msgWidth * cellWidth;
  private final int              msgPixelHeight  = msgHeight * cellHeight;
  private final int              statPixelWidth  = statWidth * cellWidth;
  private final int              statPixelHeight = statHeight * cellHeight;
  private final int              invPixelWidth   = invWidth * cellWidth;
  private final int              invPixelHeight  = invHeight * cellHeight;
  private final int              ttPixelWidth    = ttWidth * cellWidth;
  private final int              ttPixelHeight   = ttHeight * cellHeight;
  private final int              ablPixelWidth   = ablWidth * cellWidth;
  private final int              ablPixelHeight  = ablHeight * cellHeight;
  private final int              fullWidth       = gridWidth + invWidth + 10;
  private final int              fullHeight      = gridHeight + msgHeight;
  private final int              fullPixelWidth  = fullWidth * cellWidth;
  private final int              fullPixelHeight = fullHeight * cellHeight;
  private final float            grayFloat       = SColor.SLATE_GRAY.toFloatBits();
  private SparseLayers           display;
  private static SquidMessageBox msgs;
  private SquidPanel             invPanel, statPanel, ttPanel, ablPanel;
  private FOV                    fov;
  public static final Engine     engine          = new Engine();
  private Entity                 player;
  private WolfMap                curMap;
  private double[][]             visible;
  public static CreatureBuilder  cb              = new CreatureBuilder();
  private GreasedRegion          seen;
  private GreasedRegion          currentlySeen;
  private GreasedRegion          blockage;
  public static MapBuilder       mb              = MapBuilder.instance;;
  private boolean                changedLevel;
  public static ItemBuilder      ib              = new ItemBuilder();

  public static final PlayScreen instance        = new PlayScreen();

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
    invPanel.setBounds(pixelWidth, msgPixelHeight + ablPixelHeight, invPixelWidth, invPixelHeight);
    initText(invPanel.getTextCellFactory(), 1.2f, 1.5f);
    ablPanel = new SquidPanel(ablWidth, ablHeight, slab.copy());
    ablPanel.setBounds(pixelWidth, msgPixelHeight, ablPixelWidth, ablPixelHeight);
    initText(ablPanel.getTextCellFactory(), 1.2f, 1.5f);
    statPanel = new SquidPanel(statWidth, statHeight, slab.copy());
    statPanel.setBounds(msgPixelWidth, 0, statPixelWidth, statPixelHeight);
    initText(statPanel.getTextCellFactory(), 1.2f, 1.5f);
    ttPanel = new SquidPanel(ttWidth, ttHeight, slab.copy());
    ttPanel.setBounds(msgPixelWidth + statPixelWidth, 0, ttPixelWidth, ttPixelHeight);
    initText(ttPanel.getTextCellFactory(), 1.2f, 1.5f);
    msgs = new SquidMessageBox(msgWidth, msgHeight, slab.copy());
    initText(msgs.getTextCellFactory(), 1.2f, 1.5f);
    msgs.setBounds(0, 0, msgPixelWidth, msgPixelHeight);
    addMessage("Welcome to [/][Green]Wolf's Den II![]");

    input = new SquidInput((char key, boolean alt, boolean ctrl, boolean shift) ->
    {
      CommandTypes cmd = CommandTypes.MOVE;
      Direction d = Direction.NONE;
      switch (key) {
      case SquidInput.ESCAPE:
        WolfGame.setScreen(TitleScreen.instance);
        break;
      case SquidInput.UP_ARROW:
        d = Direction.UP;
        break;
      case SquidInput.UP_RIGHT_ARROW:
        d = Direction.UP_RIGHT;
        break;
      case SquidInput.RIGHT_ARROW:
        d = Direction.RIGHT;
        break;
      case SquidInput.DOWN_RIGHT_ARROW:
        d = Direction.DOWN_RIGHT;
        break;
      case SquidInput.DOWN_ARROW:
        d = Direction.DOWN;
        break;
      case SquidInput.DOWN_LEFT_ARROW:
        d = Direction.DOWN_LEFT;
        break;
      case SquidInput.LEFT_ARROW:
        d = Direction.LEFT;
        break;
      case SquidInput.UP_LEFT_ARROW:
        d = Direction.UP_LEFT;
        break;
      case SquidInput.CENTER_ARROW:
        cmd = CommandTypes.WAIT;
        break;
      case '>':
      case '<':
        cmd = CommandTypes.STAIRS;
        break;
      }
      
      if (cmd == CommandTypes.STAIRS || cmd == CommandTypes.WAIT)
        sendCmd(cmd);
      else
      {
        Position pos = player.getComponent(Position.class);
        Coord toMove = pos.current.translate(d);
        Entity creature = Mapper.creatureAt(toMove, pos.map);
        if (creature != null)
          sendCmd(CommandTypes.INTERACT, creature);
        else
          sendCmd(cmd, d);
      }
    });
    stage.addActor(display);
    stage.addActor(invPanel);
    stage.addActor(msgs);
    stage.addActor(statPanel);
    stage.addActor(ttPanel);
    stage.addActor(ablPanel);
    init();
  }

  private void initText(TextCellFactory tcf, float tweakWidth, float tweakHeight)
  {
    tcf.width(cellWidth)
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
    engine.addSystem(new CreatureSetupSystem());
    engine.addSystem(new VisionSystem());
    engine.addSystem(new ActionResolverSystem());
    engine.addSystem(new AttackResolverSystem());
    engine.addSystem(new RenderingSystem(this, display));
    engine.addSystem(new LevelChangeSystem());
    engine.addSystem(new EndStepSystem());
  }

  private void buildDungeon()
  {
    mb.buildAll();
    curMap = mb.maps.get("wolfDen1");
    int numItems = WolfGame.rng.between(1, 21);
    for (int i = 0; i < numItems; i++)
    {
      ib.seedRandom(curMap);
    }
  }

  private void buildPlayer()
  {
    player = cb.buildPlayer("fighter", curMap, "Palmyra");
    cb.build("wolf", curMap);
    cb.build("lostMan", curMap);
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
    AI ply = player.getComponent(AI.class);
    for (Object target : targets)
    {
      ply.actionStack.push(target);
    }
    ply.actionStack.push(cmd);
  }

  private void drawHUD()
  {
    float white = SColor.FLOAT_WHITE;
    statPanel.putBorders(white, "Stats");
    invPanel.putBorders(white, "Inventory and Equipment");
    ttPanel.putBorders(white, "Information");
    ablPanel.putBorders(white, "Abilities");
    msgs.putBorders(white, "Messages");
    String info = Swatch.INFO;
    String vit = Swatch.VIT;
    String arm = Swatch.ARM;
    String XP = Swatch.XP;
    String warning = Swatch.WARNING;
    Stats stats = Mapper.stats.get(player);
    SecondaryStats secs = Mapper.secondaries.get(player);
    Identity id = Mapper.identity.get(player);
    Position pos = Mapper.position.get(player);
    Armor armor = Mapper.armors.get(player);
    Trinket trink = Mapper.trinkets.get(player);
    Mainhand mh = Mapper.mainhands.get(player);
    Offhand oh = Mapper.offhands.get(player);
    Vitals vitals = Mapper.vitals.get(player);
    String eqTemplate = "%8s: %s";
    IColoredString<Color> wStr = ICString("[%s]Str[]%2d [%s]Dmg[] %6s [%s]Vit[] %5d/%5d", info, stats.str, info,
                                          secs.dmg, vit, vitals.vit, vitals.maxVit),
        wStam = ICString("[%s]Sta[]%2d [%s]End[]%3d/%3d [%s]Arm[] %5d/%5d", info, stats.stam, info, vitals.end,
                         vitals.maxEnd, arm, 100, 100),
        wSpd = ICString("[%s]Spd[]%2d [%s]Def[] %6d [%s] XP[] %5.0f/%5.0f", info, stats.spd, info, secs.def, XP, vitals.XP,
                        vitals.totXP),
        wSkl = ICString("[%s]Skl[]%2d [%s]Atk[] %6s [%s]Mov[] %2d [%s]Dly[] %2d", info, stats.skl, info, secs.atk, info,
                        secs.moveDelay, info, secs.atkDelay),
        wID = ICString("[%s]%s[] - [%s]%s[]", warning, id.name, info, id.desc),
        wLoc = ICString("[%s]Location:[] %s %s", info, pos.map.name(), pos.current),
        wMH = ICString(eqTemplate, "Mainhand", mh.name), wOH = ICString(eqTemplate, "Offhand", oh.name),
        wArm = ICString(eqTemplate, "Armor", armor.name), wTrink = ICString(eqTemplate, "Trinket", trink.name);

    statPanel.put(1, 1, wID);
    statPanel.put(1, 2, wLoc);
    statPanel.put(1, 3, wStr);
    statPanel.put(1, 4, wStam);
    statPanel.put(1, 5, wSpd);
    statPanel.put(1, 6, wSkl);

    invPanel.put(1, 1, wMH);
    invPanel.put(1, 2, wOH);
    invPanel.put(1, 3, wArm);
    invPanel.put(1, 4, wTrink);
  }

  private IColoredString<Color> ICString(String template, Object... args)
  {
    String rawText = String.format(template, args);
    return GDXMarkup.instance.colorString(rawText);
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
    GdxAI.getTimepiece().update(dt);
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

  public String map()
  {
    return curMap.id;
  }
}
