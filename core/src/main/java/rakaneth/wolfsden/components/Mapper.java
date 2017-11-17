package rakaneth.wolfsden.components;

import java.util.Arrays;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import rakaneth.wolfsden.WolfMap;
import rakaneth.wolfsden.screens.PlayScreen;
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
  public static final ComponentMapper<AI>             AIs         = ComponentMapper.getFor(AI.class);

  public static final boolean isPlayer(Entity entity)
  {
    return player.get(entity) != null;
  }

  public static final Entity[] itemAt(Coord c, WolfMap m)
  {
    Family searchFam = Family.all(Position.class)
                             .get();
    ImmutableArray<Entity> toSearch = PlayScreen.engine.getEntitiesFor(searchFam);
    return Arrays.stream(toSearch.toArray())
                 .filter(f -> position.get(f).current.equals(c) && position.get(f).map.id.equals(m.id))
                 .toArray(Entity[]::new);
  }
}
