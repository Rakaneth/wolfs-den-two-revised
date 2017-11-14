package rakaneth.wolfsden.components;

import rakaneth.wolfsden.RKDice;

public class Mainhand extends Equipment
{
	public Mainhand()
	{
		super();
	}
	
	public Mainhand(String name, String desc, RKDice atk, int def, RKDice dmg, int mov, int delay, int prot)
	{
		super(name, desc, atk, def, dmg, mov, delay, prot);
	}
}
