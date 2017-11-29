package rakaneth.wolfsden.ai;

import java.util.HashMap;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

import rakaneth.wolfsden.components.AI;
import rakaneth.wolfsden.components.Mapper;
import rakaneth.wolfsden.components.Position;
import squidpony.squidai.DijkstraMap;
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
    DijkstraMap tempDMap = new DijkstraMap(pos.map.baseMap, DijkstraMap.Measurement.CHEBYSHEV);
    for (Entity enemy : Mapper.visibleEnemiesOf(subject))
    {
      tempMap.put(Mapper.position.get(enemy).current, enemy);
    }
    Coord targetC = tempDMap.findNearest(pos.current, tempMap.keySet());

    if (targetC == null)
      return Status.FAILED;

    ai.setTarget(tempMap.get(targetC));
    return Status.SUCCEEDED;
  }

  @Override
  protected Task<Entity> copyTo(Task<Entity> task)
  {
    return task;
  }

}
