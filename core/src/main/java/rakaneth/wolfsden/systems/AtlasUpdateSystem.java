package rakaneth.wolfsden.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import rakaneth.wolfsden.components.Mapper;
import rakaneth.wolfsden.components.Position;

public class AtlasUpdateSystem extends IteratingSystem
{
  public AtlasUpdateSystem()
  {
    super(Family.all(Position.class)
                .get());
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime)
  {
    Position pos = Mapper.position.get(entity);
    if (Mapper.atlas.get(entity) == null)
      Mapper.atlas.put(entity, pos);
    else
      Mapper.atlas.replace(entity, pos);
  }
}
