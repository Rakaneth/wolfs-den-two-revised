package rakaneth.wolfsden.ai.conditions;

import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

import rakaneth.wolfsden.WolfUtils;
import rakaneth.wolfsden.components.AI;
import rakaneth.wolfsden.components.Mapper;

public class DetectFoodCondition extends LeafTask<Entity>
{

  @Override
  public Status execute()
  {
    Entity subject = getObject();
    List<Entity> foodList = Mapper.visibleFood(subject);

    if (foodList.size() > 0)
    {
      WolfUtils.log("AI", "%s smells food nearby", Mapper.getID(subject));
      return Status.SUCCEEDED;
    } else
    {
      WolfUtils.log("AI", "%s finds no food nearby.", Mapper.getID(subject));
      return Status.FAILED;  
    }
  }

  @Override
  protected Task<Entity> copyTo(Task<Entity> task)
  {
    return task;
  }

}
