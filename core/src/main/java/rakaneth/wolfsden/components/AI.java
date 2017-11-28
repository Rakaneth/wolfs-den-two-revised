package rakaneth.wolfsden.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.BehaviorTree;

public class AI implements Component
{
  private BehaviorTree<Entity> btree;

  public AI(BehaviorTree<Entity> btree)
  {
    this.btree = btree;
  }

  public BehaviorTree<Entity> btree()
  {
    return btree;
  }
}
