package rakaneth.wolfsden.systems;

import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import rakaneth.wolfsden.CommandTypes;
import rakaneth.wolfsden.components.ActionStack;

public abstract class AIDecisionSystem extends IteratingSystem
{

  public AIDecisionSystem(Family family)
  {
    super(family);
  }

  protected void sendCmd(ActionStack as, CommandTypes cmd, Object... args)
  {
    for (Object arg : args)
    {
      as.cmds.push(arg);
    }
    as.cmds.push(cmd);
  }

}
