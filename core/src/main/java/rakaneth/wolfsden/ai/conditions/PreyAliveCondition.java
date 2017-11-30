package rakaneth.wolfsden.ai.conditions;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

import rakaneth.wolfsden.components.Mapper;

public class PreyAliveCondition extends LeafTask<Entity>
{

  @Override
  public Status execute()
  {
    Entity subject = getObject();
    Entity target = Mapper.ai.get(subject)
                             .creatureTarget();

    if (Mapper.vitals.get(target).alive)
      return Status.SUCCEEDED;

    return Status.FAILED;
  }

  @Override
  protected Task<Entity> copyTo(Task<Entity> task)
  {
    return task;
  }

}
