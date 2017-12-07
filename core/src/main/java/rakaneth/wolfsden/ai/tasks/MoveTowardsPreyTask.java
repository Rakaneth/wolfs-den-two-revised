package rakaneth.wolfsden.ai.tasks;

import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

import rakaneth.wolfsden.CommandTypes;
import rakaneth.wolfsden.FactionManager;
import rakaneth.wolfsden.WolfUtils;
import rakaneth.wolfsden.components.AI;
import rakaneth.wolfsden.components.Action;
import rakaneth.wolfsden.components.Identity;
import rakaneth.wolfsden.components.Mapper;
import rakaneth.wolfsden.components.Position;
import squidpony.squidmath.Coord;

public class MoveTowardsPreyTask extends LeafTask<Entity>
{

  @Override
  public Status execute()
  {
    Entity subject = getObject();
    AI ai = Mapper.ai.get(subject);
    Action act = Mapper.actions.get(subject);
    Position subPos = Mapper.position.get(subject);
    Entity target = ai.creatureTarget();
    Identity id = Mapper.identity.get(subject);

    if (target == null)
    {
      act.sendCmd(CommandTypes.WAIT);
      WolfUtils.log("AI", "%s had to wait because target was lost", id.id);
      return Status.FAILED;
    }

    Position tarPos = Mapper.position.get(target);
    List<Coord> packSquares = FactionManager.instance.allTeammateCoords(subject);
    List<Coord> path = ai.dMap()
                         .findPath(1, null, packSquares, subPos.current, tarPos.current);

    if (path.size() == 0)
    {
      act.sendCmd(CommandTypes.WAIT);
      WolfUtils.log("AI", "%s had to wait because no path to target was found", id.id);
      return Status.FAILED;
    }

    act.sendCmd(CommandTypes.MOVE, subPos.current.toGoTo(path.get(0)));
    return Status.SUCCEEDED;
  }

  @Override
  protected Task<Entity> copyTo(Task<Entity> task)
  {
    return task;
  }

}
