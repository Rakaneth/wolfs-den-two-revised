package rakaneth.wolfsden.ai.conditions;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

import rakaneth.wolfsden.CommandTypes;
import rakaneth.wolfsden.WolfUtils;
import rakaneth.wolfsden.components.AI;
import rakaneth.wolfsden.components.Mapper;

public class PreyAliveCondition extends LeafTask<Entity>
{

  @Override
  public Status execute()
  {
    Entity subject = getObject();
    AI ai = Mapper.ai.get(subject);
    Entity target = ai.creatureTarget();

    if (Mapper.vitals.get(target).alive)
    {
      WolfUtils.log("AI", "%s has target: %s", Mapper.getID(subject), Mapper.getID(target));
      return Status.SUCCEEDED;
    }
      

    WolfUtils.log("AI", "%s's target is dead", Mapper.getID(subject));
    ai.clearTarget();
    
    return Status.FAILED;
  }

  @Override
  protected Task<Entity> copyTo(Task<Entity> task)
  {
    return task;
  }

}
