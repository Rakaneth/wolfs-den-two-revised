package rakaneth.wolfsden.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;

import rakaneth.wolfsden.CommandTypes;
import squidpony.squidgrid.FOV;
import squidpony.squidmath.AStarSearch;
import squidpony.squidmath.Coord;
import squidpony.squidmath.GreasedRegion;

public class AI implements Component
{
  public double[][]          visible;
  public FOV                 fov            = new FOV();
  public List<Entity>        visibleEnemies = new ArrayList<>();
  public List<Entity>        visibleAllies  = new ArrayList<>();
  public List<Entity> visibleOthers = new ArrayList<>();
  public Entity              target;
  public GreasedRegion       grVisible;
  public DefaultStateMachine stateMachine   = null;
  public double              visionRadius;
  public String              eID;
  public Entity              leader;
  public AStarSearch         aStar;
  public Coord               location;
  public Stack<Object>       actionStack    = new Stack<>();
  public int                 delay;
  public boolean             tookTurn;

  public AI(String eid)
  {
    eID = eid;
  }

  public void sendCmd(CommandTypes cmd, Object... args)
  {
    for (Object o : args)
    {
      actionStack.push(o);
    }
    actionStack.push(cmd);
  }
}
