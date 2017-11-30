package rakaneth.wolfsden.ai.conditions;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

import rakaneth.wolfsden.components.Mapper;

public class PreyNullCondition extends LeafTask<Entity>
{

  @Override
  public Status execute()
  {
    Entity subject = getObject();
    if (Mapper.ai.get(subject)
                 .creatureTarget() == null)
      return Status.SUCCEEDED;

    return Status.FAILED;
  }

  @Override
  protected Task<Entity> copyTo(Task<Entity> task)
  {
    return task;
  }
}
