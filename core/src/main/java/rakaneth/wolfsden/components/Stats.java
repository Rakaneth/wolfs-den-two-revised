package rakaneth.wolfsden.components;

import com.badlogic.ashley.core.Component;

public class Stats implements Component
{
  public int str;
  public int stam;
  public int skl;
  public int spd;

  public Stats(int str, int stam, int spd, int skl)
  {
    this.str = str;
    this.stam = stam;
    this.spd = spd;
    this.skl = skl;
  }
}
