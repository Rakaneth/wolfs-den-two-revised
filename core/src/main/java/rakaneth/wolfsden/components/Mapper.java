package rakaneth.wolfsden.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeLibraryManager;

import rakaneth.wolfsden.FactionManager;
import rakaneth.wolfsden.GameInfo;
import rakaneth.wolfsden.ItemBuilder.ItemBase.ItemType;
import rakaneth.wolfsden.WolfMap;
import rakaneth.wolfsden.WolfUtils;
import rakaneth.wolfsden.screens.PlayScreen;
import squidpony.squidmath.Coord;

public class Mapper implements EntityListener
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
  public static final ComponentMapper<AI>             ai          = ComponentMapper.getFor(AI.class);
  public static final ComponentMapper<Duration>       durations   = ComponentMapper.getFor(Duration.class);
  public static final Mapper                          instance    = new Mapper();

  private Mapper()
  {
  }

  public static final boolean isPlayer(Entity entity)
  {
    return player.get(entity) != null;
  }

  public static final Entity creatureAt(Coord c, WolfMap map)
  {
    Optional<Map.Entry<Entity, Position>> result = GameInfo.atlas.entrySet()
                                                                 .stream()
                                                                 .filter(f -> f.getValue().current.equals(c)
                                                                     && f.getValue().map.id.equals(map.id))
                                                                 .findFirst();

    if (result.isPresent())
    {
      Entity potential = result.get()
                               .getKey();
      if (isCreature(potential))
        return potential;
      else
        return null;
    } else
      return null;
  }

  public static boolean isCreature(Entity entity)
  {
    return GameInfo.bestiary.containsKey(getID(entity));
  }

  public static boolean isItem(Entity entity)
  {
    return GameInfo.catalog.containsKey(getID(entity));
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

  public static final boolean canSee(Entity beholder, Entity subject)
  {
    Vision vis = vision.get(beholder);
    Position sPos = position.get(subject);
    return sameLevel(beholder, subject) && vis.grVisible.contains(sPos.current);
  }

  @Override
  public void entityAdded(Entity entity)
  {
  }

  @Override
  public void entityRemoved(Entity entity)
  {
    Identity id = identity.get(entity);
    Family AIs = Family.all(AI.class)
                       .get();

    for (Entity orphan : PlayScreen.engine.getEntitiesFor(AIs))
    {
      AI eAI = ai.get(orphan);
      if (eAI.creatureTarget() == entity)
      {
        WolfUtils.log("Engine", "Entity %s removed from %s's target", getID(entity), getID(orphan));
        eAI.clearTarget();
      }
    }
    GameInfo.atlas.remove(entity);
    GameInfo.bestiary.remove(id.id);
    GameInfo.catalog.remove(id.id);
  }

  public static final List<Entity> visibleEnemiesOf(Entity entity)
  {
    List<Entity> target = new ArrayList<>();
    GameInfo.bestiary.values()
                     .stream()
                     .filter(f -> FactionManager.instance.isEnemy(entity, f))
                     .filter(g -> canSee(entity, g))
                     .filter(h -> vitals.get(h).alive)
                     .forEach(target::add);

    return target;
  }

  public static final List<Entity> visibleThings(Entity entity)
  {
    return GameInfo.catalog.values()
                           .stream()
                           .filter(f -> canSee(entity, f))
                           .collect(Collectors.toList());
  }

  public static final List<Entity> visibleFood(Entity entity)
  {
    return visibleThings(entity).stream()
                                .filter(f -> Mapper.consumables.get(f) != null)
                                .filter(g -> Mapper.consumables.get(g).type == ItemType.FOOD)
                                .collect(Collectors.toList());
  }

  public static final boolean isOn(Entity standing, Entity on)
  {
    Position standPos = position.get(standing);
    Position onPos = position.get(on);
    return sameLevel(standing, on) && (standPos.current.equals(onPos.current));
  }

  public static List<Entity> thingsUnder(Entity entity)
  {
    return GameInfo.catalog.values()
                           .stream()
                           .filter(f -> isOn(entity, f))
                           .collect(Collectors.toList());
  }

  public static final String getID(Entity entity)
  {
    return identity.get(entity).id;
  }
}
