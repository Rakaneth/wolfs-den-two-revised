package rakaneth.wolfsden.ai;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

import rakaneth.wolfsden.CommandTypes;
import rakaneth.wolfsden.WolfMap;
import rakaneth.wolfsden.components.Mapper;
import rakaneth.wolfsden.components.Position;
import squidpony.squidai.DijkstraMap;
import squidpony.squidmath.Coord;

public class MoveTowardsPreyTask extends LeafTask<Entity>
{
  private DijkstraMap dMap;

  @Override
  public Status execute()
  {
    Entity subject = getObject();
    Position pos = Mapper.position.get(subject);
    Coord curTarget = null;

    if (status == Status.FRESH)
      dMap = new DijkstraMap(pos.map.baseMap);

    List<Coord> targetCoords = new ArrayList<>();
    List<Entity> enemies = Mapper.visibleEnemiesOf(subject);
    if (enemies.size() == 0)
      return Status.FAILED;
    else
    {
      for (Entity enemy : enemies)
        targetCoords.add(Mapper.position.get(enemy).current);

      curTarget = dMap.findNearest(pos.current, targetCoords);
      List<Coord> path = dMap.findPath(1, null, null, pos.current, curTarget);
      Coord nextStep = path.get(0);

      if (pos.current.isAdjacent(nextStep))
        return Status.SUCCEEDED;
      else
      {
        Mapper.actions.get(subject)
                      .sendCmd(CommandTypes.MOVE, pos.current.toGoTo(nextStep));
        return Status.RUNNING;
      }
    }
  }

  @Override
  protected Task<Entity> copyTo(Task<Entity> task)
  {
    return task;
  }

  public void changeMap(WolfMap map)
  {
    dMap.initialize(map.baseMap);
  }

}
