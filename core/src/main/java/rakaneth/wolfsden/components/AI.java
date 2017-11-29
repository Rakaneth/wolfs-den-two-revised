package rakaneth.wolfsden.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.BehaviorTree;

import rakaneth.wolfsden.GameInfo;

public class AI implements Component
{
  private BehaviorTree<Entity> btree;
  private String               target;

  public AI(BehaviorTree<Entity> btree)
  {
    this.btree = btree;
  }

  public BehaviorTree<Entity> btree()
  {
    return btree;
  }

  public Entity target()
  {
    return GameInfo.bestiary.get(target);
  }

  public void setTarget(Entity e)
  {
    target = Mapper.identity.get(e).id;
  }

  public void setTarget(String id)
  {
    target = id;
  }

  public void clearTarget()
  {
    target = null;
  }
}
