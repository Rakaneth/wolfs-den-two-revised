package rakaneth.wolfsden.ai.tasks;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

import rakaneth.wolfsden.CommandTypes;
import rakaneth.wolfsden.WolfUtils;
import rakaneth.wolfsden.components.Mapper;

public class RandomWalkTask extends LeafTask<Entity>
{

  @Override
  public Status execute()
  {
    Entity subject = getObject();
    Mapper.actions.get(subject)
                  .sendCmd(CommandTypes.RANDOM);
    WolfUtils.log("AI", "%s moves randomly", Mapper.getID(subject));
    return Status.SUCCEEDED;
  }

  @Override
  protected Task<Entity> copyTo(Task<Entity> task)
  {
    return task;
  }

}
