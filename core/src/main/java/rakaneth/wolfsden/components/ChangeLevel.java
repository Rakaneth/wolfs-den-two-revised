package rakaneth.wolfsden.components;

import com.badlogic.ashley.core.Component;

import rakaneth.wolfsden.WolfMap.Stairs;
import squidpony.squidmath.Coord;

public class ChangeLevel implements Component
{
  public Coord  from;
  public Stairs egress;

  public ChangeLevel(Coord from, Stairs egress)
  {
    this.from = from;
    this.egress = egress;
  }
}
