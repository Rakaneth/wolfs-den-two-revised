package rakaneth.wolfsden.ai;

import java.util.Queue;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;

import rakaneth.wolfsden.CommandTypes;
import rakaneth.wolfsden.WolfUtils;
import rakaneth.wolfsden.components.AI;
import rakaneth.wolfsden.components.Mapper;
import rakaneth.wolfsden.components.Position;
import squidpony.squidmath.Coord;

public enum WolfState implements State<AI>
{
  FOLLOW_ALPHA()
  {
    @Override
    public void enter(AI ai)
    {
      WolfUtils.log("AI", "%s is now following leader", ai.eID);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void update(AI ai)
    {
      if (ai.leader == null)
        ai.stateMachine.changeState(WANDER);
      else
      {
        Position leaderPos = Mapper.position.get(ai.leader);
        Queue<Coord> path = ai.aStar.path(ai.location, leaderPos.current);
        if (path == null)
          ai.stateMachine.changeState(WANDER);
        else
        {
          if (path.size() == 1)
            ai.sendCmd(CommandTypes.WAIT);
          else
            ai.sendCmd(CommandTypes.MOVE, path.remove());
        }
      }
    }

    @Override
    public void exit(AI ai)
    {
      WolfUtils.log("AI", "%s is no longer following leader", ai.eID);
    }

    @Override
    public boolean onMessage(AI ai, Telegram tele)
    {
      return false;
    }
  },

  WANDER()
  {
    @Override
    public void enter(AI ai)
    {
      WolfUtils.log("AI", "%s is wandering", ai.eID);
    }

    @Override
    public void update(AI ai)
    {
      if (ai.leader != null && ai.visibleAllies.contains(ai.leader))
        ai.stateMachine.changeState(FOLLOW_ALPHA);
      else if (ai.visibleEnemies.size() > 0)
      {
        WolfUtils.log("AI", "%s detects prey", ai.eID);
        for (Entity enemy : ai.visibleEnemies)
        {
          WolfUtils.log("AI", "%s tells pack about %s", ai.eID, enemy.getComponent(AI.class).eID);
          MessageManager.getInstance()
                        .dispatchMessage(Messages.Wolf.SMELL_PREY, enemy);
        }
      }
      ai.sendCmd(CommandTypes.RANDOM);
    }

    @Override
    public void exit(AI ai)
    {
      WolfUtils.log("AI", "%s is not wandering", ai.eID);
    }

    @Override
    public boolean onMessage(AI ai, Telegram tele)
    {
      return false;
    }
  };

  public boolean onMessage(AI ai, Telegram tele)
  {
    return false;
  }
}
