package rakaneth.wolfsden.ai.tasks;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

import rakaneth.wolfsden.FactionManager;
import rakaneth.wolfsden.WolfUtils;
import rakaneth.wolfsden.components.Mapper;

public class CallOffLackeysTask extends LeafTask<Entity>
{

  @Override
  public Status execute()
  {
    Entity entity = getObject();
    for (Entity lackey : FactionManager.instance.allTeammates(entity))
    {
      WolfUtils.log("AI", "%s calls off %s", Mapper.getID(entity), Mapper.getID(lackey));
      Mapper.ai.get(lackey)
               .clearTarget();
    }
    return Status.SUCCEEDED;
  }

  @Override
  protected Task<Entity> copyTo(Task<Entity> task)
  {
    return task;
  }

}
