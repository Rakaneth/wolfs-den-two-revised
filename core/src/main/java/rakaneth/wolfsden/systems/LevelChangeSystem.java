package rakaneth.wolfsden.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import rakaneth.wolfsden.WolfMap;
import rakaneth.wolfsden.components.AI;
import rakaneth.wolfsden.components.ChangeLevel;
import rakaneth.wolfsden.components.Mapper;
import rakaneth.wolfsden.components.Position;
import rakaneth.wolfsden.screens.PlayScreen;
import squidpony.squidmath.GreasedRegion;

public class LevelChangeSystem extends IteratingSystem
{
  public LevelChangeSystem()
  {
    super(Family.all(ChangeLevel.class, Position.class, AI.class)
                .get());
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime)
  {
    Position pos = Mapper.position.get(entity);
    ChangeLevel lv = Mapper.changeLvl.get(entity);
    WolfMap.Connection headedTo = pos.map.getConnection(lv.from);
    AI ai = Mapper.AIs.get(entity);
  
    pos.map = headedTo.getMap();
    pos.current = headedTo.toC;
    ai.visible = ai.fov.calculateFOV(pos.map.resistanceMap, pos.current.x, pos.current.y, ai.visionRadius);
    ai.grVisible.remake(new GreasedRegion(ai.visible, 0.0).not());
    
    if (Mapper.isPlayer(entity))
      PlayScreen.instance.changeMap(pos.map);
    
    entity.remove(ChangeLevel.class);
    
  }

}
