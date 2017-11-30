package rakaneth.wolfsden;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonWriter;

import rakaneth.wolfsden.components.Factions;
import rakaneth.wolfsden.components.Mapper;
import squidpony.DataConverter;
import squidpony.squidmath.Coord;

public class FactionManager
{
  private Map<String, Map<String, Float>> factions;
  public static final FactionManager      instance = new FactionManager();
  private static final String             fileName = "data/factions.js";

  @SuppressWarnings("unchecked")
  private FactionManager()
  {
    DataConverter converter = new DataConverter(JsonWriter.OutputType.javascript);
    factions = converter.fromJson(HashMap.class, HashMap.class, Gdx.files.internal(fileName));
  }

  public Map<String, Float> getReactions(String id)
  {
    return factions.get(id);
  }

  public void addFaction(String id)
  {
    if (factions.get(id) == null)
      factions.put(id, new HashMap<String, Float>());
  }

  public void removeFaction(String id)
  {
    factions.remove(id);
  }

  public void addReaction(String faction, String target, float reaction)
  {
    addFaction(faction);
    factions.get(faction)
            .put(target, reaction);
  }

  public void removeReaction(String faction, String target)
  {
    Map<String, Float> reactTable = getReactions(faction);

    if (reactTable == null)
      return;

    reactTable.remove(target);
  }

  public void changeReaction(String faction, String target, float amt)
  {
    addFaction(faction);
    Map<String, Float> reactTable = getReactions(faction);

    Float current = reactTable.get(target);

    if (current == null)
      addReaction(faction, target, amt);
    else
    {
      float total = current + amt;
      reactTable.replace(target, total);
    }
  }

  public float getReaction(String faction, String target)
  {
    Map<String, Float> reactTable = getReactions(faction);
    if (reactTable == null)
      return 0;

    Float current = reactTable.get(target);

    if (current == null)
      return 0;

    return current;
  }

  public int getReaction(Entity e1, Entity e2)
  {
    Factions fac1 = Mapper.factions.get(e1);
    Factions fac2 = Mapper.factions.get(e2);

    if (fac1 == null || fac2 == null)
      return 0;

    int acc = 0;
    for (String f : fac1.factions)
    {
      for (String c : fac2.factions)
      {
        acc += getReaction(f, c);
      }
    }

    return acc;
  }

  public List<Entity> allInFaction(String faction)
  {
    return GameInfo.bestiary.values()
                            .stream()
                            .filter(f -> Mapper.factions.get(f).factions.contains(faction))
                            .collect(Collectors.toList());
  }

  public void addToFaction(Entity supplicant, String faction)
  {
    Mapper.factions.get(supplicant).factions.add(faction);
  }

  public void removeFromFaction(Entity apostate, String faction)
  {
    Mapper.factions.get(apostate).factions.remove(faction);
  }

  public boolean isEnemy(Entity e1, Entity e2)
  {
    return getReaction(e1, e2) < 0;
  }

  public boolean isAlly(Entity e1, Entity e2)
  {
    return getReaction(e1, e2) > 0;
  }

  public boolean isNeutral(Entity e1, Entity e2)
  {
    return getReaction(e1, e2) == 0;
  }

  public boolean isLeader(Entity e, String faction)
  {
    return Mapper.identity.get(e).id.equals(faction);
  }

  public Entity leader(String faction)
  {
    return GameInfo.bestiary.get(faction);
  }

  public List<Entity> lackeys(String faction)
  {
    return allInFaction(faction).stream()
                                .filter(f -> !isLeader(f, faction))
                                .collect(Collectors.toList());
  }

  public List<Entity> allTeammates(Entity entity)
  {
    List<Entity> finalList = new ArrayList<>();
    Factions fac = Mapper.factions.get(entity);
    for (String faction : fac.factions)
    {
      finalList.addAll(allInFaction(faction));
    }

    return finalList.stream()
                    .distinct()
                    .collect(Collectors.toList());
  }

  public List<Coord> allTeammateCoords(Entity entity)
  {
    return allTeammates(entity).stream()
                               .map(m -> Mapper.position.get(m).current)
                               .collect(Collectors.toList());
  }
}
