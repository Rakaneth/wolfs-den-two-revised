package rakaneth.wolfsden;

import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeLibrary;
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeLibraryManager;
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeParser;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.StreamUtils;

import rakaneth.wolfsden.components.AI;
import rakaneth.wolfsden.components.Action;
import rakaneth.wolfsden.components.Drawing;
import rakaneth.wolfsden.components.Duration;
import rakaneth.wolfsden.components.Factions;
import rakaneth.wolfsden.components.FreshCreature;
import rakaneth.wolfsden.components.Identity;
import rakaneth.wolfsden.components.Inventory;
import rakaneth.wolfsden.components.Mapper;
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
  private FactionManager                fm;

  @SuppressWarnings("unchecked")
  public CreatureBuilder()
  {
    DataConverter converter = new DataConverter(JsonWriter.OutputType.javascript);
    creatures = converter.fromJson(HashMap.class, CreatureBase.class, Gdx.files.internal(fileName));
    fm = FactionManager.instance;
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
      String bTreePath = "data/ai/" + base.ai + ".tree";
      creature.add(new AI(BehaviorTreeLibraryManager.getInstance()
                                                    .createBehaviorTree(bTreePath, creature)));
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
    String alphaID = Mapper.getID(alpha);
    fm.addReaction(alphaID, "player", -100);
    for (int w = 0; w < packSize; w++)
    {
      Entity wolf = build("wolf", map);
      fm.addToFaction(wolf, alphaID);
      Mapper.ai.get(wolf).setLeader(alphaID);
    }
  }

  // TODO: adjust for durations
  public void summon(Entity entity, String slaveID, Integer durationInTix)
  {
    Position summonerPos = Mapper.position.get(entity);
    Entity slave = build(slaveID, summonerPos.map);
    Identity id = Mapper.identity.get(entity);
    Identity sID = Mapper.identity.get(slave);
    slave.remove(AI.class);
    slave.add(new AI(BehaviorTreeLibraryManager.getInstance().createBehaviorTree("data/ai/summoned.tree", slave)));
    Position slavePos = Mapper.position.get(slave);
    slavePos.current = slavePos.map.getEmptyNear(Mapper.position.get(entity).current);
    Drawing summonerDraw = Mapper.drawing.get(entity);
    Drawing slaveDraw = Mapper.drawing.get(slave);
    fm.removeAllFactions(slave);
    fm.addToFaction(slave, id.id);
    Mapper.ai.get(slave).setLeader(entity);

    if (durationInTix != null)
    {
      String exitMsg = String.format("[%s]%s's[] summoned [%s]%s[] vanishes.", summonerDraw.color.getName(), id.name,
                                     slaveDraw.color.getName(), sID.name);
      slave.add(new Duration(durationInTix, exitMsg));
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
