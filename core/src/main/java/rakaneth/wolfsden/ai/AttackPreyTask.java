package rakaneth.wolfsden.ai;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

import rakaneth.wolfsden.CommandTypes;
import rakaneth.wolfsden.components.Action;
import rakaneth.wolfsden.components.Mapper;

public class AttackPreyTask extends LeafTask<Entity>
{

  @Override
  public Status execute()
  {
    Entity subject = getObject();
    Entity target = Mapper.ai.get(subject)
                             .creatureTarget();
    Action act = Mapper.actions.get(subject);
    act.sendCmd(CommandTypes.ATTACK, target);
    return Status.SUCCEEDED;
  }

  @Override
  protected Task<Entity> copyTo(Task<Entity> task)
  {
    return task;
  }

}
