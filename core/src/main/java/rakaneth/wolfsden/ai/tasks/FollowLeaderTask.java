package rakaneth.wolfsden.ai.tasks;

import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

import rakaneth.wolfsden.CommandTypes;
import rakaneth.wolfsden.FactionManager;
import rakaneth.wolfsden.WolfUtils;
import rakaneth.wolfsden.components.AI;
import rakaneth.wolfsden.components.Mapper;
import squidpony.squidai.DijkstraMap;
import squidpony.squidmath.Coord;

public class FollowLeaderTask extends LeafTask<Entity>
{

  @Override
  public Status execute()
  {
    Entity entity = getObject();
    String eID = Mapper.getID(entity);
    AI eAI = Mapper.ai.get(entity);
    Entity leader = eAI.leader();
    
    if (leader == null)
    {
      WolfUtils.log("AI", "%s cannot follow leader because leader is null", eID);
      return Status.FAILED;
    }
    
    String lID = Mapper.getID(leader);
    DijkstraMap dMap = eAI.dMap();
    Coord start = Mapper.position.get(entity).current;
    Coord leaderC = Mapper.position.get(leader).current;
    
    List<Coord> path = dMap.findPath(1, null, FactionManager.instance.allTeammateCoords(entity), start, leaderC);
    
    if (path.isEmpty())
    {
      WolfUtils.log("AI", "%s cannot follow %s; no path", eID, lID);
      return Status.FAILED;
    } else
    {
      Mapper.actions.get(entity).sendCmd(CommandTypes.MOVE, start.toGoTo(path.get(0)));
      return Status.SUCCEEDED;
    }
  }

  @Override
  protected Task<Entity> copyTo(Task<Entity> task)
  {
    return task;
  }

}
