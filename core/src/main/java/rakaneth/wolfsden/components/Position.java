package rakaneth.wolfsden.components;

import java.util.HashMap;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

import rakaneth.wolfsden.WolfMap;
import squidpony.squidmath.Coord;

public class Position implements Component
{
  public Coord   current;
  public Coord   prev;

  public WolfMap map;
  public boolean dirty;
  public static final HashMap<Entity, Position> atlas = new HashMap<>();

  public Position(Coord startPoint, WolfMap map)
  {
    current = startPoint;
    prev = startPoint;
    this.map = map;
    dirty = false;
  }
}
