package rakaneth.wolfsden.components;

import rakaneth.wolfsden.RKDice;

public class Mainhand extends Equipment
{
  public boolean dig;

  public Mainhand()
  {
    super();
  }

  public Mainhand(String name, String desc, RKDice atk, int def, RKDice dmg, int mov, int delay, int prot, boolean dig)
  {
    super(name, desc, atk, def, dmg, mov, delay, prot);
    this.dig = dig;
  }
}
