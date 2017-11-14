package rakaneth.wolfsden.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import rakaneth.wolfsden.components.FreshCreature;
import rakaneth.wolfsden.components.Mapper;
import rakaneth.wolfsden.components.Vitals;

public class CreatureSetupSystem extends IteratingSystem
{
  public CreatureSetupSystem()
  {
    super(Family.all(FreshCreature.class)
                .get());
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime)
  {
    Vitals v = Mapper.vitals.get(entity);
    v.heal();
    v.rest();
    entity.remove(FreshCreature.class);
  }

}
