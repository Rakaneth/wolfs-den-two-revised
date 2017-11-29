package rakaneth.wolfsden.ai;

import java.util.HashMap;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

import rakaneth.wolfsden.WolfUtils;
import rakaneth.wolfsden.components.AI;
import rakaneth.wolfsden.components.Identity;
import rakaneth.wolfsden.components.Mapper;
import rakaneth.wolfsden.components.Position;
import squidpony.squidmath.Coord;

public class GetTargetTask extends LeafTask<Entity>
{

  @Override
  public Status execute()
  {
    Entity subject = getObject();
    HashMap<Coord, Entity> tempMap = new HashMap<>();
    Position pos = Mapper.position.get(subject);
    AI ai = Mapper.ai.get(subject);
    Identity id = Mapper.identity.get(subject);

    for (Entity enemy : Mapper.visibleEnemiesOf(subject))
    {
      tempMap.put(Mapper.position.get(enemy).current, enemy);
    }
    Coord targetC = ai.dMap()
                      .findNearest(pos.current, tempMap.keySet());

    if (targetC == null)
    {
      ai.clearTarget();
      WolfUtils.log("AI", "%s fails to acquire a target", id.id);
    } else
    {
      ai.setTarget(tempMap.get(targetC));
      Identity tarID = Mapper.identity.get(ai.creatureTarget());
      WolfUtils.log("AI", "%s acquires a target: %s", id.id, tarID.id);
    }

    return Status.SUCCEEDED;
  }

  @Override
  protected Task<Entity> copyTo(Task<Entity> task)
  {
    return task;
  }

}
