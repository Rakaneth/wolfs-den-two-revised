package rakaneth.wolfsden.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import rakaneth.wolfsden.components.Mapper;
import rakaneth.wolfsden.components.Player;
import rakaneth.wolfsden.components.Position;
import rakaneth.wolfsden.components.Vision;
import squidpony.squidgrid.FOV;

public class VisionSystem extends IteratingSystem
{
  public VisionSystem()
  {
    super(Family.all(Vision.class, Position.class)
                .get());
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime)
  {
    Position pos = Mapper.position.get(entity);
    Vision vis = Mapper.vision.get(entity);
    Player ply = Mapper.player.get(entity);

    if (pos.dirty)
    {
      FOV.reuseFOV(pos.map.resistanceMap, vis.visible, pos.current.x, pos.current.y, vis.visionRadius);
      vis.grVisible.refill(vis.visible, 0.0)
                   .not();

      if (ply != null && pos.map.isDark())
        ply.grSeen.or(vis.grVisible);
    }
  }
}
