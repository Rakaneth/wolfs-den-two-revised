package rakaneth.wolfsden.components;

import com.badlogic.ashley.core.Component;

import rakaneth.wolfsden.RKDice;

public class SecondaryStats implements Component
{
  public int    moveDelay;
  public int    atkDelay;
  public RKDice atk = new RKDice();
  public RKDice dmg = new RKDice();
  public int    def;
}
