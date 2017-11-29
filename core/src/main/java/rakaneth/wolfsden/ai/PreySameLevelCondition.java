package rakaneth.wolfsden.ai;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

import rakaneth.wolfsden.components.AI;
import rakaneth.wolfsden.components.Mapper;

public class PreySameLevelCondition extends LeafTask<Entity>
{

  @Override
  public Status execute()
  {
    Entity subject = getObject();
    AI ai = Mapper.ai.get(subject);
    Entity target = ai.creatureTarget();

    if (Mapper.sameLevel(subject, target))
      return Status.SUCCEEDED;

    ai.clearTarget();
    return Status.FAILED;
  }

  @Override
  protected Task<Entity> copyTo(Task<Entity> task)
  {
    return task;
  }
}
