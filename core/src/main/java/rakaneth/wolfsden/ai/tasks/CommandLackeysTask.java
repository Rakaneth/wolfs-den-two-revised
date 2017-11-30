package rakaneth.wolfsden.ai.tasks;

import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

import rakaneth.wolfsden.FactionManager;
import rakaneth.wolfsden.WolfUtils;
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
        {
          lAI.setTarget(alphaTarget);
          WolfUtils.log("AI", "%s changes targets to %s due to %s's orders", Mapper.getID(lackey),
                        Mapper.getID(alphaTarget), Mapper.getID(subject));
        }
      }
      return Status.SUCCEEDED;
    } else
    {
      WolfUtils.log("AI", "%s failed to order lackeys due to loss of target", Mapper.getID(subject));
      return Status.FAILED;
    }
  }

  @Override
  protected Task<Entity> copyTo(Task<Entity> task)
  {
    return task;
  }
}
