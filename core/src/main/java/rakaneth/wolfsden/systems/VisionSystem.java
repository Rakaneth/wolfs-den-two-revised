package rakaneth.wolfsden.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import rakaneth.wolfsden.components.AI;
import rakaneth.wolfsden.components.Mapper;
import rakaneth.wolfsden.components.Position;
import squidpony.squidgrid.FOV;

public class VisionSystem extends IteratingSystem
{
  public VisionSystem()
  {
    super(Family.all(AI.class, Position.class).get());
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime)
  {
    Position pos = Mapper.position.get(entity);
    AI ai = Mapper.AIs.get(entity);
    
    if (pos.dirty)
    {
      FOV.reuseFOV(pos.map.resistanceMap, ai.visible, pos.current.x, pos.current.y, ai.visionRadius);
    }
  }

}
