package rakaneth.wolfsden.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import rakaneth.wolfsden.components.AI;
import rakaneth.wolfsden.components.FreshCreature;
import rakaneth.wolfsden.components.Mapper;
import rakaneth.wolfsden.components.Position;
import rakaneth.wolfsden.components.Vitals;
import squidpony.squidmath.AStarSearch;
import squidpony.squidmath.AStarSearch.SearchType;
import squidpony.squidmath.GreasedRegion;

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
    //set creature to full HP/EP
    Vitals v = Mapper.vitals.get(entity);
    v.heal();
    v.rest();
    
    //set up vision
    AI ai = Mapper.AIs.get(entity);
    Position pos = Mapper.position.get(entity);
    ai.visible = ai.fov.calculateFOV(pos.map.resistanceMap, pos.current.x, pos.current.y, ai.visionRadius);
    ai.grVisible = new GreasedRegion(ai.visible, 0.0).not();
    
    //set up pathfinding
    ai.aStar = new AStarSearch(pos.map.aStarMap, SearchType.CHEBYSHEV);
    
    //add to atlas
    Position.atlas.put(ai.eID, pos);
    
    //remove component when done
    entity.remove(FreshCreature.class);
    
  }

}
