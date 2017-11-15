package rakaneth.wolfsden.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

import rakaneth.wolfsden.CommandTypes;
import rakaneth.wolfsden.components.ActionStack;
import rakaneth.wolfsden.components.Mapper;
import rakaneth.wolfsden.components.WolfAI;

public class WolfDecisionSystem extends AIDecisionSystem
{
  public WolfDecisionSystem()
  {
    super(Family.all(WolfAI.class, ActionStack.class)
                .get());
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime)
  {
    // TODO: real wolfy decisions
    ActionStack as = Mapper.actions.get(entity);
    sendCmd(as, CommandTypes.RANDOM);
  }

}
