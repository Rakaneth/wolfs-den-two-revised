package rakaneth.wolfsden.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.systems.SortedIteratingSystem;

import rakaneth.wolfsden.components.Drawing;
import rakaneth.wolfsden.components.Mapper;
import rakaneth.wolfsden.components.Position;
import rakaneth.wolfsden.screens.PlayScreen;
import squidpony.squidgrid.gui.gdx.SparseLayers;
import squidpony.squidmath.Coord;

public class RenderingSystem extends SortedIteratingSystem
{
  private SparseLayers display;

  public RenderingSystem(PlayScreen screen, SparseLayers display)
  {
    super(Family.all(Drawing.class)
                .get(),
        (e1, e2) ->
        {
          return Mapper.drawing.get(e1).layer - Mapper.drawing.get(e2).layer;
        });
    this.display = display;
  }

  public void processEntity(Entity entity, float dt)
  {
    Position pos = Mapper.position.get(entity);
    if (pos.map.id.equals(PlayScreen.instance.map()))
    {
      Coord curPos = pos.current;
      Drawing dr = Mapper.drawing.get(entity);
      Coord cam = PlayScreen.instance.cam();
      double[][] visible = PlayScreen.instance.visible();
      if (visible[curPos.x][curPos.y] > 0.0)
      {
        display.put(curPos.x - cam.x, curPos.y - cam.y, dr.glyph, dr.color);
      }
    }
  }
}
