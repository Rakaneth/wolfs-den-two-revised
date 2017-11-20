package rakaneth.wolfsden.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import rakaneth.wolfsden.CommandTypes;
import rakaneth.wolfsden.GameInfo;
import rakaneth.wolfsden.WolfGame;
import rakaneth.wolfsden.WolfMap;
import rakaneth.wolfsden.WolfUtils;
import rakaneth.wolfsden.components.Mapper;
import rakaneth.wolfsden.components.AI;
import rakaneth.wolfsden.components.ChangeLevel;
import rakaneth.wolfsden.components.Position;
import rakaneth.wolfsden.components.SecondaryStats;
import rakaneth.wolfsden.screens.PlayScreen;
import squidpony.squidgrid.Direction;
import squidpony.squidmath.Coord;

public class ActionResolverSystem extends IteratingSystem
{
  public static boolean paused;

  public ActionResolverSystem()
  {
    super(Family.all(SecondaryStats.class)
                .one(AI.class)
                .get());
  }

  private void move(Position pos, AI ai)
  {
    Direction d = (Direction) ai.actionStack.pop();
    int newX = pos.current.x + d.deltaX;
    int newY = pos.current.y + d.deltaY;
    Coord newCoord = Coord.get(newX, newY);
    if (pos.map.isPassable(newCoord))
    {
      pos.dirty = true;
      pos.current = newCoord;
    }
    ai.tookTurn = true;
    ai.location = pos.current;
  }
  
  private void moveRandom(Position pos, AI ai)
  {
    ai.actionStack.push(WolfGame.rng.getRandomElement(Direction.values()));
    move(pos, ai);
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime)
  {
    SecondaryStats sStats = Mapper.secondaries.get(entity);
    AI ai = Mapper.AIs.get(entity);
    if (!paused)
      ai.delay--;
    
    if (ai.delay <= 0)
    {
      if (ai.stateMachine != null)
        ai.stateMachine.update();

      if (Mapper.isPlayer(entity))
        paused = true;

      Position pos = Mapper.position.get(entity);
      pos.dirty = false;
      if (!ai.actionStack.empty())
      {
        WolfUtils.log("Action", "%s acts on tick %d", ai.eID, GameInfo.turnCount);
        CommandTypes cmd = (CommandTypes) ai.actionStack.pop();
        switch (cmd) {
        case MOVE:
          move(pos, ai);
          ai.delay = sStats.moveDelay;
          Position.atlas.replace(entity, pos);
          break;
        case STAIRS:
          WolfMap.Stairs stair = pos.map.getStair(pos.current);
          switch (stair) {
          case UP:
          case OUT:
          case DOWN:
            entity.add(new ChangeLevel(pos.current, stair));
            ai.delay = 10;
            ai.tookTurn = true;
            Position.atlas.replace(entity, pos);
            break;
          default:
            PlayScreen.addMessage("No stairs here.");
          }
          break;
        case RANDOM:
          moveRandom(pos, ai);
          Position.atlas.replace(entity, pos);
          ai.delay = sStats.moveDelay;
          break;
        default:
        }
        paused = false;
      } else if (!paused)
      {
        //entity took no action due to empty stack
      }
    }
  }
}
