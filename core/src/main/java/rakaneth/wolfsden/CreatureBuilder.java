package rakaneth.wolfsden;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.utils.JsonWriter;

import rakaneth.wolfsden.components.Action;
import rakaneth.wolfsden.components.Drawing;
import rakaneth.wolfsden.components.Factions;
import rakaneth.wolfsden.components.FreshCreature;
import rakaneth.wolfsden.components.Identity;
import rakaneth.wolfsden.components.Inventory;
import rakaneth.wolfsden.components.Player;
import rakaneth.wolfsden.components.Position;
import rakaneth.wolfsden.components.SecondaryStats;
import rakaneth.wolfsden.components.Stats;
import rakaneth.wolfsden.components.Vision;
import rakaneth.wolfsden.components.Vitals;
import rakaneth.wolfsden.screens.PlayScreen;
import squidpony.DataConverter;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidmath.Coord;

public class CreatureBuilder
{
  private static final String           fileName = "data/creatures.js";
  private HashMap<String, CreatureBase> creatures;
  private static int                    counter  = 1;

  @SuppressWarnings("unchecked")
  public CreatureBuilder()
  {
    DataConverter converter = new DataConverter(JsonWriter.OutputType.javascript);
    creatures = converter.fromJson(HashMap.class, CreatureBase.class, Gdx.files.internal(fileName));
  }

  public Entity build(String id, WolfMap map, String name)
  {
    CreatureBase base = creatures.get(id);
    String IDid = String.format("%s-%d", id, counter++);
    SColor color = (SColor) Colors.get(base.color);
    Entity creature = new Entity();
    Coord pos = map.getEmpty();
    String nm = WolfUtils.ifNull(name, base.name);

    if (base.factions == null)
      base.factions = new ArrayList<>();

    base.factions.add(IDid);
    creature.add(new Position(pos, map));
    creature.add(new Drawing(base.glyph, color));
    creature.add(new Stats(base.str, base.stam, base.spd, base.skl));
    creature.add(new Identity(nm, IDid, base.desc));
    creature.add(new SecondaryStats());
    creature.add(new Vitals());
    creature.add(new FreshCreature());
    creature.add(new Inventory());
    creature.add(new Factions(base.factions));
    creature.add(new Vision(base.vision));
    creature.add(new Action());

    if (base.mh != null)
      PlayScreen.ib.equip(creature, base.mh);
    else
      PlayScreen.ib.equip(creature, "rightHand");

    if (base.oh != null)
      PlayScreen.ib.equip(creature, base.oh);
    else
      PlayScreen.ib.equip(creature, "leftHand");

    if (base.armor != null)
      PlayScreen.ib.equip(creature, base.armor);
    else
      PlayScreen.ib.equip(creature, "naked");

    if (base.trinket != null)
      PlayScreen.ib.equip(creature, base.trinket);
    else
      PlayScreen.ib.equip(creature, "unadorned");

    if (base.ai != null)
    {
      // TODO: wire up AIs
    }

    PlayScreen.engine.addEntity(creature);
    return creature;
  }

  public Entity build(String id, WolfMap map)
  {
    return build(id, map, null);
  }

  public Entity buildPlayer(String id, WolfMap map, String name)
  {
    Entity p = build(id, map, name);
    p.add(new Player());
    p.getComponent(Drawing.class).layer = 4;
    p.getComponent(Factions.class).factions.add("player");
    return p;
  }

  private static class CreatureBase
  {
    public String            name;
    public int               str;
    public int               stam;
    public int               spd;
    public int               skl;
    public char              glyph;
    public String            color;
    public String            desc;
    public String            mh;
    public String            oh;
    public String            armor;
    public String            trinket;
    public ArrayList<String> factions;
    public String            ai;
    public double            vision;
  }
}
