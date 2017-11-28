package rakaneth.wolfsden.components;

import java.util.Stack;

import com.badlogic.ashley.core.Component;

import rakaneth.wolfsden.CommandTypes;

public class Action implements Component
{
  public Stack<Object> actionStack = new Stack<>();
  public boolean tookTurn;
  public int delay;
  
  public void sendCmd(CommandTypes cmd, Object...objects)
  {
    for (Object o: objects)
    {
      actionStack.push(o);
    }
    actionStack.push(cmd);
  }
}
