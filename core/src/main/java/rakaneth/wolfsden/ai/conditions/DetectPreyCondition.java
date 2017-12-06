package rakaneth.wolfsden.ai.conditions;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

import rakaneth.wolfsden.WolfUtils;
import rakaneth.wolfsden.components.Mapper;

public class DetectPreyCondition extends LeafTask<Entity>
{

  @Override
  public Status execute()
  {
    Entity subject = getObject();
    String subID = Mapper.getID(subject);
    if (Mapper.visibleEnemiesOf(subject)
              .size() > 0)
    {
      WolfUtils.log("AI", "%s detects prey", subID);
      return Status.SUCCEEDED;
    } else
    {
      WolfUtils.log("AI", "%s fails to detect prey", subID);
      return Status.FAILED;
    }
  }

  @Override
  protected Task<Entity> copyTo(Task<Entity> task)
  {
    return task;
  }
}
