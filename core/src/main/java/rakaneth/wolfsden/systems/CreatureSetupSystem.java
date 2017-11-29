package rakaneth.wolfsden.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import rakaneth.wolfsden.GameInfo;
import rakaneth.wolfsden.components.FreshCreature;
import rakaneth.wolfsden.components.Identity;
import rakaneth.wolfsden.components.Mapper;
import rakaneth.wolfsden.components.Player;
import rakaneth.wolfsden.components.Position;
import rakaneth.wolfsden.components.Vision;
import rakaneth.wolfsden.components.Vitals;
import squidpony.squidmath.GreasedRegion;

public class CreatureSetupSystem extends IteratingSystem
{
  public CreatureSetupSystem()
  {
    super(Family.all(FreshCreature.class, Vision.class, Position.class, Identity.class)
                .get());
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime)
  {
    // set creature to full HP/EP
    Vitals v = Mapper.vitals.get(entity);
    v.heal();
    v.rest();

    // set up vision
    Vision vis = Mapper.vision.get(entity);
    Position pos = Mapper.position.get(entity);
    Player ply = Mapper.player.get(entity);
    Identity id = Mapper.identity.get(entity);
    pos.dirty = true;
    vis.visible = vis.fov.calculateFOV(pos.map.resistanceMap, pos.current.x, pos.current.y, vis.visionRadius);
    vis.grVisible = new GreasedRegion(vis.visible, 0.0).not();
    if (ply != null)
    {
      ply.grSeen = vis.grVisible.copy();
    }

    // add to atlas
    GameInfo.atlas.put(entity, pos);

    // add to bestiary
    GameInfo.bestiary.put(id.id, entity);

    // remove component when done
    entity.remove(FreshCreature.class);

  }

}
