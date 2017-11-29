package rakaneth.wolfsden;

import java.util.HashMap;

import com.badlogic.ashley.core.Entity;

import rakaneth.wolfsden.components.Position;

public final class GameInfo
{
  public static int                             turnCount = 0;
  public static boolean                         paused;
  public static boolean                         hudDirty  = true;
  public static boolean                         mapDirty  = true;
  public static final HashMap<String, Entity>   catalog   = new HashMap<>();
  public static final HashMap<Entity, Position> atlas     = new HashMap<>();
  public static final HashMap<String, Entity>   bestiary  = new HashMap<>();
}
