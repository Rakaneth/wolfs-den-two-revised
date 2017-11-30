package rakaneth.wolfsden.ai.conditions;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

import rakaneth.wolfsden.components.AI;
import rakaneth.wolfsden.components.Mapper;
import rakaneth.wolfsden.components.Position;

public class PreyAdjacentCondition extends LeafTask<Entity>
{

  @Override
  public Status execute()
  {
    Entity subject = getObject();
    Position subPos = Mapper.position.get(subject);
    AI ai = Mapper.ai.get(subject);
    Position targetPos = null;
    
    if (ai.creatureTarget() != null)
      targetPos = Mapper.position.get(ai.creatureTarget());
    else
      return Status.FAILED;
    

    if (subPos.current.isAdjacent(targetPos.current))
      return Status.SUCCEEDED;
    else
      return Status.FAILED;
  }

  @Override
  protected Task<Entity> copyTo(Task<Entity> task)
  {
    return task;
  }

}
