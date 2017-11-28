package rakaneth.wolfsden.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import rakaneth.wolfsden.GameInfo;
import rakaneth.wolfsden.WolfMap;
import rakaneth.wolfsden.components.ChangeLevel;
import rakaneth.wolfsden.components.Mapper;
import rakaneth.wolfsden.components.Position;
import rakaneth.wolfsden.components.Vision;
import rakaneth.wolfsden.screens.PlayScreen;
import squidpony.squidmath.GreasedRegion;

public class LevelChangeSystem extends IteratingSystem
{
  public LevelChangeSystem()
  {
    super(Family.all(ChangeLevel.class, Position.class, Vision.class)
                .get());
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime)
  {
    Position pos = Mapper.position.get(entity);
    ChangeLevel lv = Mapper.changeLvl.get(entity);
    WolfMap.Connection headedTo = pos.map.getConnection(lv.from);
    Vision vis = Mapper.vision.get(entity);

    pos.map = headedTo.getMap();
    pos.current = headedTo.toC;
    pos.dirty = true;
    GameInfo.hudDirty = true;
    vis.visible = vis.fov.calculateFOV(pos.map.resistanceMap, pos.current.x, pos.current.y, vis.visionRadius);
    vis.grVisible.remake(new GreasedRegion(vis.visible, 0.0).not());

    if (Mapper.isPlayer(entity))
    {
      PlayScreen.instance.changeMap(pos.map);
      Mapper.player.get(entity).grSeen.remake(vis.grVisible.copy());
    }

    entity.remove(ChangeLevel.class);
  }

}
