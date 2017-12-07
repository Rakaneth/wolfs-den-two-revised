package rakaneth.wolfsden.ai.tasks;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

import rakaneth.wolfsden.CommandTypes;
import rakaneth.wolfsden.components.Mapper;

public class WaitTask extends LeafTask<Entity>
{

  @Override
  public Status execute()
  {
    Entity entity = getObject();
    Mapper.actions.get(entity)
                  .sendCmd(CommandTypes.WAIT);
    return Status.SUCCEEDED;
  }

  @Override
  protected Task<Entity> copyTo(Task<Entity> task)
  {
    return task;
  }
}
