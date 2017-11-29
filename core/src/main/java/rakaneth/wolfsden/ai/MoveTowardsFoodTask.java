package rakaneth.wolfsden.ai;

import java.util.List;
import java.util.stream.Collectors;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

import rakaneth.wolfsden.CommandTypes;
import rakaneth.wolfsden.components.AI;
import rakaneth.wolfsden.components.Action;
import rakaneth.wolfsden.components.Mapper;
import squidpony.squidmath.Coord;

public class MoveTowardsFoodTask extends LeafTask<Entity>
{

  @Override
  public Status execute()
  {
    Entity subject = getObject();
    AI ai = Mapper.ai.get(subject);
    Action act = Mapper.actions.get(subject);
    List<Entity> foodList = Mapper.visibleFood(subject);
    Coord start = Mapper.position.get(subject).current;

    if (foodList.size() == 0)
      return Status.FAILED;

    List<Coord> foodPoses = foodList.stream()
                                    .map(m -> Mapper.position.get(m).current)
                                    .collect(Collectors.toList());
    Coord nearest = ai.dMap()
                      .findNearest(start, foodPoses);
    List<Coord> nextSteps = ai.dMap()
                              .findPath(1, null, null, start, nearest);

    if (nextSteps.size() == 0)
      return Status.FAILED;

    act.sendCmd(CommandTypes.MOVE, start.toGoTo(nextSteps.get(0)));
    return Status.SUCCEEDED;
  }

  @Override
  protected Task<Entity> copyTo(Task<Entity> task)
  {
    // TODO Auto-generated method stub
    return task;
  }

}
