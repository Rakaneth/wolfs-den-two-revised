package rakaneth.wolfsden.ai.tasks;

import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

import rakaneth.wolfsden.FactionManager;
import rakaneth.wolfsden.components.AI;
import rakaneth.wolfsden.components.Identity;
import rakaneth.wolfsden.components.Mapper;

public class CommandLackeysTask extends LeafTask<Entity>
{

  @Override
  public Status execute()
  {
    Entity subject = getObject();
    Identity id = Mapper.identity.get(subject);
    AI ai = Mapper.ai.get(subject);
    Entity alphaTarget = ai.creatureTarget();
    List<Entity> lackeys = FactionManager.instance.lackeys(id.id);
    Entity lackeyTarget = null;
    if (alphaTarget != null)
    {
      for (Entity lackey : lackeys)
      {
        AI lAI = Mapper.ai.get(lackey);
        lackeyTarget = lAI.creatureTarget();
        if (lackeyTarget != alphaTarget)
          lAI.setTarget(alphaTarget);
      }
      return Status.SUCCEEDED;
    }
    return Status.FAILED;
  }

  @Override
  protected Task<Entity> copyTo(Task<Entity> task)
  {
    return task;
  }
}
