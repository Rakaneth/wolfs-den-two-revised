package rakaneth.wolfsden.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.MathUtils;

public class Vitals implements Component
{
  public boolean alive;
  public int     vit;
  public int     maxVit;
  public int     end;
  public int     maxEnd;
  public int     XP;
  public int     totXP;

  public Vitals()
  {
    alive = true;
  }

  public void gainXP(int amt)
  {
    XP += amt;
    totXP += amt;
  }

  public void takeDmg(int amt)
  {
    vit -= amt;
    if (vit < 0)
      alive = false;
  }

  public void heal(int amt)
  {
    vit = Math.min(amt + vit, maxVit);
  }

  public void heal()
  {
    vit = maxVit;
    alive = true;
  }

  public void changeEnd(int amt)
  {
    end = MathUtils.clamp(end + amt, 0, maxEnd);
  }

  public void rest()
  {
    end = maxEnd;
  }
}