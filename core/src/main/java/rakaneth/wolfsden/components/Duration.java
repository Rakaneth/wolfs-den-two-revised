package rakaneth.wolfsden.components;

import com.badlogic.ashley.core.Component;

public class Duration implements Component
{
  public int duration;
  public String exitMsg;
  
  public Duration(int duration)
  {
    this.duration = duration;
  }
  
  public Duration(int duration, String exitMsg)
  {
    this.duration = duration;
    this.exitMsg = exitMsg;
  }
}
