package rakaneth.wolfsden.ai;

import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

import rakaneth.wolfsden.components.AI;
import rakaneth.wolfsden.components.Mapper;

public class DetectFoodCondition extends LeafTask<Entity>
{

  @Override
  public Status execute()
  {
    Entity subject = getObject();
    AI ai = Mapper.ai.get(subject);
    List<Entity> foodList = Mapper.visibleFood(subject);

    if (foodList.size() > 0)
      return Status.SUCCEEDED;

    return Status.FAILED;
  }

  @Override
  protected Task<Entity> copyTo(Task<Entity> task)
  {
    return task;
  }

}
