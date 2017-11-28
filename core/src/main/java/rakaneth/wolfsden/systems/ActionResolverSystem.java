package rakaneth.wolfsden.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;

import rakaneth.wolfsden.CommandTypes;
import rakaneth.wolfsden.FactionManager;
import rakaneth.wolfsden.GameInfo;
import rakaneth.wolfsden.WolfGame;
import rakaneth.wolfsden.WolfMap;
import rakaneth.wolfsden.WolfUtils;
import rakaneth.wolfsden.components.AI;
import rakaneth.wolfsden.components.Action;
import rakaneth.wolfsden.components.Attack;
import rakaneth.wolfsden.components.ChangeLevel;
import rakaneth.wolfsden.components.Identity;
import rakaneth.wolfsden.components.Mapper;
import rakaneth.wolfsden.components.Position;
import rakaneth.wolfsden.components.SecondaryStats;
import rakaneth.wolfsden.components.Stats;
import rakaneth.wolfsden.screens.PlayScreen;
import squidpony.squidgrid.Direction;
import squidpony.squidmath.Coord;

public class ActionResolverSystem extends SortedIteratingSystem
{
  public ActionResolverSystem()
  {
    super(Family.all(Stats.class, SecondaryStats.class, Action.class)
                .get(),
        (e1, e2) ->
        {
          return Mapper.stats.get(e2).spd - Mapper.stats.get(e1).spd;
        });
  }

  private void move(Position pos, Action act)
  {
    Direction d = (Direction) act.actionStack.pop();
    int newX = pos.current.x + d.deltaX;
    int newY = pos.current.y + d.deltaY;
    Coord newCoord = Coord.get(newX, newY);
    if (pos.map.isPassable(newCoord))
    {
      pos.dirty = true;
      pos.current = newCoord;
      GameInfo.mapDirty = true;
    }
    act.tookTurn = true;
  }

  private void moveRandom(Position pos, Action act)
  {
    act.actionStack.push(WolfGame.rng.getRandomElement(Direction.values()));
    move(pos, act);
  }

  private void swap(Entity e1, Entity e2)
  {
    Position e1p = Mapper.position.get(e1);
    Position e2p = Mapper.position.get(e2);
    Coord temp;
    temp = e1p.current;
    e1p.current = e2p.current;
    e2p.current = temp;
    GameInfo.mapDirty = true;
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime)
  {
    SecondaryStats sStats = Mapper.secondaries.get(entity);
    Action act = Mapper.actions.get(entity);
    Identity id = Mapper.identity.get(entity);
    AI ai = Mapper.ai.get(entity);
    if (!GameInfo.paused)
      act.delay--;

    if (act.delay <= 0)
    {
      if (ai != null)
        ai.btree()
          .step();

      if (Mapper.isPlayer(entity))
        GameInfo.paused = true;

      Position pos = Mapper.position.get(entity);
      pos.dirty = false;
      if (!act.actionStack.empty())
      {
        WolfUtils.log("Action", "%s acts on tick %d", id.id, GameInfo.turnCount);
        CommandTypes cmd = (CommandTypes) act.actionStack.pop();
        switch (cmd) {
        case MOVE:
          move(pos, act);
          act.delay = sStats.moveDelay;
          break;
        case STAIRS:
          WolfMap.Stairs stair = pos.map.getStair(pos.current);
          switch (stair) {
          case UP:
          case OUT:
          case DOWN:
            entity.add(new ChangeLevel(pos.current, stair));
            act.delay = 10;
            act.tookTurn = true;
            GameInfo.mapDirty = true;
            break;
          default:
            PlayScreen.addMessage("No stairs here.");
          }
          break;
        case RANDOM:
          moveRandom(pos, act);
          act.delay = sStats.moveDelay;
          break;
        case INTERACT:
          Entity other = (Entity) act.actionStack.pop();
          if (FactionManager.instance.isEnemy(entity, other))
          {
            entity.add(new Attack(other));
            act.tookTurn = true;
            act.delay = sStats.atkDelay;
            GameInfo.mapDirty = true;
          } else
          {
            // TODO: add logic for talky NPCs and such
            swap(entity, other);
            act.tookTurn = true;
            act.delay = sStats.moveDelay;
          }
          break;
        default:
          act.delay = 10;
          act.tookTurn = true;
        }
        GameInfo.paused = false;
      } else if (!GameInfo.paused)
      {
        // entity took no action due to empty stack
      }
    }
  }
}
