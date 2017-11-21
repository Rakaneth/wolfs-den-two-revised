package rakaneth.wolfsden.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

public class Attack implements Component
{
  public Entity target;
  
  public Attack(Entity entity)
  {
    target = entity;
  }
}
