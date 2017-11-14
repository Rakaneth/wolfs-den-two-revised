package rakaneth.wolfsden.components;

import rakaneth.wolfsden.RKDice;

public class Armor extends Equipment
{
	public Armor()
	{
		super();
	}
	
	public Armor(String name, String desc, RKDice atk, int def, RKDice dmg, int mov, int delay, int prot)
	{
		super(name, desc, atk, def, dmg, mov, delay, prot);
	}
}
