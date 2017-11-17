package rakaneth.wolfsden.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import rakaneth.wolfsden.CommandTypes;
import rakaneth.wolfsden.WolfGame;
import rakaneth.wolfsden.WolfMap;
import rakaneth.wolfsden.components.Mapper;
import rakaneth.wolfsden.components.Player;
import rakaneth.wolfsden.components.AI;
import rakaneth.wolfsden.components.ChangeLevel;
import rakaneth.wolfsden.components.Position;
import rakaneth.wolfsden.components.SecondaryStats;
import rakaneth.wolfsden.screens.PlayScreen;
import squidpony.squidgrid.Direction;
import squidpony.squidmath.Coord;

public class ActionResolverSystem extends IteratingSystem
{
  private boolean paused;

  public ActionResolverSystem()
  {
    super(Family.all(SecondaryStats.class)
                .one(AI.class)
                .get());
  }

  private void move(Position pos, Direction d)
  {
    int newX = pos.current.x + d.deltaX;
    int newY = pos.current.y + d.deltaY;
    Coord newCoord = Coord.get(newX, newY);
    if (pos.map.isPassable(newCoord))
    {
      pos.dirty = true;
      pos.current = newCoord;
    }
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime)
  {
    SecondaryStats sStats = Mapper.secondaries.get(entity);
    AI ai = Mapper.AIs.get(entity);
    if (!paused)
      ai.delay -= 1;

    if (ai.delay <= 0)
    {
      if (ai.stateMachine != null)
        ai.stateMachine.update();

      if (Mapper.isPlayer(entity))
        paused = true;

      Position pos = Mapper.position.get(entity);
      if (!ai.actionStack.empty())
      {
        CommandTypes cmd = (CommandTypes) ai.actionStack.pop();
        switch (cmd) {
        case MOVE:
          move(pos, (Direction) ai.actionStack.pop());
          ai.location = pos.current;
          ai.tookTurn = true;
          ai.delay = sStats.moveDelay;
          break;
        case STAIRS:
          WolfMap.Stairs stair = pos.map.getStair(pos.current);
          switch (stair) {
          case UP:
          case OUT:
          case DOWN:
            entity.add(new ChangeLevel(pos.current, stair));
            break;
          default:
            PlayScreen.addMessage("No stairs here.");
          }
          break;
        case RANDOM:
          Direction d = WolfGame.rng.getRandomElement(Direction.values());
          move(pos, d);
          ai.tookTurn = true;
          ai.delay = sStats.moveDelay;
          break;
        default:
        }
        paused = false;
      } else if (!paused)
      {
        // logger.log(Level.WARNING, "{0} took no action due to empty stack",
        // entity.getComponent(Identity.class).id);
      }
    }
  }
}
