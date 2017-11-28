package rakaneth.wolfsden.components;

import com.badlogic.ashley.core.Component;

import squidpony.squidgrid.FOV;
import squidpony.squidmath.GreasedRegion;

public class Vision implements Component
{
  public double[][]    visible;
  public GreasedRegion grVisible;
  public double        visionRadius;
  public FOV           fov = new FOV();

  public Vision(double vision)
  {
    visionRadius = vision;
  }
}
