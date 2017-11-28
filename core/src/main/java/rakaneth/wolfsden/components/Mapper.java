package rakaneth.wolfsden.components;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

import rakaneth.wolfsden.WolfMap;
import squidpony.squidmath.Coord;

public class Mapper
{
  public static final ComponentMapper<Player>         player      = ComponentMapper.getFor(Player.class);
  public static final ComponentMapper<Drawing>        drawing     = ComponentMapper.getFor(Drawing.class);
  public static final ComponentMapper<Identity>       identity    = ComponentMapper.getFor(Identity.class);
  public static final ComponentMapper<Position>       position    = ComponentMapper.getFor(Position.class);
  public static final ComponentMapper<Stats>          stats       = ComponentMapper.getFor(Stats.class);
  public static final ComponentMapper<ChangeLevel>    changeLvl   = ComponentMapper.getFor(ChangeLevel.class);
  public static final ComponentMapper<SecondaryStats> secondaries = ComponentMapper.getFor(SecondaryStats.class);
  public static final ComponentMapper<Armor>          armors      = ComponentMapper.getFor(Armor.class);
  public static final ComponentMapper<Mainhand>       mainhands   = ComponentMapper.getFor(Mainhand.class);
  public static final ComponentMapper<Offhand>        offhands    = ComponentMapper.getFor(Offhand.class);
  public static final ComponentMapper<Trinket>        trinkets    = ComponentMapper.getFor(Trinket.class);
  public static final ComponentMapper<Vitals>         vitals      = ComponentMapper.getFor(Vitals.class);
  public static final ComponentMapper<Consumable>     consumables = ComponentMapper.getFor(Consumable.class);
  public static final ComponentMapper<Factions>       factions    = ComponentMapper.getFor(Factions.class);
  public static final ComponentMapper<Action>         actions     = ComponentMapper.getFor(Action.class);
  public static final ComponentMapper<Attack>         attackers   = ComponentMapper.getFor(Attack.class);
  public static final ComponentMapper<Vision>         vision      = ComponentMapper.getFor(Vision.class);
  public static final HashMap<Entity, Position>       atlas       = new HashMap<>();

  public static final boolean isPlayer(Entity entity)
  {
    return player.get(entity) != null;
  }

  public static final Entity creatureAt(Coord c, WolfMap map)
  {
    Optional<Map.Entry<Entity, Position>> result = atlas.entrySet()
                                                        .stream()
                                                        .filter(f -> f.getValue().current.equals(c)
                                                            && f.getValue().map.id.equals(map.id))
                                                        .findFirst();

    if (result.isPresent())
      return result.get()
                   .getKey();
    else
      return null;
  }

  public static final boolean sameLevel(Entity e1, Entity e2)
  {
    Position p1 = position.get(e1);
    Position p2 = position.get(e2);
    return p1.map.id.equals(p2.map.id);
  }

  public static final boolean sameLevel(Position p, Entity e)
  {
    Position ePos = position.get(e);
    return p.map.id.equals(ePos.map.id);
  }
}
