package rakaneth.wolfsden.screens;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import rakaneth.wolfsden.GameInfo;
import rakaneth.wolfsden.WolfGame;
import rakaneth.wolfsden.WolfUtils;
import rakaneth.wolfsden.components.Mapper;
import rakaneth.wolfsden.components.Stats;
import rakaneth.wolfsden.components.Vitals;
import squidpony.panel.IColoredString;
import squidpony.squidgrid.gui.gdx.DefaultResources;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidgrid.gui.gdx.SquidInput;
import squidpony.squidgrid.gui.gdx.SquidPanel;
import squidpony.squidgrid.gui.gdx.TextFamily;

public class TrainScreen extends WolfScreen
{
  private final int               gridWidth       = 120;
  private final int               gridHeight      = 40;
  private final int               fullPixelWidth  = gridWidth * cellWidth;
  private final int               fullPixelHeight = gridHeight * cellHeight;
  public static final TrainScreen instance        = new TrainScreen();
  private Entity                  player;
  private SquidPanel              display;
  private float                   strCost;
  private float                   stamCost;
  private float                   spdCost;
  private float                   sklCost;

  private TrainScreen()
  {
    TextFamily slab = DefaultResources.getSlabFamily();
    slab.width(cellWidth)
        .height(cellHeight)
        .tweakWidth(1.2f * cellWidth)
        .tweakHeight(1.5f * cellHeight)
        .initBySize();
    vport = new StretchViewport(fullPixelWidth, fullPixelHeight);
    stage = new Stage(vport, batch);
    player = PlayScreen.instance.player();
    display = new SquidPanel(gridWidth, gridHeight, slab);
    display.setPosition(0, 0);
    input = new SquidInput((char key, boolean alt, boolean ctrl, boolean shift) ->
    {
      switch (key) {
      case '1':
        incStr();
        break;
      case '2':
        incStam();
        break;
      case '3':
        incSpd();
        break;
      case '4':
        incSkl();
        break;
      case 'x':
        player.getComponent(Vitals.class).gainXP(1000);
        break;
      case SquidInput.ESCAPE:
        PlayScreen.instance.enableSystems();
        WolfGame.setScreen(PlayScreen.instance);
        GameInfo.hudDirty = true;
        break;
      default:
      }
      getCosts();
    });
    setInput();
    stage.addActor(display);
    getCosts();
  }

  public void render()
  {
    super.render();
    display.erase();
    IColoredString<Color> train = ICString("Training");
    SColor strColor = hasStrXP() ? SColor.WHITE : SColor.DARK_GRAY;
    SColor stamColor = hasStamXP() ? SColor.WHITE : SColor.DARK_GRAY;
    SColor spdColor = hasSpdXP() ? SColor.WHITE : SColor.DARK_GRAY;
    SColor sklColor = hasSklXP() ? SColor.WHITE : SColor.DARK_GRAY;
    String wStr = String.format("[1] Strength %.0f", strCost), wStam = String.format("[2] Stamina %.0f", stamCost),
        wSpd = String.format("[3] Speed %.0f", spdCost), wSkl = String.format("[4] Skill %.0f", sklCost);
    display.putBordersCaptioned(SColor.FLOAT_WHITE, train);
    display.put(1, 1, wStr, strColor);
    display.put(1, 2, wStam, stamColor);
    display.put(1, 3, wSpd, spdColor);
    display.put(1, 4, wSkl, sklColor);
    if (input.hasNext())
      input.next();
    stage.act();
    stage.getViewport()
         .apply(false);
    stage.draw();
  }

  private void getCosts()
  {
    Stats stats = Mapper.stats.get(player);
    strCost = WolfUtils.fibs(stats.str) * 100;
    stamCost = WolfUtils.fibs(stats.stam) * 100;
    sklCost = WolfUtils.fibs(stats.skl) * 100;
    spdCost = WolfUtils.fibs(stats.spd) * 100;
  }

  private boolean hasStrXP()
  {
    Vitals vit = Mapper.vitals.get(player);
    return vit.XP >= strCost;
  }

  private boolean hasStamXP()
  {
    Vitals vit = Mapper.vitals.get(player);
    return vit.XP >= stamCost;
  }

  private boolean hasSpdXP()
  {
    Vitals vit = Mapper.vitals.get(player);
    return vit.XP >= spdCost;
  }

  private boolean hasSklXP()
  {
    Vitals vit = Mapper.vitals.get(player);
    return vit.XP >= sklCost;
  }

  private void incStr()
  {
    if (hasStrXP())
    {
      Stats stats = Mapper.stats.get(player);
      Vitals vit = Mapper.vitals.get(player);
      stats.str++;
      vit.XP -= strCost;
    }
  }

  private void incStam()
  {
    if (hasStamXP())
    {
      Stats stats = Mapper.stats.get(player);
      Vitals vit = Mapper.vitals.get(player);
      stats.stam++;
      vit.XP -= stamCost;
    }
  }

  private void incSpd()
  {
    if (hasSpdXP())
    {
      Stats stats = Mapper.stats.get(player);
      Vitals vit = Mapper.vitals.get(player);
      stats.spd++;
      vit.XP -= spdCost;
    }

  }

  private void incSkl()
  {
    if (hasSklXP())
    {
      Stats stats = Mapper.stats.get(player);
      Vitals vit = Mapper.vitals.get(player);
      stats.skl++;
      vit.XP -= sklCost;
    }
  }

}
