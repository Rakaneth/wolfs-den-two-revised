package rakaneth.wolfsden.ai;

import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

import rakaneth.wolfsden.components.Mapper;

public class OnFoodCondition extends LeafTask<Entity>
{

  @Override
  public Status execute()
  {
    Entity subject = getObject();
    List<Entity> foodList = Mapper.visibleFood(subject);

    for (Entity food : foodList)
    {
      if (Mapper.isOn(subject, food))
      {
        Mapper.ai.get(subject)
                 .setTarget(food);
        return Status.SUCCEEDED;
      }
    }
    return Status.FAILED;
  }

  @Override
  protected Task<Entity> copyTo(Task<Entity> task)
  {
    return task;
  }
}
