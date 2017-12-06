package rakaneth.wolfsden.ai.conditions;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

import rakaneth.wolfsden.WolfUtils;
import rakaneth.wolfsden.components.Mapper;

public class CanSeeLeaderCondition extends LeafTask<Entity>
{

  @Override
  public Status execute()
  {
    Entity entity = getObject();
    Entity leader = Mapper.ai.get(entity).leader();
    String eID = Mapper.getID(entity);
    if (leader == null)
    {
      WolfUtils.log("AI", "%s can't see leader because leader is null", eID);
      return Status.FAILED;
    }
    
    String lID = Mapper.getID(leader);
    if (Mapper.canSee(entity, leader))
    {
      WolfUtils.log("AI", "%s spots leader %s", eID, lID);
      return Status.SUCCEEDED;
    } else
    {
      WolfUtils.log("AI", "%s fails to spot leader %s", eID, lID);
      return Status.FAILED;
    }
  }

  @Override
  protected Task<Entity> copyTo(Task<Entity> task)
  {
    return task;
  }
  
}
