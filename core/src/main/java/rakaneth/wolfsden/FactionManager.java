package rakaneth.wolfsden;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.ashley.core.Entity;

import rakaneth.wolfsden.components.Factions;
import rakaneth.wolfsden.components.Identity;
import rakaneth.wolfsden.components.Mapper;

public class FactionManager
{
  private Map<String, Map<String, Integer>> factions;
  public static final FactionManager        instance = new FactionManager();

  private FactionManager()
  {
    factions = new HashMap<>();
    changeReaction("monsters", "player", -100);
  }

  public Map<String, Integer> getReactions(String id)
  {
    return factions.get(id);
  }

  public void addFaction(String id)
  {
    if (factions.get(id) == null)
      factions.put(id, new HashMap<String, Integer>());
  }

  public void removeFaction(String id)
  {
    factions.remove(id);
  }

  public void addReaction(String faction, String target, int reaction)
  {
    addFaction(faction);
    factions.get(faction)
            .put(target, reaction);
  }

  public void removeReaction(String faction, String target)
  {
    Map<String, Integer> reactTable = getReactions(faction);

    if (reactTable == null)
      return;

    reactTable.remove(target);
  }

  public void changeReaction(String faction, String target, int amt)
  {
    addFaction(faction);
    Map<String, Integer> reactTable = getReactions(faction);

    Integer current = reactTable.get(target);

    if (current == null)
      addReaction(faction, target, amt);
    else
    {
      int total = current + amt;
      reactTable.replace(target, total);
    }
  }

  public int getReaction(String faction, String target)
  {
    Map<String, Integer> reactTable = getReactions(faction);
    if (reactTable == null)
      return 0;

    Integer current = reactTable.get(target);

    if (current == null)
      return 0;

    return current;
  }

  public int getReaction(Entity e1, Entity e2)
  {
    Factions fac1 = Mapper.factions.get(e1);
    Factions fac2 = Mapper.factions.get(e2);

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

  public boolean isEnemy(Entity e1, Entity e2)
  {
    int raw = getReaction(e1, e2);
    return raw < 0;
  }

  public boolean isAlly(Entity e1, Entity e2)
  {
    int raw = getReaction(e1, e2);
    return raw > 0;
  }

  public boolean isNeutral(Entity e1, Entity e2)
  {
    return !(isAlly(e1, e2) || isEnemy(e1, e2));
  }

}
