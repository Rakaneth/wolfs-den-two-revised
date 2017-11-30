package rakaneth.wolfsden;

import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeLibrary;
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeParser;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.StreamUtils;

import rakaneth.wolfsden.components.AI;
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
  private BehaviorTreeLibrary           library;

  @SuppressWarnings("unchecked")
  public CreatureBuilder()
  {
    DataConverter converter = new DataConverter(JsonWriter.OutputType.javascript);
    creatures = converter.fromJson(HashMap.class, CreatureBase.class, Gdx.files.internal(fileName));
    library = new BehaviorTreeLibrary();
    registerAIs();
  }

  private void registerAIs()
  {
    Reader reader = null;
    BehaviorTreeParser<Entity> btp = new BehaviorTreeParser<Entity>(BehaviorTreeParser.DEBUG_LOW);
    try
    {
      for (Map.Entry<String, CreatureBase> item : creatures.entrySet())
      {
        String aid = item.getValue().ai;
        reader = Gdx.files.internal("data/ai/" + aid + ".tree")
                          .reader();
        library.registerArchetypeTree(aid, btp.parse(reader, null));
      }
    } finally
    {
      StreamUtils.closeQuietly(reader);
    }
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
      creature.add(new AI(library.createBehaviorTree(base.ai, creature)));
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
    p.remove(AI.class);
    p.getComponent(Drawing.class).layer = 4;
    p.getComponent(Factions.class).factions.add("player");
    p.getComponent(Identity.class).id = "player";
    return p;
  }
  
  public void buildWolfPack(WolfMap map)
  {
    int packSize = WolfGame.rng.between(1, 5);
    Entity alpha = build("alpha", map);
    String alphaID = alpha.getComponent(Identity.class).id;
    FactionManager.instance.addReaction(alphaID, "player", -100);
    for (int w=0; w<packSize; w++)
    {
      Entity wolf = build("wolf", map);
      FactionManager.instance.addToFaction(wolf, alphaID);
    }
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
