package rakaneth.wolfsden.components;

import java.util.Stack;

import com.badlogic.ashley.core.Component;

public class ActionStack implements Component
{
  public Stack<Object> cmds;
  public boolean       tookTurn;
  public int           delay;

  public ActionStack(int initialDelay)
  {
    cmds = new Stack<>();
    tookTurn = false;
    delay = initialDelay;
  }

  public ActionStack()
  {
    this(10);
  }
}
