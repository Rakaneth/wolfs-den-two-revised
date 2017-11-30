package rakaneth.wolfsden.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

import rakaneth.wolfsden.CommandTypes;
import rakaneth.wolfsden.FactionManager;
import rakaneth.wolfsden.components.AI;
import rakaneth.wolfsden.components.Action;
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
    Position tarPos = Mapper.position.get(ai.creatureTarget());
    List<Coord> packSquares = FactionManager.instance.allTeammateCoords(subject);
    List<Coord> path = ai.dMap()
                         .findPath(1, null, packSquares, subPos.current, tarPos.current);

    if (path.size() == 0)
      return Status.FAILED;

    act.sendCmd(CommandTypes.MOVE, subPos.current.toGoTo(path.get(0)));
    return Status.SUCCEEDED;
  }

  @Override
  protected Task<Entity> copyTo(Task<Entity> task)
  {
    return task;
  }

}
