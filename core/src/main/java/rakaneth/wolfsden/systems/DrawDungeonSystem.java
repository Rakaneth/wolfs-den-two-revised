package rakaneth.wolfsden.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;

import rakaneth.wolfsden.components.Mapper;
import rakaneth.wolfsden.components.Position;
import rakaneth.wolfsden.screens.PlayScreen;
import squidpony.squidmath.GreasedRegion;

public class DrawDungeonSystem extends EntitySystem
{
  public DrawDungeonSystem() {}
  
  @Override
  public void update (float dt)
  {
    Entity player = PlayScreen.instance.player();
    Position pos = Mapper.position.get(player);
   
    if (pos.dirty)
    {
      PlayScreen.instance.clearDungeon();
      double[][] visible = Mapper.vision.get(player).visible;
      GreasedRegion seen = Mapper.player.get(player).grSeen;
      PlayScreen.instance.drawDungeon(visible, seen);
    }
  }
}
