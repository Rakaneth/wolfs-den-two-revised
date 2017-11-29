package rakaneth.wolfsden.ai;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

import rakaneth.wolfsden.components.Mapper;
import rakaneth.wolfsden.components.Position;

public class PreyAdjacentCondition extends LeafTask<Entity>
{

  @Override
  public Status execute()
  {
    Entity subject = getObject();
    Position subPos = Mapper.position.get(subject);
    Position targetPos = Mapper.position.get(Mapper.ai.get(subject)
                                                      .target());

    if (subPos.current.isAdjacent(targetPos.current))
      return Status.SUCCEEDED;

    return Status.FAILED;
  }

  @Override
  protected Task<Entity> copyTo(Task<Entity> task)
  {
    return task;
  }

}
