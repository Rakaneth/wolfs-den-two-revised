package rakaneth.wolfsden.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import rakaneth.wolfsden.GameInfo;
import rakaneth.wolfsden.Swatch;
import rakaneth.wolfsden.components.Action;
import rakaneth.wolfsden.components.Drawing;
import rakaneth.wolfsden.components.Identity;
import rakaneth.wolfsden.components.Mapper;
import rakaneth.wolfsden.components.Position;
import rakaneth.wolfsden.components.Vitals;
import rakaneth.wolfsden.screens.PlayScreen;

public class EndStepSystem extends EntitySystem
{
  private ImmutableArray<Entity> entities;

  public EndStepSystem()
  {
  }

  @Override
  public void addedToEngine(Engine engine)
  {
    entities = engine.getEntitiesFor(Family.all(Action.class, Position.class, Vitals.class)
                                           .get());
  }

  @Override
  public void removedFromEngine(Engine engine)
  {
    entities = engine.getEntitiesFor(Family.all(Action.class, Position.class, Vitals.class)
                                           .get());
  }

  @Override
  public boolean checkProcessing()
  {
    return !GameInfo.paused;
  }

  @Override
  public void update(float dt)
  {
    for (int i = 0; i < entities.size(); ++i)
    {
      Entity entity = entities.get(i);
      Position pos = Mapper.position.get(entity);
      Vitals vit = Mapper.vitals.get(entity);
      Action act = Mapper.actions.get(entity);
      Drawing draw = Mapper.drawing.get(entity);
      Identity id = Mapper.identity.get(entity);

      act.tookTurn = false;
      if (GameInfo.turnCount % 1000 == 0 && GameInfo.turnCount > 0 && Mapper.isPlayer(entity))
      {
        vit.gainXP(vit.xpMult);
        vit.xpMult = Math.max(vit.xpMult - 0.5f, 0.1f);
      }
      // TODO: death cleanup
      if (!vit.alive && !Mapper.isPlayer(entity))
      {
        PlayScreen.addMessage("[%s]%s[] has been [%s]slain![]", draw.color.getName(), id.name, Swatch.VIT);
        PlayScreen.engine.removeEntity(entity);
      }
    }
    GameInfo.turnCount++;
  }
}
