package rakaneth.wolfsden.components;

import com.badlogic.ashley.core.Component;

import rakaneth.wolfsden.RKDice;

public abstract class Equipment implements Component
{
  public String name;
  public String desc;
  public RKDice atk;
  public int    def;
  public RKDice dmg;
  public int    mov;
  public int    delay;
  public int    prot;

  public Equipment()
  {
    atk = new RKDice("0k0");
    def = 0;
    dmg = new RKDice("0k0");
    mov = 0;
    delay = 0;
    prot = 0;
    name = "No Name";
    desc = "No Desc";
  }

  public Equipment(String name, String desc, RKDice atk, int def, RKDice dmg, int mov, int delay, int prot)
  {
    this.name = name;
    this.desc = desc;
    this.atk = atk;
    this.def = def;
    this.dmg = dmg;
    this.mov = mov;
    this.delay = delay;
    this.prot = prot;
  }
}
