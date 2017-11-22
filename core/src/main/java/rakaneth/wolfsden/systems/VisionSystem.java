package rakaneth.wolfsden.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import rakaneth.wolfsden.FactionManager;
import rakaneth.wolfsden.components.AI;
import rakaneth.wolfsden.components.Mapper;
import rakaneth.wolfsden.components.Position;
import squidpony.squidgrid.FOV;

public class VisionSystem extends IteratingSystem
{
  public VisionSystem()
  {
    super(Family.all(AI.class, Position.class)
                .get());
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime)
  {
    Position pos = Mapper.position.get(entity);
    AI ai = Mapper.AIs.get(entity);

    if (pos.dirty)
    {
      FOV.reuseFOV(pos.map.resistanceMap, ai.visible, pos.current.x, pos.current.y, ai.visionRadius);
      ai.grVisible.refill(ai.visible, 0.0)
                  .not();
      ai.visibleEnemies.clear();
      ai.visibleAllies.clear();
      ai.visibleOthers.clear();
      FactionManager fm = FactionManager.instance;
      // TODO: filter between allies and enemies
      Mapper.atlas.entrySet()
                  .stream()
                  .filter((f) ->
                  {
                    Position fp = f.getValue();
                    return fp.map.id.equals(pos.map.id) && ai.grVisible.contains(fp.current);
                  })
                  .forEach((e) ->
                  {
                    Entity other = e.getKey();
                    if (fm.isEnemy(entity, other))
                      ai.visibleEnemies.add(other);
                    else if (fm.isAlly(entity, other))
                      ai.visibleAllies.add(other);
                    else
                      ai.visibleOthers.add(other);
                  });
    }
  }
}
