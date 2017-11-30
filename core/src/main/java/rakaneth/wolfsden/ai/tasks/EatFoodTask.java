package rakaneth.wolfsden.ai.tasks;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

import rakaneth.wolfsden.CommandTypes;
import rakaneth.wolfsden.components.AI;
import rakaneth.wolfsden.components.Action;
import rakaneth.wolfsden.components.Mapper;

public class EatFoodTask extends LeafTask<Entity>
{

  @Override
  public Status execute()
  {
    Entity subject = getObject();
    Action act = Mapper.actions.get(subject);
    AI ai = Mapper.ai.get(subject);

    act.sendCmd(CommandTypes.USE_SELF, ai.itemTarget());
    ai.clearTarget();
    return Status.SUCCEEDED;
  }

  @Override
  protected Task<Entity> copyTo(Task<Entity> task)
  {
    return task;
  }

}
