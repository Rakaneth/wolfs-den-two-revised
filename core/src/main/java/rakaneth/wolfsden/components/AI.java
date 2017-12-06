package rakaneth.wolfsden.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.BehaviorTree;

import rakaneth.wolfsden.GameInfo;
import squidpony.squidai.DijkstraMap;

public class AI implements Component
{
  private BehaviorTree<Entity> btree;
  private String               target;
  private DijkstraMap          dMap;
  private String leader;

  public AI(BehaviorTree<Entity> btree)
  {
    this.btree = btree;
  }

  public BehaviorTree<Entity> btree()
  {
    return btree;
  }
  
  public void setBTree(BehaviorTree<Entity> btree)
  {
    this.btree = btree;
  }

  public Entity creatureTarget()
  {
    return GameInfo.bestiary.get(target);
  }

  public Entity itemTarget()
  {
    return GameInfo.catalog.get(target);
  }
  
  public Entity leader()
  {
    return GameInfo.bestiary.get(leader);
  }

  public void setTarget(Entity e)
  {
    target = Mapper.identity.get(e).id;
  }

  public void setTarget(String id)
  {
    target = id;
  }
  
  public void setLeader(Entity leader)
  {
    this.leader = Mapper.getID(leader);
  }
  
  public void setLeader(String id)
  {
    leader = id;
  }

  public void clearTarget()
  {
    target = null;
  }

  public void setDMap(DijkstraMap dMap)
  {
    this.dMap = dMap;
  }

  public DijkstraMap dMap()
  {
    return dMap;
  }
}
